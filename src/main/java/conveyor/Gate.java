package conveyor;

import java.io.IOException;

/**
 *  Runner
 * 
 * @author kkonyshev
 *
 */
public class Gate {

	/* параметры комманжной строки запуска */
	
	private static final String PARAM_KEY_H = "-h";
	private static final String PARAM_KEY_T = "-t";
	private static final String PARAM_KEY_P = "-p";
	
	/* сообщения */
	
	private static final String USING_DEFAULT_THREAD_NUMBER = "using default thread number (";
	private static final String USING_DEFAULT_PORT_NUMBER 	= "using default port number (";
	private static final String INSTEAD_OF 					= ") instead of: ";
	
	/* описание использования */
	
	private static final String USAGE_LINE_1 = "usage: [-pPORT_NUMBER -tTHREAD_NUMBER]";
	private static final String USAGE_LINE_2 = "example: -p8080 -t4";

	public static void main(String[] args) throws IOException {
    	Integer portNumber = FrontImpl.DEFAULT_PORT_NUMBER;
    	Integer threadNumber = AppImpl.DEFAULT_THREAD_NUMBER;
    	Boolean startApp = Boolean.TRUE;
    	if (args.length != 0) {
    		for (String arg: args) {
	        	if (arg.startsWith(PARAM_KEY_P)) {
	        		String value = arg.substring(2);
	        		try {
						Integer portValue = Integer.valueOf(value);
	        			if (portValue>0) {
	        				portNumber = portValue;
	        			} else {
	        				throw new IllegalAccessException();
	        			}
	        		} catch (Exception e) {
	        			Utils.Logging.SYSTEM.info(USING_DEFAULT_PORT_NUMBER + FrontImpl.DEFAULT_PORT_NUMBER + INSTEAD_OF + value);
	        		}
	       		} else if (arg.startsWith(PARAM_KEY_T)) {
	       			String value = arg.substring(2);
	        		try {
	        			Integer threadValue = Integer.valueOf(value);
	        			if (threadValue>0) {
	        				threadNumber = threadValue;
	        			} else {
	        				throw new IllegalAccessException();
	        			}
	        		} catch (Exception e) {
	        			Utils.Logging.SYSTEM.info(USING_DEFAULT_THREAD_NUMBER + AppImpl.DEFAULT_THREAD_NUMBER + INSTEAD_OF + value);
	        		}
	       		} else if (arg.startsWith(PARAM_KEY_H)) {
	       			startApp = Boolean.FALSE;
	       			System.out.println(USAGE_LINE_1);
	       			System.out.println(USAGE_LINE_2);
	       		}
    		}
        }
    	if (startApp) {
    		new FrontImpl(new AppImpl(threadNumber), portNumber);
    	}
    }
}
