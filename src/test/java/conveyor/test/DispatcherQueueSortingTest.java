package conveyor.test;

import java.util.Random;

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
@ContextConfiguration(locations = {"classpath:application-context-test-sorting.xml"})
public class DispatcherQueueSortingTest {

	@Autowired
	private Producer<Item> producer;
	
	@Autowired
	private Consumer<Item> consumer;
	
	@Test
	public void testSortItem() throws InterruptedException {
		Long itemGroupId = 0L;
		for (int count=0; count<10; count++) {
			Long gandomItemId = randLong(0, 150);
			producer.addItem(new ItemImpl(itemGroupId, gandomItemId));
		}
		
		Long consumerId = 2L;
		Long groupId = consumer.leaseGroupId(consumerId);
		
		Long prevousId = consumer.getNext(groupId).getId();
		while (consumer.hasNextItem(groupId)) {
			Item i = consumer.getNext(groupId);
			Long currentId = i.getId();
			Assert.assertTrue("Ожидается " + currentId + ">" + prevousId, currentId.compareTo(prevousId)>=0);
			prevousId = i.getId();
		}
	}
	
	private Long randLong(int min, int max) {
	    Random rand = new Random();
	    return new Long(rand.nextInt((max - min) + 1) + min);
	}
}
