package conveyor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import conveyor.api.Dispatcher;
import conveyor.api.Item;

@Repository
public class GateService {

	@Autowired
	private Dispatcher<Item> dispatcher;
	
	
}
