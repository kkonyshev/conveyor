package conveyor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import conveyor.api.Item;
import conveyor.api.Producer;
import conveyor.impl.ThreadProcessorImpl;

/**
 * Реазилация для использования в рамках тестового web-приложения
 * 
 * @author kkonyshev
 *
 */
public class AppImpl implements Producer<Item> {
	
	public static Integer DEFAULT_THREAD_NUMBER = 2;    
    
	private Producer<Item> dispatcher;
	private ExecutorService executors;
	
	private Integer threadNumber;
	
	public AppImpl() {
		this(DEFAULT_THREAD_NUMBER);
	}
	
	public AppImpl(Integer threadNumber) {
		this.threadNumber = threadNumber;
		init();
	}
	
	/**
	 * Инициализация пула обработчиков и диспетчера
	 * 
	 */
	//TODO redefine pool size online
	public void init() {
		dispatcher = Utils.Context.getDispatcher();
		executors = Executors.newFixedThreadPool(threadNumber);
        for (Integer threadCount=0; threadCount<threadNumber; threadCount++) {
        	ThreadProcessorImpl bean = Utils.Context.createThreadProcessorInstance();
			executors.submit(bean);
        }
        executors.shutdown();
	}

	@Override
	public void addItem(Item item) {
		dispatcher.addItem(item);
	}
}
