package conveyor.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.Utils;
import conveyor.api.Item;
import conveyor.api.Producer;
import conveyor.impl.ItemImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test-process.xml"})
public class DispatcherQueueWorkTest {

	@Autowired
	private Producer<Item> producer;
	
	@Autowired
	private Thread threadProcessor;
	
	@Test
	public void testWork() {
		Long itemGroupId = 0L;
		for (int count=0; count<10; count++) {
			Long gandomItemId = Utils.randLong(0, 150);
			producer.addItem(new ItemImpl(itemGroupId, gandomItemId));
		}
		
		threadProcessor.start();
		for (int count=0; count<10; count++) {
			Long randomItemId = Utils.randLong(0, 150);
			producer.addItem(new ItemImpl(itemGroupId, randomItemId));
		}
		try {
			Thread.sleep(1000);
			threadProcessor.interrupt();
		} catch (InterruptedException ex) {
			
		}
	}
}
