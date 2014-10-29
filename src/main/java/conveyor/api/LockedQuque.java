package conveyor.api;

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import conveyor.LockedLinkedList;

public interface LockedQuque {

	LockedLinkedList<Item> getItemList(Long groupId);

	void putItemList(Long groupId, LockedLinkedList<Item> itemList);

	LockedLinkedList<Item> removeItemListByGroupId(Long groupId);

	Set<Long> getGroupIds();
	
	Lock getLeaseLock();

	Condition getFreeGroupAvailable();

	LockedLinkedList<Item> createGroupIfNotExists(Long groupId);
}