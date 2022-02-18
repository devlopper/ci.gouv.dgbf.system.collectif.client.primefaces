package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;

import ci.gouv.dgbf.system.collectif.server.client.rest.PaymentCredit;

public class PaymentCreditReadController extends AbstractExpenditureAmountsReadController<PaymentCredit> implements Serializable {

	public PaymentCreditReadController(PaymentCredit paymentCredit) {
		super(paymentCredit, StringHelper.applyCase(ci.gouv.dgbf.system.collectif.server.api.persistence.PaymentCredit.NAME,Case.FIRST_CHARACTER_UPPER));
	}

}