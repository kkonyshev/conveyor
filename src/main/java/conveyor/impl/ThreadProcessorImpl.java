package conveyor.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import conveyor.Utils;
import conveyor.api.Consumer;
import conveyor.api.Item;
import conveyor.api.ThreadProcessor;

/**
 * Реализация потока обработки очереди
 * 
 * @author kkonyshev
 *
 */
public class ThreadProcessorImpl extends Thread implements ThreadProcessor<Item> {
	
	private static final String CONSUMER = "Processor [";

	public static final int DEFAULT_ITEM_COUNT_THRESHOLD = 5;

	private Logger logger = Utils.Logging.PROCESSING;
	
	
	@Autowired
	private Consumer<Item> dispatcher;
	
	/**
	 * Идентификатор обрабатываемой группы
	 */
	private Long groupId;
	
	/**
	 * Количество обработанных элементов после последнего запроса группы обработки
	 */
	private Integer processedItemCount = 0;
	
	/**
	 * Счетчик обработанных элементов для смены группы 
	 */
	private Integer itemCountThreshold;
	
	/**
	 * Общее количество обработанных элементов
	 */
	private Integer totalProcessedCount = 0;
	
	public ThreadProcessorImpl() {
		this(DEFAULT_ITEM_COUNT_THRESHOLD);
	}
	
	public ThreadProcessorImpl(Integer itemCountThreshold) {
		this.itemCountThreshold = itemCountThreshold;
		logger.info(CONSUMER + getProcessotId() + "]: Initing. Item count threshold is: " + itemCountThreshold);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (processedItemCount>=itemCountThreshold || groupId==null) {
					logger.info(CONSUMER + getProcessotId() + "]: release new groupId");
					leaseGroupId();
				}
				if (dispatcher.hasNextItem(groupId)) {
					process(dispatcher.getNext(groupId));
				} else {
					logger.info(CONSUMER + getProcessotId() + "]: no more items in group: " + groupId);
					leaseGroupId();
				}
			} catch (InterruptedException e) {
				logger.error(CONSUMER + getProcessotId() + "]: interrupted", e);
				//TODO send onAfter die event
			}
		}
	}

	/**
	 * Метод запрашивает у диспетчера идентификатор группы для обработки и сбрасывает счетчик обработанных элементов из прошлой группы. 
	 * 
	 * @throws InterruptedException
	 */
	private void leaseGroupId() throws InterruptedException {
		logger.info(CONSUMER + getProcessotId() + "]: releasing new groupId to process");
		this.groupId = dispatcher.leaseGroupId(getProcessotId());
		logger.info(CONSUMER + getProcessotId() + "]: reserving - " + groupId);
		processedItemCount = 0;
	}

	/**
	 * Метод обработки элемента очереди
	 * 
	 * @param item
	 */
	protected void process(Item item) {
		logger.info(CONSUMER + getProcessotId() + "]: processing item - " + item);
		try {
			totalProcessedCount++;
			StringBuilder sb = new StringBuilder();
			sb.append(getProcessotId()).append(":").append(item).append("/").append(totalProcessedCount);
			System.out.println(sb.toString());
			processedItemCount++;
			//Thread.sleep(100);
		} catch (Exception e) {
			logger.error(CONSUMER + getProcessotId() + "]: item marked as filed - " + item);
			dispatcher.markItemFailed(item);
		}
	}

	@Override
	public Integer getItemCountThreshold() {
		return itemCountThreshold;
	}

	@Override
	public void setItemCountThreshold(Integer itemCountThreshold) {
		this.itemCountThreshold = itemCountThreshold;
	}

	public Long getProcessotId() {
		return getId();
	}

	public Integer getTotalProcessedCount() {
		return totalProcessedCount;
	}
}
