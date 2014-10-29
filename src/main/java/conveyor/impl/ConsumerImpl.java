package conveyor.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import conveyor.LockedLinkedList;
import conveyor.Utils;
import conveyor.api.Consumer;
import conveyor.api.ConsumerRegistry;
import conveyor.api.Item;
import conveyor.api.LockedQuque;

public class ConsumerImpl implements Consumer<Item> {

	private static final String NO_ELEMENTS_IN_GROUP = "no elements in group [";
	
	private static Logger logger = Utils.Logging.DISPATCHER;
	
	@Autowired
	private LockedQuque lockedQuque;
	
	@Autowired
	private ConsumerRegistry consumerRegistry;
	
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
		LockedLinkedList<Item> groupList = lockedQuque.createGroupIfNotExists(groupId);
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
				lockedQuque.removeItemListByGroupId(groupId);
			}
			return item;
		} finally {
			groupList.unlock();
		}
	}

	@Override
	public Long leaseGroupId(Long consumerId) throws InterruptedException {
		lockedQuque.getLeaseLock().lock();
		try {
			Long groupId = null;
			while (groupId==null) {
				Long prevReservedGroupId = consumerRegistry.unregister(consumerId);
				logger.debug("requesting group for processor [" + consumerId + "], prevGroupId is " + prevReservedGroupId);
				HashSet<Long> freeGroupIds = createGroupIdsForLeasing();
				logger.debug("requesting group for processor [" + consumerId + "], freeGroupIds is " + freeGroupIds);
				if (freeGroupIds.isEmpty()) {
					logger.debug("requesting group for processor [" + consumerId + "], waiting");
					lockedQuque.getFreeGroupAvailable().await();
					continue;
				}
				freeGroupIds.remove(prevReservedGroupId);
				if (freeGroupIds.isEmpty()) {
					logger.debug("requesting group for processor [" + consumerId + "], use prevGroupId");
					groupId = prevReservedGroupId;
				} else {
					Random randomGenerator = new Random();
				    int index = randomGenerator.nextInt(freeGroupIds.size());
				    logger.debug("requesting group for processor [" + consumerId + "], renerated random index " + index);
					groupId = (Long) freeGroupIds.toArray()[index];
					logger.debug("requesting group for processor [" + consumerId + "], new groupId is " + groupId);
				}
				consumerRegistry.register(consumerId, groupId);
			}
			return groupId;
		} finally {
			lockedQuque.getLeaseLock().unlock();
		}
	}

	@Override
	public Boolean hasNextItem(Long groupId) {
		LockedLinkedList<Item> groupList = lockedQuque.createGroupIfNotExists(groupId);
		groupList.lock();
		boolean empty = groupList.isEmpty();
		try {
			return !empty;
		} finally {
			if (empty) {
				lockedQuque.removeItemListByGroupId(groupId);
			}
			groupList.unlock();
		}
	}

	@Override
	public void markItemFailed(Item item) {
		logger.error("Item marked as failed: " + item);
	}

	
	/**/
	
	protected HashSet<Long> createGroupIdsForLeasing() {
		Set<Long> avialableGroupIds = new HashSet<Long>(lockedQuque.getGroupIds());
		Collection<Long> leasedGroupIds = consumerRegistry.getLeasedGroupIds();
		avialableGroupIds.removeAll(leasedGroupIds);
		HashSet<Long> freeGroupIds = new HashSet<Long>(avialableGroupIds);
		return freeGroupIds;
	}
	
}
