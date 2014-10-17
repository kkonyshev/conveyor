package conveyor;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LinkedList с локом
 * 
 * @author kkonyshev
 *
 * @param <I>
 */
public class LockedLinkedList<I> extends LinkedList<I> {
	
	private static final long serialVersionUID = 1L;
	private Lock lock = new ReentrantLock();
	private Condition isEmpty  = lock.newCondition();
	
	/**
	 * {@link java.util.concurrent.locks.Lock#lock()}
	 */
	public void lock() {
		lock.lock();
	}
	
	/**
	 * {@link java.util.concurrent.locks.Lock#unlock()}
	 */
	public void unlock() {
		lock.unlock();
	}
	
	/**
	 * Условие отсутствия элементов в списке
	 * 
	 * @return
	 */
	public Condition emptyCondition() {
		return isEmpty;
	}
}
