package conveyor;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import conveyor.impl.DispatcherImpl;
import conveyor.impl.ThreadProcessorImpl;

/**
 * Вспомогательный класс
 * 
 * @author kkonyshev
 *
 */
public abstract class Utils {
	
	public static class Logging {
		
		private static final String LOGGER_NAME_SYSTEM 		= "SYSTEM";
		private static final String LOGGER_NAME_DISPATCHER 	= "DISPATCHER";
		private static final String LOGGER_NAME_PROCESSING 	= "PROCESSING";
		
		public static Logger SYSTEM 	= LoggerFactory.getLogger(LOGGER_NAME_SYSTEM);
		public static Logger DISPATCHER = LoggerFactory.getLogger(LOGGER_NAME_DISPATCHER);
		public static Logger PROCESSING = LoggerFactory.getLogger(LOGGER_NAME_PROCESSING);
	}
	
	public static Long randLong(int min, int max) {
	    Random rand = new Random();
	    return new Long(rand.nextInt((max - min) + 1) + min);
	}
	
	public static class Context {
		private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
		
		public static DispatcherImpl getDispatcher() {
			return (DispatcherImpl)applicationContext.getBean("dispatcher");
		}
		
		public static ThreadProcessorImpl createThreadProcessorInstance() {
			return (ThreadProcessorImpl)applicationContext.getBean("threadProcessor");
		}
	}
}
