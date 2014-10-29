package conveyor.api;

import java.util.HashSet;

public interface ConsumerRegistry {

	Long register(Long consumerId, Long groupId);

	Long unregister(Long consumerId);

	HashSet<Long> getLeasedGroupIds();

}