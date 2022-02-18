package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;

public class EntryAuthorizationReadController extends AbstractExpenditureAmountsReadController<EntryAuthorization> implements Serializable {

	public EntryAuthorizationReadController(EntryAuthorization entryAuthorization) {
		super(entryAuthorization, ci.gouv.dgbf.system.collectif.server.api.persistence.EntryAuthorization.NAME);
	}

}