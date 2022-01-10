package ci.gouv.dgbf.system.collectif.client;

import java.util.List;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.BudgetaryActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryActVersionController;

public interface Helper {

	public static BudgetaryAct getBudgetaryActFromRequestParameter(BudgetaryActVersion version) {
		if(version != null && version.getBudgetaryAct() != null)
			return version.getBudgetaryAct();
		return DependencyInjection.inject(BudgetaryActController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.BUDGETARY_ACT_IDENTIFIER), null);
	}
	
	public static BudgetaryActVersion getBudgetaryActVersionFromRequestParameter(String identifier,Boolean computeSumsAndTotal) {
		Controller.GetArguments arguments = new Controller.GetArguments();
		arguments.setProjections(List.of(BudgetaryActVersionDto.JSON_IDENTIFIER,BudgetaryActVersionDto.JSON_CODE,BudgetaryActVersionDto.JSON_NAME,BudgetaryActVersionDto.JSON_BUDGETARY_ACT));
		if(StringHelper.isBlank(identifier)) {
			arguments.setFilter(new Filter.Dto().addField(Parameters.LATEST_BUDGETARY_ACT_VERSION, Boolean.TRUE));
			return DependencyInjection.inject(BudgetaryActVersionController.class).getOne(arguments);
		}
		return DependencyInjection.inject(BudgetaryActVersionController.class).getByIdentifier(identifier, arguments);
	}
	
	public static BudgetaryActVersion getBudgetaryActVersionFromRequestParameter(Boolean computeSumsAndTotal) {
		return getBudgetaryActVersionFromRequestParameter(WebController.getInstance().getRequestParameter(Parameters.BUDGETARY_ACT_VERSION_IDENTIFIER)
				, computeSumsAndTotal);
	}
}