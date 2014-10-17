package conveyor.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import conveyor.LockedLinkedList;
import conveyor.Utils;
import conveyor.api.Dispatcher;
import conveyor.api.Item;

/**
 * Реализация обработчика
 * 
 * @author kkonyshev
 *
 */
public class DispatcherImpl implements Dispatcher<Item> {

	private static final String NO_ELEMENTS_IN_GROUP = "no elements in group [";
	private static final String ADDING_TO_QUEUE = "adding to queue: ";
	private static final String BAD_ITEM_GROUP_ID_IS_NULL = "bad item: groupId is null";

	private static Logger logger = Utils.Logging.DISPATCHER;
	
	/**
	 * Очередь элементов на обработку по разбитая по группам
	 */
	private Map<Long, LockedLinkedList<Item>> queueMap = new ConcurrentHashMap<Long, LockedLinkedList<Item>>();
	
	/**
	 * идентификатор обработчика --> идентификатор зарезервированной группы
	 */
	private Map<Long, Long> consumerToGroupRegistry = new ConcurrentHashMap<Long, Long>();
	
	/**
	 * Блокировка резервирования группы обработки
	 */
	private Lock 		leaseLock 			= new ReentrantLock();
	
	/**
	 * Условие появления свободных для резервирования групп
	 */
	private Condition 	freeGroupAvailable 	= leaseLock.newCondition();
	
	
	
	private Lock		groupMapLock		= new ReentrantLock();

	/* Produces API */
	
	@Override
	public void addItem(Item item) {
		Long groupId = item.getGroupId();
		if (groupId==null) {
			logger.error(BAD_ITEM_GROUP_ID_IS_NULL);
			throw new IllegalArgumentException(BAD_ITEM_GROUP_ID_IS_NULL);
		}
				
		LockedLinkedList<Item> groupList = createGroupIfNotExists(groupId);
		groupList.lock();
		try {
			logger.info(ADDING_TO_QUEUE + item);
			groupList.add(item);
			Collections.sort(groupList);
			groupList.emptyCondition().signalAll();
		} finally {
			groupList.unlock();
		}

		leaseLock.lock();
		try {
			HashSet<Long> leasingGroup = createGroupIdsForLeasing();
			if (!leasingGroup.isEmpty()) {
				freeGroupAvailable.signalAll();
			}
		} finally {
			leaseLock.unlock();
		}
	}

	protected LockedLinkedList<Item> createGroupIfNotExists(Long groupId) {
		groupMapLock.lock();
		try {
			LockedLinkedList<Item> groupList = queueMap.get(groupId);
			if (groupList==null) {
				groupList = new LockedLinkedList<Item>();
				queueMap.put(groupId, groupList);
			}
			return groupList;
		} finally {
			groupMapLock.unlock();
		}
	}

	/* Consumer API */

	/**
	 * Метод получения следующего в группе элемента для обработки.
	 * Если в группе нет элементов поток засыпает на 1 сек.
	 * Если в течении этого времени новые элементы не появились, выбрасывается исключение InterruptedException
	 * 
	 * @param groupId
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public Item getNext(Long groupId) throws InterruptedException {
		LockedLinkedList<Item> groupList = createGroupIfNotExists(groupId);
		groupList.lock();
		try {
			if (groupList.isEmpty()) {
				boolean waitRes = groupList.emptyCondition().await(1, TimeUnit.SECONDS);
				if (!waitRes) {
					logger.error(NO_ELEMENTS_IN_GROUP + groupId + "]");
					throw new InterruptedException(NO_ELEMENTS_IN_GROUP + groupId + "]");
				}
			}
			Item item = groupList.pollFirst();
			logger.info("pooling from queue: " + item);
			if (groupList.isEmpty()) {
				queueMap.remove(groupId);
			}
			return item;
		} finally {
			groupList.unlock();
		}
	}

	@Override
	public Long leaseGroupId(Long consumerId) throws InterruptedException {
		leaseLock.lock();
		try {
			Long groupId = null;
			Long prevReservedGroupId = consumerToGroupRegistry.remove(consumerId);
			HashSet<Long> freeGroupIds = createGroupIdsForLeasing();
			if (freeGroupIds.isEmpty()) {
				freeGroupAvailable.await();
				freeGroupIds = createGroupIdsForLeasing();
			}
			freeGroupIds.remove(prevReservedGroupId);
			if (freeGroupIds.isEmpty()) {
				groupId = prevReservedGroupId;
			} else {
				Random randomGenerator = new Random();
			    int index = randomGenerator.nextInt(freeGroupIds.size());
				groupId = (Long) freeGroupIds.toArray()[index];
			}
			consumerToGroupRegistry.put(consumerId, groupId);
			return groupId;
		} finally {
			leaseLock.unlock();
		}
	}

	protected HashSet<Long> createGroupIdsForLeasing() {
		Set<Long> avialableGroupIds = new HashSet<Long>(queueMap.keySet());
		Collection<Long> leasedGroupIds = consumerToGroupRegistry.values();
		avialableGroupIds.removeAll(leasedGroupIds);
		HashSet<Long> freeGroupIds = new HashSet<Long>(avialableGroupIds);
		return freeGroupIds;
	}

	@Override
	public Boolean hasNextItem(Long groupId) {
		LockedLinkedList<Item> groupList = createGroupIfNotExists(groupId);
		groupList.lock();
		boolean empty = groupList.isEmpty();
		try {
			return !empty;
		} finally {
			if (empty) {
				queueMap.remove(groupId);
			}
			groupList.unlock();
		}
	}

	@Override
	public void markItemFailed(Item item) {
		logger.error("Item marked as failed: " + item);
	}
}
