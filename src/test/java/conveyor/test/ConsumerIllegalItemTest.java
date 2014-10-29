package conveyor.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import conveyor.api.Consumer;
import conveyor.api.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@Ignore
public class ConsumerIllegalItemTest {

	@Autowired
	private Consumer<Item> consumer;
	
	/**
	 * Проверяемое исключение выбрасываемое по таймауту 
	 * 
	 * @throws InterruptedException
	 */
	@Test(expected=InterruptedException.class)
	public void testDispatcherNoMoreElementInGroup() throws InterruptedException {
		consumer.getNext(-1L);
	}
}
