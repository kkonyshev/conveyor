package conveyor.test;

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
public class DispatcherSingleGroupTest {

	@Autowired
	private Dispatcher<Item> dispatcherProto;
	
	@Test
	public void testSingleItemQueueGroupExist() throws InterruptedException {
		Long initGroupId = 1L;
		Long consumerId  = 1L;
		dispatcherProto.addItem(new ItemDto(initGroupId, 1L));
		
		Long groupIdToWork = dispatcherProto.leaseGroupId(consumerId);
		Assert.assertTrue("Ожидается наличие элемента для обрабоки", dispatcherProto.hasNextItem(groupIdToWork));
	}
}
