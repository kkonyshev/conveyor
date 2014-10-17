package conveyor;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		private static final String LOGGER_NAME_WORKER 		= "WORKER";
		
		public static Logger SYSTEM 	= LoggerFactory.getLogger(LOGGER_NAME_SYSTEM);
		public static Logger DISPATCHER = LoggerFactory.getLogger(LOGGER_NAME_DISPATCHER);
		public static Logger WORKER 	= LoggerFactory.getLogger(LOGGER_NAME_WORKER);
	}
	
	public static Long randLong(int min, int max) {
	    Random rand = new Random();
	    return new Long(rand.nextInt((max - min) + 1) + min);
	}
}
