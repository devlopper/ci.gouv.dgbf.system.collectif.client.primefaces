package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import ci.gouv.dgbf.system.collectif.server.client.rest.PaymentCredit;

public class PaymentCreditReadController extends AbstractExpenditureAmountsReadController<PaymentCredit> implements Serializable {

	public PaymentCreditReadController(PaymentCredit paymentCredit) {
		super(paymentCredit, ci.gouv.dgbf.system.collectif.server.api.persistence.PaymentCredit.NAME);
	}

}