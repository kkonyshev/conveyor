package conveyor;

import java.util.ArrayList;
import java.util.Collection;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;
import conveyor.api.Item;
import conveyor.dto.ItemDto;

/**
 * Описание web-сервисов для добавления элементов в очередь обработки
 * 
 * @author kkonyshev
 *
 */
public class FrontImpl implements SparkApplication {

	/* request URLs */
	
	public static final String URL_POST_CREATE_BATCH 	= "/create/batch/";
    public static final String URL_POST_CREATE_SINGLE 	= "/create/";
    public static final String URL_GET_CREATE 			= "/";
	
    /*request query params*/
    
    private static final String QUERY_PARAM_BATCH_SIZE	 = "batchSize";
    private static final String QUERY_PARAM_GROUP_ID 	= "groupId";
    private static final String QUERY_PARAM_ID 			= "id";
    
    /* default param values */
    
    private static final Integer DEFAULT_BATCH_CREATE_SIZE 	= 5;
    public static final Integer DEFAULT_PORT_NUMBER 		= 8082;
    
    private AppImpl service;
    
	public FrontImpl(AppImpl service, Integer portNumber) {
		this.service = service;

		spark.Spark.setPort(portNumber);
		init();
	}
	
	/**
	 * Сервисы
	 * 
	 */
    @Override
    public void init() {
    	/**
    	 * Массовое добавление элементов в очередь.
    	 * Параметры: "batchSize" -- количество добавляемых в очередь элементов (по-умолчанию: FrontImpl.DEFAULT_BATCH_CREATE_SIZE=5)
    	 */
		spark.Spark.post(
    			new Route(URL_POST_CREATE_BATCH) {
					@Override
					public Object handle(Request request, Response response) {
		            	Integer batchSize = DEFAULT_BATCH_CREATE_SIZE;
		            	
		            	Object inputBatchSize = request.queryParams(QUERY_PARAM_BATCH_SIZE);
		            	try {
		            		batchSize = Integer.valueOf(inputBatchSize.toString());
		            		if (batchSize<=0) {
		            			throw new Exception();
		            		}
		            	} catch (Exception e) {
		            	}
		            	
			            Collection<Item> itemList = new ArrayList<Item>();
		            	for (int count=0; count<batchSize; count++) {
			            	Long itemGroupId  = Utils.randLong(0, 5);
			        		Long randomItemId = Utils.randLong(0, 150);
			        		itemList.add(new ItemDto(itemGroupId, randomItemId));
			        	}
		            	for (Item i: itemList) {
		            		service.addItem(i);
		            	}
		            	
		            	return itemList.toString();
					}
    			}
    	);
		
		/**
		 * Добавление в очередь нового элемента
		 * Параметры: идентификатор группы -- "groupId", идентификатор элемента -- "id"
		 */
    	spark.Spark.post(
    			new Route(URL_POST_CREATE_SINGLE) {
					@Override
					public Object handle(Request request, Response response) {
						try {
			            	Object groupIdObject = request.queryParams(QUERY_PARAM_GROUP_ID);
			            	Object idObject = request.queryParams(QUERY_PARAM_ID);
			            	Long groupId = Long.valueOf(groupIdObject.toString());
			            	Long id = Long.valueOf(idObject.toString());
			            	Item i = new ItemDto(groupId, id);
			            	service.addItem(i);
			            	return i;
			            } catch (Exception e) {
			            	return e.getMessage();
			            }
					}
    			}
    	);
    	
    	/**
    	 * Веб форма для добавления элементов в очередь
    	 */
    	spark.Spark.get(
    			new Route(URL_GET_CREATE) {
					@Override
					public Object handle(Request request, Response response) {		            	
						 StringBuilder form = new StringBuilder();
						 form
						 .append("<p>Create single item</p>")
						 .append("<form id='add-item' method='POST' action='").append(URL_POST_CREATE_SINGLE).append("'>")
						 .append("Group id: <input type='text' name='").append(QUERY_PARAM_GROUP_ID).append("'/>")
						 .append("Item id: <input type='text' name='").append(QUERY_PARAM_ID).append("'/>")
						 .append("<input type='submit' value='add item' form='add-item' />")
						 .append("</form>")
						 .append("<hr/>")
						 .append("<p>Creatae batch item</p>")
						 .append("<form id='add-batch' method='POST' action='").append(URL_POST_CREATE_BATCH).append("'>")
						 .append("Add batch random item. Batch size<input type='text' name='").append(QUERY_PARAM_BATCH_SIZE).append("'/>")
						 .append("<input type='submit' value='add batch' form='add-batch' />")
						 .append("</form>");
						 return form.toString();
					}
    			}
    	);
	}
}
