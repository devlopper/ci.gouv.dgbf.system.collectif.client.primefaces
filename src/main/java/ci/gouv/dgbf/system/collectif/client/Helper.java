package ci.gouv.dgbf.system.collectif.client;

import java.util.List;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.ExerciseDto;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.Exercise;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExerciseController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;

public interface Helper {

	public static Exercise getExerciseFromRequestParameter() {
		return DependencyInjection.inject(ExerciseController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.EXERCISE_IDENTIFIER), new Controller.GetArguments()
				.projections(ExerciseDto.JSON_IDENTIFIER,ExerciseDto.JSON_CODE,ExerciseDto.JSON_NAME,ExerciseDto.JSON_YEAR));
	}
	
	public static LegislativeAct getLegislativeActFromRequestParameter(LegislativeActVersion version) {
		if(version != null && version.getAct() != null)
			return version.getAct();
		return DependencyInjection.inject(LegislativeActController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_IDENTIFIER), null);
	}
	
	public static LegislativeActVersion getLegislativeActVersionFromRequestParameter(String identifier,Boolean computeSumsAndTotal) {
		Controller.GetArguments arguments = new Controller.GetArguments();
		arguments.setProjections(List.of(LegislativeActVersionDto.JSON_IDENTIFIER,LegislativeActVersionDto.JSON_CODE,LegislativeActVersionDto.JSON_NAME,LegislativeActVersionDto.JSON_BUDGETARY_ACT));
		if(StringHelper.isBlank(identifier)) {
			arguments.setFilter(new Filter.Dto().addField(Parameters.LATEST_LEGISLATIVE_ACT_VERSION, Boolean.TRUE));
			return DependencyInjection.inject(LegislativeActVersionController.class).getOne(arguments);
		}
		return DependencyInjection.inject(LegislativeActVersionController.class).getByIdentifier(identifier, arguments);
	}
	
	public static LegislativeActVersion getLegislativeActVersionFromRequestParameter(Boolean computeSumsAndTotal) {
		return getLegislativeActVersionFromRequestParameter(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER)
				, computeSumsAndTotal);
	}
}