package conveyor.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.api.Item;
import conveyor.api.Producer;
import conveyor.impl.ItemImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class ProducerIllegalItemTest {

	@Autowired
	private Producer<Item> dispatcher;

	@Test(expected=IllegalArgumentException.class)
	public void testDispatcherBadElementNoGroupIdItemId() throws InterruptedException {
		dispatcher.addItem(new ItemImpl(null, null));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDispatcherBadElementNoGroupId() throws InterruptedException {
		dispatcher.addItem(new ItemImpl(null, 1L));
	}
}
