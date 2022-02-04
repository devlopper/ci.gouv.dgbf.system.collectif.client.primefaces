package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.AbstractMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.ContextMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.rest.ResponseHelper;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.client.expenditure.ExpenditureListPage;
import ci.gouv.dgbf.system.collectif.client.resource.ResourceListPage;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.PaymentCredit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class LegislativeActVersionListPage extends AbstractEntityListPageContainerManagedImpl<LegislativeActVersion> implements Serializable {

	private LegislativeActVersionFilterController filterController;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new LegislativeActVersionFilterController();   
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue(ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(LegislativeActVersionFilterController.class,filterController);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		LegislativeActVersionFilterController filterController = (LegislativeActVersionFilterController) MapHelper.readByKey(arguments, LegislativeActVersionFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (LegislativeActVersionFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new LegislativeActVersionFilterController());		
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, LegislativeActVersion.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);

		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("60");
		
		dataTable.addHeaderToolbarLeftCommandsByArgumentsOpenViewInDialogCreate();
		
		dataTable.setEntityIdentifierParameterName(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER);
		dataTable.addRecordMenuItemByArgumentsNavigateToView(null,ExpenditureListPage.OUTCOME, MenuItem.FIELD_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.Expenditure.NAME_PLURAL
				,MenuItem.FIELD_ICON,"fa fa-arrow-circle-down");
		dataTable.addRecordMenuItemByArgumentsNavigateToView(null,ResourceListPage.OUTCOME, MenuItem.FIELD_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.Resource.NAME_PLURAL
				,MenuItem.FIELD_ICON,"fa fa-arrow-circle-up");
		dataTable.addRecordMenuItemByArgumentsNavigateToView(null,GeneratedActListPage.OUTCOME, MenuItem.FIELD_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.GeneratedAct.NAME_PLURAL
				,MenuItem.FIELD_ICON,"fa fa-file-text");
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Importer dépenses", "fa fa-plus-square-o", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeActVersion legislativeActVersion = (LegislativeActVersion)action.readArgument();
				if(legislativeActVersion == null)
					throw new RuntimeException("Sélectionner "+ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
				Response response = DependencyInjection.inject(ExpenditureController.class).import_(legislativeActVersion.getIdentifier());
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Définir comme version par défaut", "fa fa-plus-square-o", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeActVersion legislativeActVersion = (LegislativeActVersion)action.readArgument();
				if(legislativeActVersion == null)
					throw new RuntimeException("Sélectionner "+ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
				Response response = DependencyInjection.inject(LegislativeActController.class).updateDefaultVersion(legislativeActVersion);
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		
		/*
		LegislativeActVersionFilterController finalFilterController = filterController;
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Inclure", "fa fa-long-arrow-down", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeActVersion regulatoryAct = (LegislativeActVersion)action.readArgument();
				if(regulatoryAct == null)
					throw new RuntimeException("Sélectionner un acte de gestion");
				LegislativeActVersion legislativeActVersion = finalFilterController == null ? null : finalFilterController.getLegislativeActVersionInitial();
				if(legislativeActVersion == null)
					throw new RuntimeException("Sélectionner une version de collectif");
				Response response = DependencyInjection.inject(LegislativeActVersionController.class).include(legislativeActVersion, Boolean.FALSE, regulatoryAct);
				//PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier());
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Eclure", "fa fa-long-arrow-up", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeActVersion regulatoryAct = (LegislativeActVersion)action.readArgument();
				if(regulatoryAct == null)
					throw new RuntimeException("Sélectionner un acte de gestion");
				LegislativeActVersion legislativeActVersion = finalFilterController == null ? null : finalFilterController.getLegislativeActVersionInitial();
				if(legislativeActVersion == null)
					throw new RuntimeException("Sélectionner une version de collectif");
				Response response = DependencyInjection.inject(LegislativeActVersionController.class).exclude(legislativeActVersion, Boolean.FALSE, regulatoryAct);
				//PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier());
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		*/
		return dataTable;
	}
	
	public static DataTable buildDataTable(Object...objects) {
		return buildDataTable(ArrayHelper.isEmpty(objects) ? null : MapHelper.instantiate(objects));
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		private LegislativeActVersionFilterController filterController;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(LegislativeActVersion.FIELD_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Code");
				map.put(Column.FIELD_WIDTH, "70");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
			}else if(LegislativeActVersion.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Libellé");
			}else if(LegislativeActVersion.FIELD_ACT_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Collectif");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
			}else if(LegislativeActVersion.FIELD___AUDIT__.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_WIDTH, "350");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
			}else
				Helper.DataTable.Amounts.processColumnArguments(map, fieldName,null,null,null);
			
			return map;
		}
		
		@Override
		public Class<? extends AbstractMenu> getRecordMenuClass(AbstractCollection collection) {
			return ContextMenu.class;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<LegislativeActVersion> implements Serializable {
		
		private LegislativeActVersionFilterController filterController;
		
		public LazyDataModel() {
			setEntityClass(LegislativeActVersion.class);
		}
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return List.of(LegislativeActVersionDto.JSONS_STRINGS,LegislativeActVersionDto.JSONS_AMOUTNS,LegislativeActVersionDto.JSON___AUDIT__);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return LegislativeActVersionFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
		
		@Override
		protected void process(LegislativeActVersion legislativeActVersion) {
			super.process(legislativeActVersion);
			if(legislativeActVersion.getEntryAuthorization() == null)
				legislativeActVersion.setEntryAuthorization(new EntryAuthorization());
			if(legislativeActVersion.getPaymentCredit() == null)
				legislativeActVersion.setPaymentCredit(new PaymentCredit());
		}
	}
	
	public static final String OUTCOME = "legislativeActVersionListView";
}