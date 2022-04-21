package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
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
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class LegislativeActVersionReadPage extends AbstractPageContainerManagedImpl implements Serializable {

	private LegislativeActVersion legislativeActVersion;
	private LegislativeActVersionReadController readController;
	private ExpenditureFilterController expenditureFilterController;
	private ResourceFilterController resourceFilterController;
	private RegulatoryActFilterController regulatoryActFilterController;
	private GeneratedActFilterController generatedActFilterController;
	private TabMenu tabMenu;
	private Layout layout;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		legislativeActVersion = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
				.projections(LegislativeActVersionDto.JSONS_STRINGS//,LegislativeActVersionDto.JSONS_RESOURCES_AMOUTNS,LegislativeActVersionDto.JSONS_EXPENDITURES_AMOUTNS
						,LegislativeActVersionDto.JSON_IS_DEFAULT_VERSION,LegislativeActVersionDto.JSONS_LEGISLATIVE_ACT_FROM_DATE_AS_TIMESTAMP_DATE_AS_TIMESTAMP
						,LegislativeActVersionDto.JSONS_GENERATED_ACT_COUNT_ACT_GENERATABLE_GENERATED_ACT_DELETABLE,LegislativeActVersionDto.JSON___AUDIT__)
				.setFilter(new Filter.Dto().addField(Parameters.DEFAULT_LEGISLATIVE_ACT_VERSION_IN_LATEST_LEGISLATIVE_ACT, Boolean.TRUE)));
		if(legislativeActVersion == null)
			return;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		buildTabMenu(cellsMaps);
		buildTab(cellsMaps);
		buildLayout(cellsMaps);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(legislativeActVersion == null)
			return "Aucune version collectif budgétaire trouvée";
		/*
		if(readController != null)
			return legislativeActVersion.getName()+" - "+TABS.stream().filter(tab -> tab.getParameterValue().equals(TAB_SUMMARY)).findFirst().get().getName();
		if(regulatoryActFilterController != null)
			return regulatoryActFilterController.generateWindowTitleValue(tabMenu.getSelected().getName());
		*/
		return legislativeActVersion.getName();
	}
	
	private void buildTabMenu(Collection<Map<Object,Object>> cellsMaps) {		
		tabMenu = TabMenu.build(TabMenu.ConfiguratorImpl.FIELD_ITEMS_OUTCOME,OUTCOME,TabMenu.ConfiguratorImpl.FIELD_TABS,TABS,TabMenu.ConfiguratorImpl.FIELD_TAB_MENU_ITEM_BUILDER,new TabMenu.Tab.MenuItemBuilder.AbstractImpl() {
			@Override
			protected void process(TabMenu tabMenu, Tab tab, MenuItem item) {
				super.process(tabMenu, tab, item);
				if(TAB_REGULATORY_ACTS.equals(tab.getParameterValue())) {
					HttpServletRequest request = __inject__(HttpServletRequest.class);
					if(!request.getParameterMap().keySet().contains(Parameters.REGULATORY_ACT_INCLUDED))
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
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_INCONSISTENCIES))
			buildTabInconsistencies(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_GENERATED_ACTS))
			buildTabGeneratedActs(cellsMaps);
	}
	
	private void buildTabSummary(Collection<Map<Object,Object>> cellsMaps) {
		readController = new LegislativeActVersionReadController(legislativeActVersion);
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
		regulatoryActFilterController.setLegislativeActVersionInitial(legislativeActVersion);
		if(legislativeActVersion.getActFromDateAsTimestamp() != null)
			regulatoryActFilterController.setDateGreaterThanOrEqualInitial(new Date(legislativeActVersion.getActFromDateAsTimestamp()));
		if(legislativeActVersion.getActDateAsTimestamp() != null)
			regulatoryActFilterController.setDateLowerThanOrEqualInitial(new Date(legislativeActVersion.getActDateAsTimestamp()));
		//HttpServletRequest request = __inject__(HttpServletRequest.class);
		//if(!request.getParameterMap().keySet().contains(Parameters.REGULATORY_ACT_INCLUDED))
		//	regulatoryActFilterController.setIncludedInitial(Boolean.TRUE);
		regulatoryActFilterController.setReadOnlyByFieldsNames(RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
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
	
	private void buildTabExpenditures(Collection<Map<Object,Object>> cellsMaps) {
		expenditureFilterController = new ExpenditureFilterController();
		expenditureFilterController.setLegislativeActVersionInitial(legislativeActVersion);
		expenditureFilterController.setReadOnlyByFieldsNames(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
		expenditureFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_EXPENDITURES);
		DataTable dataTable = ExpenditureListPage.buildDataTable(ExpenditureFilterController.class,expenditureFilterController,ExpenditureListPage.OUTCOME,OUTCOME
				,DataTable.FIELD_LISTENER,new ExpenditureDataTableListenerImpl());
		expenditureFilterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_EXPENDITURES);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable));
		
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
		resourceFilterController.setLegislativeActVersionInitial(legislativeActVersion);
		resourceFilterController.setReadOnlyByFieldsNames(ResourceFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ResourceFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
		resourceFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_RESOURCES);
		DataTable dataTable = ResourceListPage.buildDataTable(ResourceFilterController.class,resourceFilterController,ResourceListPage.OUTCOME,OUTCOME
				,DataTable.FIELD_LISTENER,new ResourceDataTableListenerImpl());
		resourceFilterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_RESOURCES);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable));
		
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
	
	private void buildTabInconsistencies(Collection<Map<Object,Object>> cellsMaps) {
		expenditureFilterController = new ExpenditureFilterController();
		expenditureFilterController.setLegislativeActVersionInitial(legislativeActVersion);
		expenditureFilterController.setAvailableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial(Boolean.TRUE);
		expenditureFilterController.setReadOnlyByFieldsNames(ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,ExpenditureFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE
				,ExpenditureFilterController.FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE);
		expenditureFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_INCONSISTENCIES);
		DataTable dataTable = ExpenditureListPage.buildDataTable(ExpenditureFilterController.class,expenditureFilterController,ExpenditureListPage.OUTCOME,OUTCOME
				,DataTable.FIELD_LISTENER,new ExpenditureDataTableListenerImpl());
		expenditureFilterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_INCONSISTENCIES);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable));
		
	}
	
	private void buildTabGeneratedActs(Collection<Map<Object,Object>> cellsMaps) {
		generatedActFilterController = new GeneratedActFilterController();
		generatedActFilterController.setLegislativeActVersionInitial(legislativeActVersion);
		generatedActFilterController.setReadOnlyByFieldsNames(GeneratedActFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE,GeneratedActFilterController.FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE);
		generatedActFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_GENERATED_ACTS);
		generatedActFilterController.setParameterTabIdentifier(TAB_GENERATED_ACTS);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,GeneratedActListPage.buildDataTable(GeneratedActFilterController.class,generatedActFilterController,GeneratedActListPage.OUTCOME,OUTCOME)));
	}
	
	private void buildLayout(Collection<Map<Object,Object>> cellsMaps) {
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	/**/
	
	public static final String TAB_SUMMARY = "recapitulatif";	
	public static final String TAB_REGULATORY_ACTS = "actes_gestion";
	public static final String TAB_EXPENDITURES = "depenses";
	public static final String TAB_RESOURCES = "ressources";
	public static final String TAB_INCONSISTENCIES = "incoherences";
	public static final String TAB_GENERATED_ACTS = "actesgeneres";
	public static final List<TabMenu.Tab> TABS = List.of(
		new TabMenu.Tab("Récapitulatif",TAB_SUMMARY)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.RegulatoryAct.NAME_PLURAL,TAB_REGULATORY_ACTS)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.Expenditure.NAME_PLURAL,TAB_EXPENDITURES)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.Resource.NAME_PLURAL,TAB_RESOURCES)
		,new TabMenu.Tab("Incohérences",TAB_INCONSISTENCIES)
		,new TabMenu.Tab(ci.gouv.dgbf.system.collectif.server.api.persistence.GeneratedAct.NAME_PLURAL,TAB_GENERATED_ACTS)
	);

	public static final String OUTCOME = "legislativeActVersionReadView";
	
	@Getter @Setter @Accessors(chain=true)
	public static class ExpenditureDataTableListenerImpl extends ExpenditureListPage.DataTableListenerImpl implements Serializable {
		
		public ExpenditureDataTableListenerImpl() {
			adjustmentEditUserInterfaceAction = UserInterfaceAction.OPEN_VIEW_IN_DIALOG;
		}
		
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class ResourceDataTableListenerImpl extends ResourceListPage.DataTableListenerImpl implements Serializable {
		
		public ResourceDataTableListenerImpl() {
			adjustmentEditUserInterfaceAction = UserInterfaceAction.OPEN_VIEW_IN_DIALOG;
		}
		
	}
}