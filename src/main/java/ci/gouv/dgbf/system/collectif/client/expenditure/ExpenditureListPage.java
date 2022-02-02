package ci.gouv.dgbf.system.collectif.client.expenditure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.collectif.client.ActivitySelectionController;
import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.ExpenditureDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.PaymentCredit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ExpenditureListPage extends AbstractEntityListPageContainerManagedImpl<Expenditure> implements Serializable {

	private ExpenditureFilterController filterController;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ExpenditureFilterController();
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue(ci.gouv.dgbf.system.collectif.server.api.persistence.Expenditure.NAME_PLURAL);
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(ExpenditureFilterController.class,filterController);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		ExpenditureFilterController filterController = (ExpenditureFilterController) MapHelper.readByKey(arguments, ExpenditureFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (ExpenditureFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new ExpenditureFilterController());		
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);	
		if(filterController.getActivitySelectionController() == null)
			filterController.setActivitySelectionController(new ActivitySelectionController());
		filterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Expenditure.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("60");
		
		Map<String, List<String>> parameters = new HashMap<>();
		if(filterController.getLegislativeActVersionInitial() != null)
			parameters.put(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, List.of((String)FieldHelper.readSystemIdentifier(filterController.getLegislativeActVersionInitial())));
		if(filterController.getLegislativeActInitial() != null && !parameters.containsKey(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER))
			parameters.put(Parameters.LEGISLATIVE_ACT_IDENTIFIER, List.of((String)FieldHelper.readSystemIdentifier(filterController.getLegislativeActInitial())));
		if(filterController.getActivityInitial() != null)
			parameters.put(Parameters.ACTIVITY_IDENTIFIER, List.of((String)FieldHelper.readSystemIdentifier(filterController.getActivityInitial())));
		
		if(Boolean.TRUE.equals(dataTableListenerImpl.getAdjustmentEditable())) {
			
		}else {
			dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD___OUTCOME__,ExpenditureAdjustPage.OUTCOME,MenuItem.FIELD___PARAMETERS__,parameters
					, MenuItem.FIELD_VALUE,"Ajuster",MenuItem.FIELD_ICON,"fa fa-pencil",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.NAVIGATE_TO_VIEW);
		}
		
		return dataTable;
	}
	
	public static DataTable buildDataTable(Object...objects) {
		return buildDataTable(ArrayHelper.isEmpty(objects) ? null : MapHelper.instantiate(objects));
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		private ExpenditureFilterController filterController;
		//private Expenditure expenditureAmountsSum;
		//private Boolean showCodeOnlyWherePossible;
		private Boolean adjustmentEditable/*,amountsColumnsFootersShowable/*,entryAuthorizationAndPaymentCreditShowable*/;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(Expenditure.FIELD_BUDGETARY_ACT_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Acte");
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Expenditure.FIELD_BUDGETARY_ACT_VERSION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Version");
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Expenditure.FIELD_SECTION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Section");
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Expenditure.FIELD_NATURE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "N.D.");
				map.put(Column.FIELD_WIDTH, "40");
			}else if(Expenditure.FIELD_BUDGET_SPECIALIZATION_UNIT_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "U.S.B.");
				map.put(Column.FIELD_WIDTH, "60");
			}else if(Expenditure.FIELD_ACTION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Action");
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Expenditure.FIELD_ACTIVITY_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Activité");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(Expenditure.FIELD_ECONOMIC_NATURE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "N.E.");
				map.put(Column.FIELD_WIDTH, "60");	
			}else if(Expenditure.FIELD_FUNDING_SOURCE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "S.F.");
				map.put(Column.FIELD_WIDTH, "80");
				map.put(Column.FIELD_VISIBLE, isInvestment());
			}else if(Expenditure.FIELD_LESSOR_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Bailleur");
				map.put(Column.FIELD_VISIBLE, isInvestment());
			}else if(Expenditure.FIELD___AUDIT__.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
				map.put(Column.FIELD_WIDTH, "200");
			}
			
			//Amounts
			
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_INITIAL, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Initial", ExpenditureAmounts.FIELD_INITIAL, fieldName
						, null,Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvement", ExpenditureAmounts.FIELD_MOVEMENT, fieldName
						, isInvestment(),Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvements Inclus(M)", ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName
						, isInvestment(),Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel(A)", ExpenditureAmounts.FIELD_ACTUAL, fieldName
						, isInvestment(),Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel Calculé", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName
						, isInvestment(),Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_AVAILABLE, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Disponible", ExpenditureAmounts.FIELD_AVAILABLE
						, fieldName,isInvestment(), Boolean.FALSE, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation(V)", ExpenditureAmounts.FIELD_ADJUSTMENT
						, fieldName, isInvestment(),adjustmentEditable, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "A+V", ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
						, fieldName, isInvestment(),adjustmentEditable, filterController.getExpendituresAmountsSum());
			else if(Helper.isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName))
				Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Collectif(A-M+V)", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName
						, isInvestment(),Boolean.FALSE, filterController.getExpendituresAmountsSum());
			
			return map;
		}
		/*
		@Override
		public String getStyleClassByRecord(Object record, Integer recordIndex) {
			if(record instanceof Expenditure) {
				Expenditure expenditure = (Expenditure) record;
				if(!ExpenditureBusiness.isAvailableEnoughForAdjustment(expenditure.getPaymentCredit(Boolean.TRUE).getAvailable(),expenditure.getPaymentCredit(Boolean.TRUE).getAdjustment())
						|| (Boolean.TRUE.equals(isInvestment(filterController)) && !ExpenditureBusiness.isAvailableEnoughForAdjustment(expenditure.getEntryAuthorization(Boolean.TRUE).getAvailable()
								,expenditure.getEntryAuthorization(Boolean.TRUE).getAdjustment()) )
						) {
					return "cyk-background-highlight-"+(recordIndex % 2 == 0 ? "even" : "odd");
				}
			}
			return super.getStyleClassByRecord(record, recordIndex);
		}
		*/

		private Boolean isInvestment() {
			if(filterController == null)
				return null;
			return filterController.isInvestment();
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<Expenditure> implements Serializable {
		
		private ExpenditureFilterController filterController;
		
		public LazyDataModel() {
			setEntityClass(Expenditure.class);
		}
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return List.of(ExpenditureDto.JSONS_STRINGS,ExpenditureDto.JSONS_AMOUTNS,ExpenditureDto.JSON___AUDIT__);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return ExpenditureFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
		
		@Override
		protected void process(Expenditure expenditure) {
			super.process(expenditure);
			if(expenditure.getEntryAuthorization() == null)
				expenditure.setEntryAuthorization(new EntryAuthorization());
			if(expenditure.getPaymentCredit() == null)
				expenditure.setPaymentCredit(new PaymentCredit());
		}
	}
	
	public static final String OUTCOME = "expenditureListView";
}