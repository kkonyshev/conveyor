package conveyor.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import conveyor.api.ConsumerRegistry;

public class ConsumerRegistryImpl implements ConsumerRegistry {
	/**
	 * идентификатор обработчика --> идентификатор зарезервированной группы
	 */
	private Map<Long, Long> consumerToGroupRegistry = new ConcurrentHashMap<Long, Long>();
	
	@Override
	public Long register(Long consumerId, Long groupId) {
		return consumerToGroupRegistry.put(consumerId, groupId);
	}
	
	@Override
	public Long unregister(Long consumerId) {
		return consumerToGroupRegistry.remove(consumerId);
	}
	
	@Override
	public HashSet<Long> getLeasedGroupIds() {
		return new HashSet<Long>(consumerToGroupRegistry.values());
	}
}
