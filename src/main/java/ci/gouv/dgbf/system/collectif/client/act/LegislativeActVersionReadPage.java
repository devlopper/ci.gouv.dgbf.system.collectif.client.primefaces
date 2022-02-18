package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class LegislativeActVersionReadPage extends AbstractPageContainerManagedImpl implements Serializable {

	private LegislativeActVersion legislativeActVersion;
	private LegislativeActVersionReadController readController;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		legislativeActVersion = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
				.projections(LegislativeActVersionDto.JSONS_STRINGS,LegislativeActVersionDto.JSONS_AMOUTNS,LegislativeActVersionDto.JSON_IS_DEFAULT_VERSION,LegislativeActVersionDto.JSON___AUDIT__)
				.setFilter(new Filter.Dto().addField(Parameters.DEFAULT_LEGISLATIVE_ACT_VERSION_IN_LATEST_LEGISLATIVE_ACT, Boolean.TRUE)));
		if(legislativeActVersion != null) {
			readController = new LegislativeActVersionReadController(legislativeActVersion);
			RegulatoryActFilterController regulatoryActFilterController = new RegulatoryActFilterController();
			regulatoryActFilterController.setLegislativeActVersionInitial(legislativeActVersion);
			regulatoryActFilterController.setIncludedInitial(Boolean.TRUE);
			regulatoryActFilterController.setRenderType(AbstractFilterController.RenderType.NONE);
			readController.setRegulatoryActsDataTable(RegulatoryActListPage.buildDataTable(RegulatoryActFilterController.class,regulatoryActFilterController));
			readController.initialize();
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(legislativeActVersion == null)
			return "Aucune version collectif budgétaire trouvée";
		return legislativeActVersion.getName();
	}
}