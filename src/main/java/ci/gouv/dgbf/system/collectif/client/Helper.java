package ci.gouv.dgbf.system.collectif.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.output.OutputText;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.BudgetCategoryDto;
import ci.gouv.dgbf.system.collectif.server.api.service.ExerciseDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.Amounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetCategory;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetCategoryController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Exercise;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExerciseController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;

public interface Helper {

	public static Exercise getExerciseFromRequestParameter() {
		return DependencyInjection.inject(ExerciseController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.EXERCISE_IDENTIFIER), new Controller.GetArguments()
				.projections(ExerciseDto.JSON_IDENTIFIER,ExerciseDto.JSON_CODE,ExerciseDto.JSON_NAME,ExerciseDto.JSON_YEAR));
	}
	
	public static BudgetCategory getBudgetCategoryFromRequestParameter(String identifier) {
		Controller.GetArguments arguments = new Controller.GetArguments() {
			@Override
			public void listenIdentifierIsBlank() {
				setFilter(new Filter.Dto().addField(Parameters.DEFAULT_VALUE, Boolean.TRUE));
			}
		};
		arguments.setIdentifierBlankable(Boolean.TRUE);
		arguments.setProjections(List.of(BudgetCategoryDto.JSON_IDENTIFIER,BudgetCategoryDto.JSON_CODE,BudgetCategoryDto.JSON_NAME));
		return DependencyInjection.inject(BudgetCategoryController.class).getByIdentifier(identifier, arguments);
	}
	
	public static Boolean isEntryAuthorizationOrPaymentCredit(String amountTypeFieldName,String amountValueFieldName,String fieldName) {
		Boolean value = FieldHelper.join(amountTypeFieldName,amountValueFieldName).equals(fieldName) 
				|| (amountTypeFieldName+StringHelper.applyCase(amountValueFieldName, Case.FIRST_CHARACTER_UPPER)).equals(fieldName);
		return value;
	}
	
	public static Boolean isEntryAuthorizationOrPaymentCredit(String amountValueFieldName,String fieldName) {
		Boolean value = isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_ENTRY_AUTHORIZATION,amountValueFieldName, fieldName) 
				|| isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_PAYMENT_CREDIT,amountValueFieldName, fieldName);			
		return value;
	}
	
	public static void setAmountColumnArgumentsMap(Map<Object,Object> map,String name,String title,String fieldName1,String fieldName2,Boolean editable,Object amountsSum) {
		map.put(Column.FIELD_VALUE_TYPE, Value.Type.CURRENCY);
		map.put(Column.FIELD_WIDTH, "120");
		map.put(Column.FIELD_HEADER_OUTPUT_TEXT, OutputText.build(OutputText.FIELD_VALUE,name,OutputText.FIELD_TITLE,ValueHelper.defaultToIfBlank(title, name)/*,OutputText.FIELD_STYLE,"color:red;font-size: 80%;"*/));
		map.put(Column.FIELD_VISIBLE, VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME.contains(fieldName2));
		map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, editable);
		map.put(Column.ConfiguratorImpl.FIELD_SHOW_FOOTER, Boolean.TRUE);
		map.put(Column.ConfiguratorImpl.FIELD_LISTENER, new Column.Listener.AbstractImpl() {
			@Override
			public Object readFooterValueFromMaster(Object master, String fieldName) {
				if(master == null)
					return null;
				if(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT.equals(fieldName) && ((Expenditure)master).getEntryAuthorization() != null)
					return ((Expenditure)master).getEntryAuthorization().getAdjustment();
				if(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT.equals(fieldName) && ((Expenditure)master).getPaymentCredit() != null)
					return ((Expenditure)master).getPaymentCredit().getAdjustment();
				return super.readFooterValueFromMaster(master, fieldName);
			}
		});
		//map.put(Column.ConfiguratorImpl.FIELD_FOOTER_OUTPUT_TEXT_VALUE, readAmount(amountsSum, fieldName1, fieldName2));
	}
	
	public static void setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(Map<Object,Object> map,String name,String title,String amountValueFieldName,String fieldName
			,Boolean both,Boolean editable,Object amountsSum) {
		if(Boolean.TRUE.equals(both)) {
			if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_ENTRY_AUTHORIZATION,amountValueFieldName,fieldName))
				setAmountColumnArgumentsMap(map, name+" A.E.",title, Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, amountsSum);
			else if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_PAYMENT_CREDIT,amountValueFieldName,fieldName))
				setAmountColumnArgumentsMap(map, name+" C.P.",title, Expenditure.FIELD_PAYMENT_CREDIT, amountValueFieldName, editable, amountsSum);
		}else {
			setAmountColumnArgumentsMap(map, name,title, Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, amountsSum);
		}
	}
	
	public static void setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(Map<Object,Object> map,String name,String amountValueFieldName,String fieldName
			,Boolean both,Boolean editable,Object amountsSum) {
		setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, name, name, amountValueFieldName, fieldName, both, editable, amountsSum);
	}
	
	public static Number readAmount(Object instance,String fieldName1,String fieldName2) {
		if(instance == null)
			return null;			
		ExpenditureAmounts amounts = (ExpenditureAmounts) FieldHelper.read(instance, fieldName1);
		if(amounts == null)
			return null;
		return (Number) FieldHelper.read(amounts, fieldName2);
	}
	
	public static void addAmountsColumnsNames(Collection<String> collection,Boolean isEntryAuthorizationAdjustmentEditable,Boolean isInvestment,Collection<String> names) {
		if(collection == null || CollectionHelper.isEmpty(names))
			return;
		for(String name : names) {
			if(ExpenditureAmounts.FIELD_INITIAL.equals(name)) {
				collection.add(FieldHelper.join(Expenditure.FIELD_ENTRY_AUTHORIZATION,name));
			}else if(ExpenditureAmounts.FIELD_ADJUSTMENT.equals(name)) {
				if(Boolean.TRUE.equals(isEntryAuthorizationAdjustmentEditable)) {
					collection.add(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT);
					if(Boolean.TRUE.equals(isInvestment))
						collection.add(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT);
				}else {
					collection.add(FieldHelper.join(Expenditure.FIELD_ENTRY_AUTHORIZATION,name));
					if(Boolean.TRUE.equals(isInvestment))
						collection.add(FieldHelper.join(Expenditure.FIELD_PAYMENT_CREDIT,name));
				}
			}else {
				collection.add(FieldHelper.join(Expenditure.FIELD_ENTRY_AUTHORIZATION,name));
				if(Boolean.TRUE.equals(isInvestment))
					collection.add(FieldHelper.join(Expenditure.FIELD_PAYMENT_CREDIT,name));
			}
		}
	}
	public static void addAmountsColumnsNames(Collection<String> collection,Boolean isEntryAuthorizationAdjustmentEditable,Boolean isInvestment,String...names) {
		if(collection == null || ArrayHelper.isEmpty(names))
			return;
		addAmountsColumnsNames(collection,isEntryAuthorizationAdjustmentEditable,isInvestment, CollectionHelper.listOf(names));
	}
	
	/**/
	
	public interface DataTable {
		
		public interface Amounts {
			
			static void processColumnArguments(Map<Object,Object> map,String fieldName,Boolean both,Boolean editable,Object amountsSum) {
				/*if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_INITIAL, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Initial", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_INITIAL, fieldName
							, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_MOVEMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvement", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_MOVEMENT, fieldName
							, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_MOVEMENT_INCLUDED, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Mouvements Inclus(M)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_MOVEMENT_INCLUDED, fieldName
							, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel(A)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL, fieldName
							, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Actuel Calculé", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED, fieldName
							, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_AVAILABLE, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Disponible", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_AVAILABLE
							, fieldName, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ADJUSTMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation(V)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ADJUSTMENT
							, fieldName, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_EXPECTED_ADJUSTMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Variation attendue(VA)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_EXPECTED_ADJUSTMENT
							, fieldName, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_EXPECTED_ADJUSTMENT_MINUS_ADJUSTMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Gap Variation(GV)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_EXPECTED_ADJUSTMENT_MINUS_ADJUSTMENT
							, fieldName, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_PLUS_ADJUSTMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "A+V", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_PLUS_ADJUSTMENT
							, fieldName, both,editable, amountsSum);
				else if(Helper.isEntryAuthorizationOrPaymentCredit(ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName))
					Helper.setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(map, "Collectif(A-M+V)", ci.gouv.dgbf.system.collectif.server.client.rest.Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT, fieldName
							, both,editable, amountsSum);*/
			}
		}
	}
	
	/**/
	
	Collection<String> VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME = List.of(/*ExpenditureAmounts.FIELD_INITIAL,ExpenditureAmounts.FIELD_MOVEMENT
			,*/Amounts.FIELD_ACTUAL,Amounts.FIELD_MOVEMENT_INCLUDED,Amounts.FIELD_EXPECTED_ADJUSTMENT,Amounts.FIELD_ADJUSTMENT,Amounts.FIELD_EXPECTED_ADJUSTMENT_MINUS_ADJUSTMENT
			,Amounts.FIELD_ACTUAL_PLUS_ADJUSTMENT,Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT);
}