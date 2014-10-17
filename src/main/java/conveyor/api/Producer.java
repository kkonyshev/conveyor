package conveyor.api;



/**
 * Интерйефс добавления элементов в очередь обработки
 * 
 * @author kkonyshev
 *
 * @param <I>
 */
public interface Producer<I> {
	/**
	 * Добавление нового элемента в очередь обработки
	 * 
	 * @param item
	 */
	void addItem(I item);
	
	/**
	 * Массовое добалвение элементов в очередь обработки
	 * 
	 * @param itemColelction
	 */
	//void addItemBatch(Collection<I> itemColelction);
}
