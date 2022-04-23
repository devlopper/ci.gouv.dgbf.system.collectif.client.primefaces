package ci.gouv.dgbf.system.collectif.client;

import java.io.Serializable;
import java.util.List;

import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractFilterControllerBasedLegislativeActVersion extends org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController implements Serializable {

	protected SelectOneCombo legislativeActSelectOne,legislativeActVersionSelectOne,includedSelectOne;
	
	protected LegislativeAct legislativeActInitial;
	protected LegislativeActVersion legislativeActVersionInitial;
	
	public AbstractFilterControllerBasedLegislativeActVersion() {
		if(legislativeActVersionInitial == null)
			legislativeActVersionInitial = __inject__(LegislativeActVersionController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER)
					, new Controller.GetArguments().setProjections(List.of(LegislativeActVersionDto.JSON_IDENTIFIER,LegislativeActVersionDto.JSON_CODE,LegislativeActVersionDto.JSON_NAME,LegislativeActVersionDto.JSON_LEGISLATIVE_ACT)));
		if(legislativeActVersionInitial != null) {
			legislativeActInitial = legislativeActVersionInitial.getAct();
		}
		
		if(legislativeActInitial == null && legislativeActVersionInitial == null)
			legislativeActInitial = __inject__(LegislativeActController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_IDENTIFIER));
	}
	
}