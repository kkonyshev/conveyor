package conveyor.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import conveyor.LockedLinkedList;
import conveyor.api.Item;
import conveyor.api.LockedQuque;

public class LockedQuqueImpl implements LockedQuque {
	/**
	 * Очередь элементов на обработку по группам
	 */
	private Map<Long, LockedLinkedList<Item>> queueMap = new ConcurrentHashMap<Long, LockedLinkedList<Item>>();
	
	
	/**
	 * Блокировка резервирования группы обработки
	 */
	private Lock 		leaseLock 			= new ReentrantLock(true);
	
	/**
	 * Условие появления свободных для резервирования групп
	 */
	private Condition 	freeGroupAvailable 	= leaseLock.newCondition();	
	
	private Lock		groupMapLock		= new ReentrantLock();
	
	@Override
	public LockedLinkedList<Item> getItemList(Long groupId) {
		return queueMap.get(groupId);
	}
	
	@Override
	public void putItemList(Long groupId, LockedLinkedList<Item> itemList) {
		queueMap.put(groupId, itemList);
	}
	
	@Override
	public LockedLinkedList<Item> removeItemListByGroupId(Long groupId) {
		return queueMap.remove(groupId);
	}
	
	@Override
	public Set<Long> getGroupIds() {
		return new HashSet<Long>(queueMap.keySet());
	}

	public Lock getLeaseLock() {
		return leaseLock;
	}

	public Condition getFreeGroupAvailable() {
		return freeGroupAvailable;
	}

	public LockedLinkedList<Item> createGroupIfNotExists(Long groupId) {
		groupMapLock.lock();
		try {
			LockedLinkedList<Item> groupList = getItemList(groupId);
			if (groupList==null) {
				groupList = new LockedLinkedList<Item>();
				putItemList(groupId, groupList);
			}
			return groupList;
		} finally {
			groupMapLock.unlock();
		}
	}
}
