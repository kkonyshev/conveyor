package conveyor.impl;

import conveyor.api.Item;

/**
 * Обрабатываемый объект
 * 
 * @author kkonyshev
 *
 */
public class ItemImpl implements Item {
	private Long id;
	private Long groupId;
	
	public ItemImpl(Long groupId, Long id) {
		this.groupId = groupId;
		this.id = id;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public Long getGroupId() {
		return groupId;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	@Override
	public int compareTo(Item o) {
		if (o==null || o.getId()==null || this.id==null) {
			return 1;
		}
		return this.id.compareTo(o.getId());
	}
	
	@Override
	public String toString() {
		return "[" + groupId + "][" + id + "]";
	}
}
