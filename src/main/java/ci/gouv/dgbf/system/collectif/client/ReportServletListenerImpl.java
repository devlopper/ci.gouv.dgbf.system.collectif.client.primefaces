package ci.gouv.dgbf.system.collectif.client;
import java.io.Serializable;

import javax.ws.rs.core.Response;

import org.cyk.utility.report.jasper.client.ReportServlet;

import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;

public class ReportServletListenerImpl extends ReportServlet.Listener.AbstractImpl implements Serializable {
	
	@Override
	protected Response getResponse(String identifier,String parametersAsJson, String fileType, Boolean isContentInline) {
		if(EXPENDITURE_ADJUSTMENT_IS_NOT_ZERO.equals(identifier))
			return Expenditure.getService().getAdjustmentIsNotZeroReport(parametersAsJson,fileType,isContentInline, null);
		return super.getResponse(identifier, parametersAsJson, fileType, isContentInline);
	}
	
	public static final String EXPENDITURE_ADJUSTMENT_IS_NOT_ZERO = "saisie-ajustement";
}