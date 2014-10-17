package conveyor.test;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.api.Dispatcher;
import conveyor.api.Item;
import conveyor.dto.ItemDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class DispatcherQueueSortingTest {

	@Autowired
	private Dispatcher<Item> dispatcher;
	
	@Test
	public void testSortItem() throws InterruptedException {
		Long itemGroupId = 0L;
		for (int count=0; count<10; count++) {
			Long gandomItemId = randLong(0, 150);
			dispatcher.addItem(new ItemDto(itemGroupId, gandomItemId));
		}
		
		Long consumerId = 2L;
		Long groupId = dispatcher.leaseGroupId(consumerId);
		
		Long prevousId = dispatcher.getNext(groupId).getId();
		while (dispatcher.hasNextItem(groupId)) {
			Item i = dispatcher.getNext(groupId);
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
