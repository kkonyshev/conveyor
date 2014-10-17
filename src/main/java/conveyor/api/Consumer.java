package conveyor.api;


/**
 * Интерфейс доступа обрабтчика к диспетчеру
 * 
 * @author kkonyshev
 *
 * @param <I>
 */
public interface Consumer<I> {

	/**
	 * Получение идентификатора группы для обработки
	 * 
	 * @param consumerId
	 * @return
	 * @throws InterruptedException 
	 */
	Long leaseGroupId(Long consumerId) throws InterruptedException;
	
	/**
	 * Проверка наличия элементов в группе для обработки
	 * 
	 * @param groupId
	 * @return
	 */
	Boolean hasNextItem(Long groupId);
	
	/**
	 * Получение следующего элемента для обработки
	 * 
	 * @param groupId
	 * @return
	 * @throws InterruptedException 
	 */
	I getNext(Long groupId) throws InterruptedException;
	
	
	/**
	 * Вернуть ошибку обработки
	 * 
	 * @param item
	 */
	void markItemFailed(I item);
}
