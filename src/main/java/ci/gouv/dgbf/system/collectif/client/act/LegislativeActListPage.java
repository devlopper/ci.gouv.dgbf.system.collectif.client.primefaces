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
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.PaymentCredit;
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
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Créer version", "fa fa-plus-square-o", new MenuItem.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				LegislativeAct legislativeAct = (LegislativeAct)action.readArgument();
				if(legislativeAct == null)
					throw new RuntimeException("Sélectionner "+ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
				Response response = DependencyInjection.inject(LegislativeActVersionController.class).create(null, null, null, legislativeAct.getIdentifier());
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		/*
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
				map.put(Column.FIELD_HEADER_TEXT, "Libellé");
			}else if(LegislativeAct.FIELD_EXERCISE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exercice");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(LegislativeAct.FIELD_DEFAULT_VERSION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Version");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(LegislativeAct.FIELD_IN_PROGRESS_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "En cours");
				map.put(Column.FIELD_WIDTH, "100");
			}/*else if(LegislativeAct.FIELD_EXPECTED_ENTRY_AUTHORIZATION_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Ajustement A.E. attendu");
				map.put(Column.FIELD_WIDTH, "200");
			}else if(LegislativeAct.FIELD_EXPECTED_PAYMENT_CREDIT_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Ajustement C.P. attendu");
				map.put(Column.FIELD_WIDTH, "200");
			}else if(LegislativeAct.FIELD_REMAINS_ENTRY_AUTHORIZATION_TO_BE_ADJUSTED.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Reste A.E. à ajuster");
				map.put(Column.FIELD_WIDTH, "200");
			}else if(LegislativeAct.FIELD_REMAINS_PAYMENT_CREDIT_TO_BE_ADJUSTED.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Reste C.P. à ajuster");
				map.put(Column.FIELD_WIDTH, "200");
			}*/else if(LegislativeAct.FIELD___AUDIT__.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_WIDTH, "350");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
			}
	
			//Amounts
			
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_INITIAL, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Initial", ExpenditureAmounts.FIELD_INITIAL, fieldName
						, null,Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvement", ExpenditureAmounts.FIELD_MOVEMENT, fieldName
						, null,Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvements Inclus(M)", ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName
						, null,Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel(A)", ExpenditureAmounts.FIELD_ACTUAL, fieldName
						, null,Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel Calculé", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName
						, null,Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_AVAILABLE, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Disponible", ExpenditureAmounts.FIELD_AVAILABLE
						, fieldName,null, Boolean.FALSE, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation(V)", ExpenditureAmounts.FIELD_ADJUSTMENT
						, fieldName, null,null, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "A+V", ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
						, fieldName, null,null, null);
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Collectif(A-M+V)", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName
						, null,Boolean.FALSE, null);
			
			
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
			return List.of(LegislativeActDto.JSONS_STRINGS,LegislativeActDto.JSONS_AMOUTNS,LegislativeActDto.JSON___AUDIT__);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return LegislativeActFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
		
		@Override
		protected void process(LegislativeAct legislativeAct) {
			super.process(legislativeAct);
			if(legislativeAct.getEntryAuthorization() == null)
				legislativeAct.setEntryAuthorization(new EntryAuthorization());
			if(legislativeAct.getPaymentCredit() == null)
				legislativeAct.setPaymentCredit(new PaymentCredit());
		}
	}
	
	public static final String OUTCOME = "legislativeActListView";
}