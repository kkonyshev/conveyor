package conveyor.test;

import org.junit.Assert;
import org.junit.Test;

import conveyor.Utils;
import conveyor.impl.ItemImpl;

/**
 * TODO В процессе эволюции объекта и необходимости не забыть переопределить hashCode/equals 
 * 
 * @author kkonyshev
 *
 */
public class HashCodeTest {

	@Test
	public void testHashCode() {
		for (int i=0; i<1000; i++) {
			Long groupId = Utils.randLong(1, 100);
			Long itemId = Utils.randLong(1, 100);
			ItemImpl dto1 = new ItemImpl(itemId, groupId);
			ItemImpl dto2 = new ItemImpl(itemId, groupId);
			Assert.assertNotSame(dto1.hashCode(), dto2.hashCode());
		}
	}
}
