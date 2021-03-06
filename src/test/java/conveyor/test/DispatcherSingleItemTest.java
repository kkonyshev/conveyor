package conveyor.test;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.api.Consumer;
import conveyor.api.Item;
import conveyor.api.Producer;
import conveyor.impl.ItemImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test-single-group.xml"})
public class DispatcherSingleItemTest {

	@Autowired
	private Producer<Item> producer;
	
	@Autowired
	private Consumer<Item> consumer;
	
	@Test
	public void testSingleItemQueueItemExist() throws InterruptedException {
		Long initGroupId = 1L;
		Long consumerId  = 1L;
		producer.addItem(new ItemImpl(initGroupId, 1L));

		Long groupIdToWork = consumer.leaseGroupId(consumerId);
		Assert.assertEquals("Ожидается наличие единственной группы", initGroupId, groupIdToWork);
	}
}
