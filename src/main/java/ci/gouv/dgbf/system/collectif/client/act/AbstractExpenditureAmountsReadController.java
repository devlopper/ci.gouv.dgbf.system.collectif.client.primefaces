package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractExpenditureAmountsReadController<AMOUNTS extends ExpenditureAmounts> extends AbstractAmountsReadController<AMOUNTS> implements Serializable {

	public AbstractExpenditureAmountsReadController(AMOUNTS amounts,String name) {
		super(amounts,name);
	}
	
}