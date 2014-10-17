package conveyor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;
import conveyor.api.Dispatcher;
import conveyor.api.Item;
import conveyor.dto.ItemDto;
import conveyor.impl.DispatcherImpl;
import conveyor.impl.ThreadProcessorImpl;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Gate implements SparkApplication {
    
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
	
	private Dispatcher<Item> dispatcher;
	
	private Configuration cfg;
    private static Integer portNumber = 8082;
    private static Integer threadNumber = 2;
    
    public static void main(String[] args) throws IOException {
    	if (args.length != 0) {
    		for (String arg: args) {
	        	if (arg.startsWith("-p")) {
	        		String value = arg.substring(2);
	        		try {
						Integer portValue = Integer.valueOf(value);
	        			if (portValue>0) {
	        				portNumber = portValue;
	        			} else {
	        				throw new IllegalAccessException();
	        			}
	        		} catch (Exception e) {
	        			Utils.Logging.SYSTEM.info("using default port number (" + portNumber + ") instead of: " + value);
	        		}
	       		} else if (arg.startsWith("-t")) {
	       			String value = arg.substring(2);
	        		try {
	        			Integer threadValue = Integer.valueOf(value);
	        			if (threadValue>0) {
	        				threadNumber = threadValue;
	        			} else {
	        				throw new IllegalAccessException();
	        			}
	        		} catch (Exception e) {
	        			Utils.Logging.SYSTEM.info("using default thread number (" + threadNumber + ") instead of: " + value);
	        		}
	       		} else if (arg.startsWith("-h")) {
	       			Utils.Logging.SYSTEM.info("usage: [-pPORT_NUMBER -tTHREAD_NUMBER]");
	       			Utils.Logging.SYSTEM.info("example: -p8080 -t4");
	       		}
    		}
        }
    	new Gate();
    }

    public Gate() throws IOException {
    	init();
    }

	public void init() {
        cfg = createFreemarkerConfiguration();
        spark.Spark.setPort(portNumber);
        
        dispatcher = (DispatcherImpl)applicationContext.getBean("dispatcher");
        
        ExecutorService executors = Executors.newFixedThreadPool(threadNumber);
        for (Integer threadCount=0; threadCount<threadNumber; threadCount++) {
        	ThreadProcessorImpl bean = (ThreadProcessorImpl)applicationContext.getBean("threadProcessor");
        	bean.setProcessorId(threadCount.longValue());
			executors.submit(bean);
        }
        executors.shutdown();
        try {
			initializeRoutes();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
    
    abstract class FreemarkerBasedRoute extends Route {
        final Template template;

        /**
         * Constructor
         *
         * @param path The route path which is used for matching. (e.g. /hello, users/:name)
         */
        protected FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);
        }

        @Override
        public Object handle(Request request, Response response) {
            StringWriter writer = new StringWriter();
            try {
                doHandle(request, response, writer);
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/error");
            }
            return writer;
        }

        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;

    }

    private void initializeRoutes() throws IOException {
    	initBusinessRoutes();
    }

	private void initBusinessRoutes() throws IOException {
		
		spark.Spark.get(
    			new FreemarkerBasedRoute("/rnd/:total", "rnd.ftl") {
		            @Override
		            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
		            	SimpleHash root = new SimpleHash();
		            	
		            	Integer total = 5;
		            	
		            	Object totalObject = request.params(":total");
		            	try {
		            		total = Integer.valueOf(totalObject.toString());
		            		if (total<=0) {
		            			throw new Exception();
		            		}
		            	} catch (Exception e) {
		            		e.printStackTrace();
		            	}
		            	
			            Collection<Item> itemList = new ArrayList<Item>();
		            	for (int count=0; count<total; count++) {
			            	Long itemGroupId  = Utils.randLong(0, 5);
			        		Long randomItemId = Utils.randLong(0, 150);
			        		itemList.add(new ItemDto(itemGroupId, randomItemId));
			        	}
		            	for (Item i: itemList) {
		            		dispatcher.addItem(i);
		            	}
			            root.put("items", itemList.toString());
		            	template.process(root, writer);
		            }
    			}
    	);
		
    	spark.Spark.get(
    			new FreemarkerBasedRoute("/add/:groupId/:id", "add.ftl") {
		            @Override
		            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
		            	SimpleHash root = new SimpleHash();
		            	
		            	Object groupIdObject = request.params(":groupId");
		            	Object idObject = request.params(":id");
		            	if (groupIdObject!=null && idObject!=null) {
			            	try {
			            		Long groupId = Long.valueOf(groupIdObject.toString());
			            		Long id = Long.valueOf(idObject.toString());
			            		Item i = new ItemDto(groupId, id);
			            		dispatcher.addItem(i);
			            		root.put("item", i);
			            	} catch (Exception e) {
			            		e.printStackTrace();
			            		root.put("item", "Error: " + e.getMessage());
			            	}
		            	}
		            	
		            	template.process(root, writer);
		            }
    			}
    	);
    	
    	spark.Spark.get(
    			new FreemarkerBasedRoute("/error", "error.ftl") {
    				@Override
    				protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
    					SimpleHash root = new SimpleHash();
    					
    					root.put("error", "System has encountered an error.");
    					template.process(root, writer);
    				}
    			}
    			);
	}


    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(Gate.class, "/freemarker");
        return retVal;
    }
}
