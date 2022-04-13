package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;

import ci.gouv.dgbf.system.collectif.server.client.rest.Revenue;

public class RevenueReadController extends AbstractResourceAmountsReadController<Revenue> implements Serializable {

	public RevenueReadController(Revenue revenue) {
		super(revenue, StringHelper.applyCase(ci.gouv.dgbf.system.collectif.server.api.persistence.Resource.NAME,Case.FIRST_CHARACTER_UPPER));
	}

}