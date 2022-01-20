package ci.gouv.dgbf.system.collectif.client.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.SpecificServiceGetter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.collectif.server.api.service.ResourceDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.Revenue;
import ci.gouv.dgbf.system.collectif.server.client.rest.Resource;
import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceAmounts;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ResourceListPage extends AbstractEntityListPageContainerManagedImpl<Resource> implements Serializable {

	private ResourceFilterController filterController;
	@Inject private SpecificServiceGetter specificServiceGetter;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ResourceFilterController();   
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue(ci.gouv.dgbf.system.collectif.server.api.persistence.Resource.NAME_PLURAL);
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(ResourceFilterController.class,filterController);
		//dataTable.setHeaderToolbarLeftCommands(null);
		//dataTable.setRecordMenu(null);
		//dataTable.setRecordCommands(null);
		//dataTable.setMenuColumn(null);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		/*
		
		filterController = (ResourceFilterController) lazyDataModelListenerImpl.getFilterController();
		if(filterController == null)
			lazyDataModelListenerImpl.setFilterController(filterController = new ResourceFilterController());		
		lazyDataModelListenerImpl.enableFilterController();
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		filterController.getActivitySelectionController().getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Resource.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());	
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);
		dataTable.getOrderNumberColumn().setWidth("60");
		*/
		
		if(arguments == null) 
			arguments = new HashMap<>();
		ResourceFilterController filterController = (ResourceFilterController) MapHelper.readByKey(arguments, ResourceFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (ResourceFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new ResourceFilterController());		
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Resource.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("60");
		
		return dataTable;
	}
	
	public static DataTable buildDataTable(Object...objects) {
		return buildDataTable(ArrayHelper.isEmpty(objects) ? null : MapHelper.instantiate(objects));
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		private ResourceFilterController filterController;
		//private Resource resourceAmountsSum;
		//private Boolean showCodeOnlyWherePossible;
		private Boolean adjustmentEditable/*,amountsColumnsFootersShowable/*,entryAuthorizationAndPaymentCreditShowable*/;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(Resource.FIELD_BUDGETARY_ACT_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Resource.FIELD_BUDGETARY_ACT_VERSION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Resource.FIELD_SECTION_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.Section.NAME);
				map.put(Column.FIELD_WIDTH, "70");
			}else if(Resource.FIELD_BUDGET_SPECIALIZATION_UNIT_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.BudgetSpecializationUnit.INITALS);
				map.put(Column.FIELD_WIDTH, "60");
			}else if(Resource.FIELD_ACTIVITY_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.ResourceActivity.NAME);
				map.put(Column.FIELD_WIDTH, "100");
			}else if(Resource.FIELD_ECONOMIC_NATURE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, ci.gouv.dgbf.system.collectif.server.api.persistence.EconomicNature.INITIALS);
			}else if(Resource.FIELD___AUDIT__.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Audit");
				map.put(Column.FIELD_VISIBLE, Boolean.FALSE);
				map.put(Column.FIELD_WIDTH, "200");
			}
			
			//Amounts
			
			else if(isRevenue(ResourceAmounts.FIELD_INITIAL, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Initial", ResourceAmounts.FIELD_INITIAL, fieldName,Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_MOVEMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvement", ResourceAmounts.FIELD_MOVEMENT, fieldName,Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_MOVEMENT_INCLUDED, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvements Inclus(M)", ResourceAmounts.FIELD_MOVEMENT_INCLUDED, fieldName,Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_ACTUAL, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel(A)", ResourceAmounts.FIELD_ACTUAL, fieldName,Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel Calculé", ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName,Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_AVAILABLE, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Disponible", ResourceAmounts.FIELD_AVAILABLE, fieldName, Boolean.FALSE, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation(V)", ResourceAmounts.FIELD_ADJUSTMENT, fieldName, adjustmentEditable, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "A+V", ResourceAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName, adjustmentEditable, filterController);
			else if(isRevenue(ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName))
				setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Collectif(A-M+V)", ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName,Boolean.FALSE, filterController);
			
			return map;
		}
		/*
		@Override
		public String getStyleClassByRecord(Object record, Integer recordIndex) {
			if(record instanceof Resource) {
				Resource resource = (Resource) record;
				if(!ResourceBusiness.isAvailableEnoughForAdjustment(resource.getPaymentCredit(Boolean.TRUE).getAvailable(),resource.getPaymentCredit(Boolean.TRUE).getAdjustment())
						|| (Boolean.TRUE.equals(isInvestment(filterController)) && !ResourceBusiness.isAvailableEnoughForAdjustment(resource.getEntryAuthorization(Boolean.TRUE).getAvailable()
								,resource.getEntryAuthorization(Boolean.TRUE).getAdjustment()) )
						) {
					return "cyk-background-highlight-"+(recordIndex % 2 == 0 ? "even" : "odd");
				}
			}
			return super.getStyleClassByRecord(record, recordIndex);
		}
		*/
		/*@Override
		public String getTooltipByRecord(Object record, Integer recordIndex) {
			if(record instanceof Resource) {
				Resource resource = (Resource) record;
				if(Boolean.TRUE.equals(isInvestment(filterController)))					
					return String.format(ROW_TOOLTIP_INVESTMENT_FORMAT
							,NumberHelper.format(resource.getEntryAuthorization(Boolean.TRUE).getInitial())
							,NumberHelper.format(resource.getPaymentCredit(Boolean.TRUE).getInitial())
							,NumberHelper.format(resource.getEntryAuthorization(Boolean.TRUE).getMovement())
							,NumberHelper.format(resource.getPaymentCredit(Boolean.TRUE).getMovement())
							,NumberHelper.format(resource.getEntryAuthorization(Boolean.TRUE).getMovementIncluded())
							,NumberHelper.format(resource.getPaymentCredit(Boolean.TRUE).getMovementIncluded())
							);
				else
					return String.format(ROW_TOOLTIP_NOT_INVESTMENT_FORMAT,NumberHelper.format(resource.getEntryAuthorization(Boolean.TRUE).getMovementIncluded()));
			}	
			return super.getTooltipByRecord(record, recordIndex);
		}
		*/
		
		
		//private static final String ROW_TOOLTIP_INVESTMENT_FORMAT = "Budget Initial AE|CP : %s|%s , Mouvements AE|CP : %s|%s , Mouvements Inclus AE|CP : %s|%s";
		//private static final String ROW_TOOLTIP_NOT_INVESTMENT_FORMAT = "Mouvements Inclus : %s";
		
		private static Boolean isRevenue(String amountTypeFieldName,String amountValueFieldName,String fieldName) {
			Boolean value = FieldHelper.join(amountTypeFieldName,amountValueFieldName).equals(fieldName) 
					|| (amountTypeFieldName+StringHelper.applyCase(amountValueFieldName, Case.FIRST_CHARACTER_UPPER)).equals(fieldName);
			return value;
		}
		
		private static Boolean isRevenue(String amountValueFieldName,String fieldName) {
			Boolean value = isRevenue(Resource.FIELD_REVENUE,amountValueFieldName, fieldName);			
			return value;
		}

		private static void setAmountColumnArgumentsMap(Map<Object,Object> map,String name,String fieldName1,String fieldName2,Boolean editable,Resource resourceAmountsSum) {
			map.put(Column.FIELD_VALUE_TYPE, Value.Type.CURRENCY);
			map.put(Column.FIELD_WIDTH, "100");
			map.put(Column.FIELD_HEADER_TEXT, name);
			map.put(Column.FIELD_VISIBLE, VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME.contains(fieldName2));
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, editable);
			map.put(Column.ConfiguratorImpl.FIELD_SHOW_FOOTER, Boolean.TRUE);
			map.put(Column.ConfiguratorImpl.FIELD_FOOTER_OUTPUT_TEXT_VALUE, getAmount(resourceAmountsSum, fieldName1, fieldName2));
		}
		
		private static void setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(Map<Object,Object> map,String name,String amountValueFieldName,String fieldName
				,Boolean editable,ResourceFilterController filterController) {
			Resource resourceAmountsSum = filterController == null ? null : filterController.sumResourcesAmounts();			
			setAmountColumnArgumentsMap(map, name, Resource.FIELD_REVENUE, amountValueFieldName, editable, resourceAmountsSum);
		}
		
		private static Number getAmount(Resource resource,String fieldName1,String fieldName2) {
			if(resource == null)
				return null;			
			ResourceAmounts amounts = (ResourceAmounts) FieldHelper.read(resource, fieldName1);
			if(amounts == null)
				return null;
			return (Number) FieldHelper.read(amounts, fieldName2);
		}
		
		private static final Collection<String> VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME = List.of(/*ResourceAmounts.FIELD_INITIAL,ResourceAmounts.FIELD_MOVEMENT
				,*/ResourceAmounts.FIELD_ACTUAL,ResourceAmounts.FIELD_MOVEMENT_INCLUDED,ResourceAmounts.FIELD_ADJUSTMENT,ResourceAmounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
				,ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT);
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<Resource> implements Serializable {
		
		private ResourceFilterController filterController;
		
		public LazyDataModel() {
			setEntityClass(Resource.class);
		}
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return List.of(ResourceDto.JSONS_STRINGS,ResourceDto.JSONS_AMOUTNS,ResourceDto.JSON___AUDIT__);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return ResourceFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
		
		@Override
		protected void process(Resource resource) {
			super.process(resource);
			if(resource.getRevenue() == null)
				resource.setRevenue(new Revenue());
		}
	}
	
	public static final String OUTCOME = "resourceListView";
}