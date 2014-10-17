package conveyor.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.Utils;
import conveyor.api.Dispatcher;
import conveyor.api.Item;
import conveyor.dto.ItemDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class DispatcherQueueWorkTest {

	@Autowired
	private Dispatcher<Item> dispatcher;
	
	@Autowired
	private Thread consumer;
	
	@Test
	public void testWork() {
		Long itemGroupId = 0L;
		for (int count=0; count<10; count++) {
			Long gandomItemId = Utils.randLong(0, 150);
			dispatcher.addItem(new ItemDto(itemGroupId, gandomItemId));
		}
		
		consumer.start();
		for (int count=0; count<10; count++) {
			Long randomItemId = Utils.randLong(0, 150);
			dispatcher.addItem(new ItemDto(itemGroupId, randomItemId));
		}
		try {
			Thread.sleep(1000);
			consumer.interrupt();
		} catch (InterruptedException ex) {
			
		}
	}
}
