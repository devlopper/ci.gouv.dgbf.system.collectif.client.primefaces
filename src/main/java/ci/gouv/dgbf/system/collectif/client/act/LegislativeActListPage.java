package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.AbstractMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.ContextMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class LegislativeActListPage extends AbstractEntityListPageContainerManagedImpl<LegislativeAct> implements Serializable {

	private LegislativeActFilterController filterController;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new LegislativeActFilterController();   
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue(ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(LegislativeActFilterController.class,filterController);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		LegislativeActFilterController filterController = (LegislativeActFilterController) MapHelper.readByKey(arguments, LegislativeActFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (LegislativeActFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new LegislativeActFilterController());		
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, LegislativeAct.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("60");
		
		dataTable.addHeaderToolbarLeftCommandsByArgumentsOpenViewInDialogCreate();
		
		dataTable.setEntityIdentifierParameterName(Parameters.LEGISLATIVE_ACT_IDENTIFIER);
		dataTable.addRecordMenuItemByArgumentsNavigateToView(null,LegislativeActVersionListPage.OUTCOME, MenuItem.FIELD_VALUE,"Versions",MenuItem.FIELD_ICON,"fa fa-eye");
		/*
		LegislativeActFilterController finalFilterController = filterController;
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Inclure", "fa fa-long-arrow-down", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeAct regulatoryAct = (LegislativeAct)action.readArgument();
				if(regulatoryAct == null)
					throw new RuntimeException("Sélectionner un acte de gestion");
				LegislativeAct legislativeAct = finalFilterController == null ? null : finalFilterController.getLegislativeActInitial();
				if(legislativeAct == null)
					throw new RuntimeException("Sélectionner une version de collectif");
				Response response = DependencyInjection.inject(LegislativeActController.class).include(legislativeAct, Boolean.FALSE, regulatoryAct);
				//PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier());
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Eclure", "fa fa-long-arrow-up", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeAct regulatoryAct = (LegislativeAct)action.readArgument();
				if(regulatoryAct == null)
					throw new RuntimeException("Sélectionner un acte de gestion");
				LegislativeAct legislativeAct = finalFilterController == null ? null : finalFilterController.getLegislativeActInitial();
				if(legislativeAct == null)
					throw new RuntimeException("Sélectionner une version de collectif");
				Response response = DependencyInjection.inject(LegislativeActController.class).exclude(legislativeAct, Boolean.FALSE, regulatoryAct);
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
		private LegislativeActFilterController filterController;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(LegislativeAct.FIELD_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Code");
				map.put(Column.FIELD_WIDTH, "70");
			}else if(LegislativeAct.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Désignation");
			}/*else if(LegislativeAct.FIELD_ENTRY_AUTHORIZATION_AMOUNT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "A.E.");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(LegislativeAct.FIELD_PAYMENT_CREDIT_AMOUNT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "C.P.");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(LegislativeAct.FIELD_AUDIT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_WIDTH, "200");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
			}else if(LegislativeAct.FIELD_INCLUDED_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Inclus");
				map.put(Column.FIELD_WIDTH, "70");
			}*/
			
			return map;
		}
		
		@Override
		public Class<? extends AbstractMenu> getRecordMenuClass(AbstractCollection collection) {
			return ContextMenu.class;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<LegislativeAct> implements Serializable {
		
		private LegislativeActFilterController filterController;
		
		public LazyDataModel() {
			setEntityClass(LegislativeAct.class);
		}
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return List.of(LegislativeActDto.JSON_IDENTIFIER,LegislativeActDto.JSON_CODE,LegislativeActDto.JSON_NAME,LegislativeActDto.JSON___AUDIT__);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return LegislativeActFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
		
		
	}
	
	public static final String OUTCOME = "legislativeActListView";
}