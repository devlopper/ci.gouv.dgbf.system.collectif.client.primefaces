package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceAmounts;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractResourceAmountsReadController<AMOUNTS extends ResourceAmounts> extends AbstractAmountsReadController<AMOUNTS> implements Serializable {

	public AbstractResourceAmountsReadController(AMOUNTS amounts,String name) {
		super(amounts,name);
	}
	
	@Override
	protected Boolean hasIncludedMovement() {
		return Boolean.FALSE;
	}
	
	@Override
	protected Boolean hasAvailable() {
		return Boolean.FALSE;
	}
}