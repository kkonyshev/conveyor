package conveyor.api;

/**
 * Обработчик элементов
 * 
 * @author kkonyshev
 *
 */
public interface ThreadProcessor<I> {
	/**
	 * 
	 * @return
	 */
	Integer getItemCountThreshold();
	/**
	 * 
	 * @param itemCountThreshold
	 */
	void setItemCountThreshold(Integer itemCountThreshold);
	
	/**
	 * 
	 */
	Integer getTotalProcessedCount();
}
