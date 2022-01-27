package ci.gouv.dgbf.system.collectif.client.expenditure;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.collectif.client.ActivitySelectionController;
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
		return filterController.generateWindowTitleValue("Lignes budgétaires");
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(ExpenditureFilterController.class,filterController);
		//dataTable.setHeaderToolbarLeftCommands(null);
		//dataTable.setRecordMenu(null);
		//dataTable.setRecordCommands(null);
		//dataTable.setMenuColumn(null);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		/*
		
		filterController = (ExpenditureFilterController) lazyDataModelListenerImpl.getFilterController();
		if(filterController == null)
			lazyDataModelListenerImpl.setFilterController(filterController = new ExpenditureFilterController());		
		lazyDataModelListenerImpl.enableFilterController();
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		filterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Expenditure.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());	
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);
		dataTable.getOrderNumberColumn().setWidth("60");
		*/
		
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
				map.put(Column.FIELD_VISIBLE, isInvestment(filterController));
			}else if(Expenditure.FIELD_LESSOR_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Bailleur");
				map.put(Column.FIELD_VISIBLE, isInvestment(filterController));
			}else if(Expenditure.FIELD___AUDIT__.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
				map.put(Column.FIELD_WIDTH, "200");
			}
			
			//Amounts
			
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_INITIAL, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Initial", ExpenditureAmounts.FIELD_INITIAL, fieldName
						, null,Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvement", ExpenditureAmounts.FIELD_MOVEMENT, fieldName
						, isInvestment(filterController),Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvements Inclus(M)", ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED, fieldName
						, isInvestment(filterController),Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel(A)", ExpenditureAmounts.FIELD_ACTUAL, fieldName
						, isInvestment(filterController),Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel Calculé", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName
						, isInvestment(filterController),Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_AVAILABLE, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Disponible", ExpenditureAmounts.FIELD_AVAILABLE
						, fieldName,isInvestment(filterController), Boolean.FALSE, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation(V)", ExpenditureAmounts.FIELD_ADJUSTMENT
						, fieldName, isInvestment(filterController),adjustmentEditable, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "A+V", ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
						, fieldName, isInvestment(filterController),adjustmentEditable, filterController);
			else if(isEntryAuthorizationOrPaymentCredit(ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Collectif(A-M+V)", ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName
						, isInvestment(filterController),Boolean.FALSE, filterController);
			
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
		/*@Override
		public String getTooltipByRecord(Object record, Integer recordIndex) {
			if(record instanceof Expenditure) {
				Expenditure expenditure = (Expenditure) record;
				if(Boolean.TRUE.equals(isInvestment(filterController)))					
					return String.format(ROW_TOOLTIP_INVESTMENT_FORMAT
							,NumberHelper.format(expenditure.getEntryAuthorization(Boolean.TRUE).getInitial())
							,NumberHelper.format(expenditure.getPaymentCredit(Boolean.TRUE).getInitial())
							,NumberHelper.format(expenditure.getEntryAuthorization(Boolean.TRUE).getMovement())
							,NumberHelper.format(expenditure.getPaymentCredit(Boolean.TRUE).getMovement())
							,NumberHelper.format(expenditure.getEntryAuthorization(Boolean.TRUE).getMovementIncluded())
							,NumberHelper.format(expenditure.getPaymentCredit(Boolean.TRUE).getMovementIncluded())
							);
				else
					return String.format(ROW_TOOLTIP_NOT_INVESTMENT_FORMAT,NumberHelper.format(expenditure.getEntryAuthorization(Boolean.TRUE).getMovementIncluded()));
			}	
			return super.getTooltipByRecord(record, recordIndex);
		}
		*/
		private static Boolean isInvestment(ExpenditureFilterController filterController) {
			if(filterController == null)
				return null;
			return filterController.isInvestment();
		}
		
		//private static final String ROW_TOOLTIP_INVESTMENT_FORMAT = "Budget Initial AE|CP : %s|%s , Mouvements AE|CP : %s|%s , Mouvements Inclus AE|CP : %s|%s";
		//private static final String ROW_TOOLTIP_NOT_INVESTMENT_FORMAT = "Mouvements Inclus : %s";
		
		private static Boolean isEntryAuthorizationOrPaymentCredit(String amountTypeFieldName,String amountValueFieldName,String fieldName) {
			Boolean value = FieldHelper.join(amountTypeFieldName,amountValueFieldName).equals(fieldName) 
					|| (amountTypeFieldName+StringHelper.applyCase(amountValueFieldName, Case.FIRST_CHARACTER_UPPER)).equals(fieldName);
			return value;
		}
		
		private static Boolean isEntryAuthorizationOrPaymentCredit(String amountValueFieldName,String fieldName) {
			Boolean value = isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_ENTRY_AUTHORIZATION,amountValueFieldName, fieldName) 
					|| isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_PAYMENT_CREDIT,amountValueFieldName, fieldName);			
			return value;
		}

		private static void setAmountColumnArgumentsMap(Map<Object,Object> map,String name,String fieldName1,String fieldName2,Boolean editable,Expenditure expenditureAmountsSum) {
			map.put(Column.FIELD_VALUE_TYPE, Value.Type.CURRENCY);
			map.put(Column.FIELD_WIDTH, "100");
			map.put(Column.FIELD_HEADER_TEXT, name);
			map.put(Column.FIELD_VISIBLE, VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME.contains(fieldName2));
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, editable);
			map.put(Column.ConfiguratorImpl.FIELD_SHOW_FOOTER, Boolean.TRUE);
			map.put(Column.ConfiguratorImpl.FIELD_FOOTER_OUTPUT_TEXT_VALUE, getAmount(expenditureAmountsSum, fieldName1, fieldName2));
		}
		
		private static void setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(Map<Object,Object> map,String name,String amountValueFieldName,String fieldName
				,Boolean both,Boolean editable,ExpenditureFilterController filterController) {
			Expenditure expenditureAmountsSum = filterController == null ? null : filterController.sumExpendituresAmounts();
			if(Boolean.TRUE.equals(both)) {
				if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_ENTRY_AUTHORIZATION,amountValueFieldName,fieldName))
					setAmountColumnArgumentsMap(map, name+" A.E.", Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, expenditureAmountsSum);
				else if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_PAYMENT_CREDIT,amountValueFieldName,fieldName))
					setAmountColumnArgumentsMap(map, name+" C.P.", Expenditure.FIELD_PAYMENT_CREDIT, amountValueFieldName, editable, expenditureAmountsSum);
			}else {
				setAmountColumnArgumentsMap(map, name, Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, expenditureAmountsSum);
			}
		}
		
		private static Number getAmount(Expenditure expenditure,String fieldName1,String fieldName2) {
			if(expenditure == null)
				return null;			
			ExpenditureAmounts amounts = (ExpenditureAmounts) FieldHelper.read(expenditure, fieldName1);
			if(amounts == null)
				return null;
			return (Number) FieldHelper.read(amounts, fieldName2);
		}
		
		private static final Collection<String> VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME = List.of(/*ExpenditureAmounts.FIELD_INITIAL,ExpenditureAmounts.FIELD_MOVEMENT
				,*/ExpenditureAmounts.FIELD_ACTUAL,ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED,ExpenditureAmounts.FIELD_ADJUSTMENT,ExpenditureAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
				,ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT);
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