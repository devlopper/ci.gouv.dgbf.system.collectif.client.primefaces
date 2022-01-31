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
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.ExerciseDto;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.Amounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.Exercise;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExerciseController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;

public interface Helper {

	public static Exercise getExerciseFromRequestParameter() {
		return DependencyInjection.inject(ExerciseController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.EXERCISE_IDENTIFIER), new Controller.GetArguments()
				.projections(ExerciseDto.JSON_IDENTIFIER,ExerciseDto.JSON_CODE,ExerciseDto.JSON_NAME,ExerciseDto.JSON_YEAR));
	}
	
	public static LegislativeAct getLegislativeActFromRequestParameter(LegislativeActVersion version) {
		if(version != null && version.getAct() != null)
			return version.getAct();
		return DependencyInjection.inject(LegislativeActController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_IDENTIFIER), null);
	}
	
	public static LegislativeActVersion getLegislativeActVersionFromRequestParameter(String identifier,Boolean computeSumsAndTotal) {
		Controller.GetArguments arguments = new Controller.GetArguments();
		arguments.setProjections(List.of(LegislativeActVersionDto.JSON_IDENTIFIER,LegislativeActVersionDto.JSON_CODE,LegislativeActVersionDto.JSON_NAME,LegislativeActVersionDto.JSON_LEGISLATIVE_ACT
				,LegislativeActVersionDto.JSONS_GENERATED_ACT_COUNT_ACT_GENERATABLE_GENERATED_ACT_DELETABLE));
		if(StringHelper.isBlank(identifier)) {
			arguments.setFilter(new Filter.Dto().addField(Parameters.LATEST_LEGISLATIVE_ACT_VERSION, Boolean.TRUE));
			return DependencyInjection.inject(LegislativeActVersionController.class).getOne(arguments);
		}
		return DependencyInjection.inject(LegislativeActVersionController.class).getByIdentifier(identifier, arguments);
	}
	
	public static LegislativeActVersion getLegislativeActVersionFromRequestParameter(Boolean computeSumsAndTotal) {
		return getLegislativeActVersionFromRequestParameter(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER)
				, computeSumsAndTotal);
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
	
	public static void setAmountColumnArgumentsMap(Map<Object,Object> map,String name,String fieldName1,String fieldName2,Boolean editable,Object amountsSum) {
		map.put(Column.FIELD_VALUE_TYPE, Value.Type.CURRENCY);
		map.put(Column.FIELD_WIDTH, "100");
		map.put(Column.FIELD_HEADER_TEXT, name);
		map.put(Column.FIELD_VISIBLE, VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME.contains(fieldName2));
		map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, editable);
		map.put(Column.ConfiguratorImpl.FIELD_SHOW_FOOTER, Boolean.TRUE);
		map.put(Column.ConfiguratorImpl.FIELD_FOOTER_OUTPUT_TEXT_VALUE, readAmount(amountsSum, fieldName1, fieldName2));
	}
	
	public static void setEntryAuthorizationOrPaymentCreditColumnsArgumentsMaps(Map<Object,Object> map,String name,String amountValueFieldName,String fieldName
			,Boolean both,Boolean editable,Object amountsSum) {
		if(Boolean.TRUE.equals(both)) {
			if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_ENTRY_AUTHORIZATION,amountValueFieldName,fieldName))
				setAmountColumnArgumentsMap(map, name+" A.E.", Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, amountsSum);
			else if(isEntryAuthorizationOrPaymentCredit(Expenditure.FIELD_PAYMENT_CREDIT,amountValueFieldName,fieldName))
				setAmountColumnArgumentsMap(map, name+" C.P.", Expenditure.FIELD_PAYMENT_CREDIT, amountValueFieldName, editable, amountsSum);
		}else {
			setAmountColumnArgumentsMap(map, name, Expenditure.FIELD_ENTRY_AUTHORIZATION, amountValueFieldName, editable, amountsSum);
		}
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
	
	Collection<String> VISIBLE_AMOUNTS_COLUMNS_FIELDS_NAME = List.of(/*ExpenditureAmounts.FIELD_INITIAL,ExpenditureAmounts.FIELD_MOVEMENT
			,*/Amounts.FIELD_ACTUAL,Amounts.FIELD_MOVEMENT_INCLUDED,Amounts.FIELD_ADJUSTMENT,Amounts.FIELD_ACTUAL_PLUS_ADJUSTMENT,Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT);
}