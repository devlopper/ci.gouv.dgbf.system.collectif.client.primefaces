package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class LegislativeActReadPage extends AbstractPageContainerManagedImpl implements Serializable {

	private LegislativeAct legislativeAct;
	private LegislativeActReadController readController;
		
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		legislativeAct = __inject__(LegislativeActController.class).getOne(new Controller.GetArguments().projections(LegislativeActDto.JSON_IDENTIFIER,LegislativeActDto.JSONS_STRINGS,LegislativeActDto.JSONS_AMOUTNS,LegislativeActDto.JSON___AUDIT__)
				.setFilter(new Filter.Dto().addField(Parameters.LATEST_LEGISLATIVE_ACT, Boolean.TRUE)));
		if(legislativeAct != null) {
			readController = new LegislativeActReadController(legislativeAct);
			LegislativeActVersionFilterController filterController = new LegislativeActVersionFilterController();
			filterController.setLegislativeActInitial(legislativeAct);
			filterController.setRenderType(AbstractFilterController.RenderType.NONE);
			readController.setLegislativeActVersionsDataTable(LegislativeActVersionListPage.buildDataTable(LegislativeActVersionFilterController.class,filterController));
			readController.initialize();
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(legislativeAct == null)
			return "Aucun collectif budgétaire trouvé";
		return legislativeAct.getName();
	}
}