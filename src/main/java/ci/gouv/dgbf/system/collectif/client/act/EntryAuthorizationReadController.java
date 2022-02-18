package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;

import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;

public class EntryAuthorizationReadController extends AbstractExpenditureAmountsReadController<EntryAuthorization> implements Serializable {

	public EntryAuthorizationReadController(EntryAuthorization entryAuthorization) {
		super(entryAuthorization, StringHelper.applyCase(ci.gouv.dgbf.system.collectif.server.api.persistence.EntryAuthorization.NAME,Case.FIRST_CHARACTER_UPPER));
	}

}