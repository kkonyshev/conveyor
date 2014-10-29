package conveyor.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import conveyor.LockedLinkedList;
import conveyor.Utils;
import conveyor.api.ConsumerRegistry;
import conveyor.api.Item;
import conveyor.api.LockedQuque;
import conveyor.api.Producer;

public class ProducerImpl implements Producer<Item> {

	private static final String ADDING_TO_QUEUE = "adding to queue: ";
	private static final String BAD_ITEM_GROUP_ID_IS_NULL = "bad item: groupId is null";

	private static Logger logger = Utils.Logging.DISPATCHER;
	
	@Autowired
	private LockedQuque lockedQuque;
	
	@Autowired
	private ConsumerRegistry consumerRegistry;
	
	@Override
	public void addItem(Item item) {
		Long groupId = item.getGroupId();
		if (groupId==null) {
			logger.error(BAD_ITEM_GROUP_ID_IS_NULL);
			throw new IllegalArgumentException(BAD_ITEM_GROUP_ID_IS_NULL);
		}
				
		LockedLinkedList<Item> groupList = lockedQuque.createGroupIfNotExists(groupId);
		groupList.lock();
		try {
			logger.info(ADDING_TO_QUEUE + item);
			groupList.add(item);
			Collections.sort(groupList);
			groupList.emptyCondition().signal();
		} finally {
			groupList.unlock();
		}

		lockedQuque.getLeaseLock().lock();
		try {
			HashSet<Long> leasingGroup = createGroupIdsForLeasing();
			if (!leasingGroup.isEmpty()) {
				lockedQuque.getFreeGroupAvailable().signal();
			}
		} finally {
			lockedQuque.getLeaseLock().unlock();
		}
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
