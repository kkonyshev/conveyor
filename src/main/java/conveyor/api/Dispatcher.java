package conveyor.api;


/**
 * Диспетчер
 * 
 * @author kkonyshev
 *
 */
public interface Dispatcher<I> extends Consumer<I>, Producer<I> {
}
