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
	
	private static final int DEFAULT_ITEM_COUNT_THRESHOLD = 5;

	private Logger logger = Utils.Logging.WORKER;
	
	private Long processorId;
	
	@Autowired
	private Consumer<Item> dispatcher;
	
	private Long groupId;
	private Integer processedItemCount = 0;
	private Integer itemCountThreshold;
	
	public ThreadProcessorImpl() {
		this(DEFAULT_ITEM_COUNT_THRESHOLD);
	}
	
	public ThreadProcessorImpl(Integer itemCountThreshold) {
		this.itemCountThreshold = itemCountThreshold;
		this.processorId = getId();
		logger.info("Consumer [" + getProcessotId() + "]: Initing. Item count threshold is: " + itemCountThreshold);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (processedItemCount>=itemCountThreshold || groupId==null) {
					leaseGroupId();
				}
				if (dispatcher.hasNextItem(groupId)) {
					process(dispatcher.getNext(groupId));
				} else {
					leaseGroupId();
				}
			} catch (InterruptedException e) {
				logger.error("Consumer [" + getProcessotId() + "]: interrupted", e);
			}
		}
	}

	private void leaseGroupId() throws InterruptedException {
		logger.info("Consumer [" + getProcessotId() + "]: releasing new groupId to process");
		this.groupId = dispatcher.leaseGroupId(getProcessotId());
		logger.info("Consumer [" + getProcessotId() + "]: reserving - " + groupId);
		processedItemCount = 0;
	}

	/**
	 * Метод обработки элементов очереди
	 * 
	 * @param item
	 */
	protected void process(Item item) {
		logger.info("Consumer [" + getProcessotId() + "]: processing item - " + item);
		try {
			StringBuilder sb = new StringBuilder();
			/*for (Long i=0L;i<getProcessotId();i++) {
				sb.append("           ");
			}*/
			sb.append(getProcessotId()).append(":").append(item);
			System.out.println(sb.toString());
			processedItemCount++;
			Thread.sleep(100);
		} catch (Exception e) {
			Utils.Logging.WORKER.error("Consumer [" + getProcessotId() + "]: item marked as filed - " + item);
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
		return processorId;
	}

	public void setProcessorId(Long processotId) {
		this.processorId = processotId;
	}
}
