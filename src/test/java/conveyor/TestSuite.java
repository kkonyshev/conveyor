package conveyor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import conveyor.test.ConsumerIllegalItemTest;
import conveyor.test.DispatcherQueueWorkTest;
import conveyor.test.ProducerIllegalItemTest;
import conveyor.test.DispatcherSingleGroupTest;
import conveyor.test.DispatcherSingleItemTest;
import conveyor.test.DispatcherQueueSortingTest;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		ProducerIllegalItemTest.class,
		ConsumerIllegalItemTest.class, 
		DispatcherSingleGroupTest.class,
		DispatcherSingleItemTest.class, 
		DispatcherQueueSortingTest.class,
		DispatcherQueueWorkTest.class
})
public class TestSuite {
}
