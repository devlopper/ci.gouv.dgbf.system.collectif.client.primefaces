package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.TabMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.TabMenu.Tab;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.client.expenditure.ExpenditureFilterController;
import ci.gouv.dgbf.system.collectif.client.expenditure.ExpenditureListPage;
import ci.gouv.dgbf.system.collectif.client.resource.ResourceFilterController;
import ci.gouv.dgbf.system.collectif.client.resource.ResourceListPage;
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
	private LegislativeActVersionFilterController legislativeActVersionFilterController;
	private RegulatoryActFilterController regulatoryActFilterController;
	private ExpenditureFilterController expenditureFilterController;
	private ResourceFilterController resourceFilterController;
	private TabMenu tabMenu;
	private Layout layout;
		
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		legislativeAct = __inject__(LegislativeActController.class).getOne(new Controller.GetArguments().projections(LegislativeActDto.JSON_IDENTIFIER,LegislativeActDto.JSONS_STRINGS,LegislativeActDto.JSONS_AMOUTNS
				,LegislativeActDto.JSON_DEFAULT_VERSION_IDENTIFIER,LegislativeActDto.JSON___AUDIT__)
				.setFilter(new Filter.Dto().addField(Parameters.LATEST_LEGISLATIVE_ACT, Boolean.TRUE)));
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		buildTabMenu(cellsMaps);
		buildTab(cellsMaps);
		buildLayout(cellsMaps);
	}
	
	private void buildTabMenu(Collection<Map<Object,Object>> cellsMaps) {		
		tabMenu = TabMenu.build(TabMenu.ConfiguratorImpl.FIELD_ITEMS_OUTCOME,OUTCOME,TabMenu.ConfiguratorImpl.FIELD_TABS,TABS,TabMenu.ConfiguratorImpl.FIELD_TAB_MENU_ITEM_BUILDER,new TabMenu.Tab.MenuItemBuilder.AbstractImpl() {
			@Override
			protected void process(TabMenu tabMenu, Tab tab, MenuItem item) {
				super.process(tabMenu, tab, item);
				if(StringHelper.isNotBlank(legislativeAct.getDefaultVersionIdentifier()))
					item.addParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, legislativeAct.getDefaultVersionIdentifier());
				if(TAB_REGULATORY_ACTS.equals(tab.getParameterValue())) {					
					item.addParameter(Parameters.REGULATORY_ACT_INCLUDED, Boolean.TRUE.toString());
				}
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,tabMenu,Cell.FIELD_WIDTH,12));
	}
	
	private void buildTab(Collection<Map<Object,Object>> cellsMaps) {
		if(tabMenu.getSelected().getParameterValue().equals(TAB_SUMMARY))
			buildTabSummary(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_REGULATORY_ACTS))
			buildTabRegulatoryActs(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_EXPENDITURES))
			buildTabExpenditures(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_RESOURCES))
			buildTabResources(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_VERSIONS))
			buildTabVersions(cellsMaps);
	}
	
	private void buildTabSummary(Collection<Map<Object,Object>> cellsMaps) {
		LegislativeActReadController readController = new LegislativeActReadController(legislativeAct);
		readController.initialize();
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,readController.getLayout()));
		/*
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				LegislativeActVersionReadController readController = new LegislativeActVersionReadController(legislativeActVersion);
				readController.initialize();
				return readController.getLayout();
			}
		},Cell.FIELD_WIDTH,12));
		*/
	}
	
	private void buildTabRegulatoryActs(Collection<Map<Object,Object>> cellsMaps) {
		regulatoryActFilterController = new RegulatoryActFilterController();
		regulatoryActFilterController.setLegislativeActInitial(legislativeAct);
		//HttpServletRequest request = __inject__(HttpServletRequest.class);
		//if(!request.getParameterMap().keySet().contains(Parameters.REGULATORY_ACT_INCLUDED))
		//	regulatoryActFilterController.setIncludedInitial(Boolean.TRUE);
		regulatoryActFilterController.setReadOnlyByFieldsNames(RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE);
		regulatoryActFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_REGULATORY_ACTS);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,RegulatoryActListPage.buildDataTable(RegulatoryActFilterController.class,regulatoryActFilterController,RegulatoryActListPage.OUTCOME,OUTCOME)));
		/*
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.FALSE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				RegulatoryActFilterController regulatoryActFilterController = new RegulatoryActFilterController();
				regulatoryActFilterController.setLegislativeActVersionInitial(legislativeActVersion);
				regulatoryActFilterController.setIncludedInitial(Boolean.TRUE);
				regulatoryActFilterController.ignore(RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
				return RegulatoryActListPage.buildDataTable(RegulatoryActFilterController.class,regulatoryActFilterController);
			}
		},Cell.FIELD_WIDTH,12));
		*/
	}
	
	private void buildTabVersions(Collection<Map<Object,Object>> cellsMaps) {
		legislativeActVersionFilterController = new LegislativeActVersionFilterController();
		legislativeActVersionFilterController.setLegislativeActInitial(legislativeAct);
		legislativeActVersionFilterController.setReadOnlyByFieldsNames(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE);
		legislativeActVersionFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_VERSIONS);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,LegislativeActVersionListPage.buildDataTable(LegislativeActVersionFilterController.class,legislativeActVersionFilterController,LegislativeActVersionListPage.OUTCOME,OUTCOME)));
		/*
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				expenditureFilterController = new ExpenditureFilterController();
				expenditureFilterController.setLegislativeActVersionInitial(legislativeActVersion);
				expenditureFilterController.ignore(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
				return ExpenditureListPage.buildDataTable(ExpenditureFilterController.class,expenditureFilterController);
			}
		},Cell.FIELD_WIDTH,12));
		*/
	}
	
	private void buildTabExpenditures(Collection<Map<Object,Object>> cellsMaps) {
		expenditureFilterController = new ExpenditureFilterController();
		expenditureFilterController.setLegislativeActInitial(legislativeAct);
		expenditureFilterController.setReadOnlyByFieldsNames(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE);
		expenditureFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_EXPENDITURES);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,ExpenditureListPage.buildDataTable(ExpenditureFilterController.class,expenditureFilterController,ExpenditureListPage.OUTCOME,OUTCOME)));
		expenditureFilterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_EXPENDITURES);
		/*
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				expenditureFilterController = new ExpenditureFilterController();
				expenditureFilterController.setLegislativeActVersionInitial(legislativeActVersion);
				expenditureFilterController.ignore(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
				return ExpenditureListPage.buildDataTable(ExpenditureFilterController.class,expenditureFilterController);
			}
		},Cell.FIELD_WIDTH,12));
		*/
	}
	
	private void buildTabResources(Collection<Map<Object,Object>> cellsMaps) {
		resourceFilterController = new ResourceFilterController();
		resourceFilterController.setLegislativeActInitial(legislativeAct);
		resourceFilterController.setReadOnlyByFieldsNames(ResourceFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE);
		resourceFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_RESOURCES);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,ResourceListPage.buildDataTable(ResourceFilterController.class,resourceFilterController,ResourceListPage.OUTCOME,OUTCOME)));
		//resourceFilterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_EXPENDITURES);
		/*
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				resourceFilterController = new ResourceFilterController();
				resourceFilterController.setLegislativeActVersionInitial(legislativeActVersion);
				resourceFilterController.ignore(ResourceFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ResourceFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
				return ResourceListPage.buildDataTable(ResourceFilterController.class,resourceFilterController);
			}
		},Cell.FIELD_WIDTH,12));
		*/
	}
	
	private void buildLayout(Collection<Map<Object,Object>> cellsMaps) {
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(legislativeAct == null)
			return "Aucun collectif budgétaire trouvé";
		return legislativeAct.getName();
	}
	
	public static final String TAB_SUMMARY = "summary";	
	public static final String TAB_REGULATORY_ACTS = "actes_gestion";
	public static final String TAB_EXPENDITURES = "depenses";
	public static final String TAB_RESOURCES = "recettes";
	public static final String TAB_VERSIONS = "versions";
	public static final List<TabMenu.Tab> TABS = List.of(
		new TabMenu.Tab("Récapitulatif",TAB_SUMMARY)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.RegulatoryAct.NAME_PLURAL,TAB_REGULATORY_ACTS)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.Expenditure.NAME_PLURAL,TAB_EXPENDITURES)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.Resource.NAME_PLURAL,TAB_RESOURCES)
		,new TabMenu.Tab("Versions",TAB_VERSIONS)
	);

	public static final String OUTCOME = "legislativeActReadView";
}