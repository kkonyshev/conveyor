package conveyor.api;

/**
 * Интерфейс обрабатываемых элементов
 * 
 * @author kkonyshev
 *
 */
public interface Item extends Comparable<Item> {
	
	/**
	 * Идентификатор группы элемента
	 * @return
	 */
	Long getGroupId();
	
	/**
	 * Идентификатор элемента
	 * @return
	 */
	Long getId();
}