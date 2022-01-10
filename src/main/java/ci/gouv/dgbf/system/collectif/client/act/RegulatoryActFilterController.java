package ci.gouv.dgbf.system.collectif.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueConverter;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetaryActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.RegulatoryAct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class RegulatoryActFilterController extends AbstractFilterController implements Serializable {

private SelectOneCombo budgetaryActSelectOne,budgetaryActVersionSelectOne,includedSelectOne;
	
	private BudgetaryAct budgetaryActInitial;
	private BudgetaryActVersion budgetaryActVersionInitial;
	private Boolean includedInitial;
	
	public RegulatoryActFilterController() {
		if(budgetaryActVersionInitial == null)
			budgetaryActVersionInitial = Helper.getBudgetaryActVersionFromRequestParameter(null);
		if(budgetaryActInitial == null)
			budgetaryActInitial = Helper.getBudgetaryActFromRequestParameter(budgetaryActVersionInitial);
		includedInitial = ValueConverter.getInstance().convertToBoolean(buildParameterName(RegulatoryAct.FIELD_INCLUDED));
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_BUDGETARY_ACT_SELECT_ONE.equals(fieldName))
			return budgetaryActInitial;
		if(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return budgetaryActVersionInitial;
		if(FIELD_INCLUDED_SELECT_ONE.equals(fieldName))
			return includedInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_BUDGETARY_ACT_SELECT_ONE, BudgetaryActVersion.class);
		buildInputSelectOne(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE, BudgetaryActVersion.class);
		buildInputSelectOne(FIELD_INCLUDED_SELECT_ONE, Boolean.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		budgetaryActSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,budgetaryActVersionSelectOne));
		budgetaryActVersionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		budgetaryActSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_BUDGETARY_ACT_SELECT_ONE.equals(fieldName))
			return buildBudgetaryActSelectOne((BudgetaryAct) value);
		if(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return buildBudgetaryActVersionSelectOne((BudgetaryActVersion) value);
		if(FIELD_INCLUDED_SELECT_ONE.equals(fieldName))
			return buildIncludedSelectOne((Boolean) value);
		return null;
	}
	
	private SelectOneCombo buildBudgetaryActSelectOne(BudgetaryAct budgetaryAct) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,budgetaryAct,SelectOneCombo.FIELD_CHOICE_CLASS,BudgetaryAct.class
				,SelectOneCombo.FIELD_CHOICES,BudgetaryAct.buildChoices(),SelectOneCombo.FIELD_CHOICES_INITIALIZED,Boolean.TRUE,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<BudgetaryAct>() {
			
			@Override
			public void select(AbstractInputChoiceOne input, BudgetaryAct budgetaryAct) {
				super.select(input, budgetaryAct);
				if(budgetaryActVersionSelectOne != null)  
					budgetaryActVersionSelectOne.updateChoices();
				budgetaryActVersionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier(); 
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Acte");
		//input.setValueAsFirstChoiceIfNull();
		//input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildBudgetaryActVersionSelectOne(BudgetaryActVersion budgetaryActVersion) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,budgetaryActVersion,SelectOneCombo.FIELD_CHOICE_CLASS,BudgetaryActVersion.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<BudgetaryActVersion>() {
			@Override
			protected Collection<BudgetaryActVersion> __computeChoices__(AbstractInputChoice<BudgetaryActVersion> input, Class<?> entityClass) {
				if(AbstractInput.getValue(budgetaryActSelectOne) == null)
					return null;
				BudgetaryAct budgetaryAct = (BudgetaryAct) budgetaryActSelectOne.getValue();
				Collection<BudgetaryActVersion> choices = DependencyInjection.inject(BudgetaryActVersionController.class).getByBudgetaryActIdentifier(budgetaryAct.getIdentifier());
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			
			@Override
			public void select(AbstractInputChoiceOne input, BudgetaryActVersion budgetaryActVersion) {
				super.select(input, budgetaryActVersion);
				
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Version");
		return input;
	}
	
	private SelectOneCombo buildIncludedSelectOne(Boolean included) {
		return SelectOneCombo.buildUnknownYesNoOnly((Boolean) included, "Inclus");
	}
	
	@Override
	protected Boolean isInputValueNotNull(AbstractInput<?> input) {
		if(input == includedSelectOne)
			return Boolean.TRUE;
		return super.isInputValueNotNull(input);
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(BudgetaryAct.class.equals(klass))
			return AbstractInput.getValue(budgetaryActVersionSelectOne) == null;
		if(BudgetaryActVersion.class.equals(klass))
			return Boolean.TRUE;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(RegulatoryAct.FIELD_INCLUDED.equals(fieldName) || input == includedSelectOne)
			return Parameters.REGULATORY_ACT_INCLUDED;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected String buildParameterValue(AbstractInput<?> input) {
		if(input == includedSelectOne)
			return input.getValue() == null ? null : input.getValue().toString();
		return super.buildParameterValue(input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(budgetaryActSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetaryActSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetaryActSelectOne,Cell.FIELD_WIDTH,4));	
		}
		
		if(budgetaryActVersionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetaryActVersionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetaryActVersionSelectOne,Cell.FIELD_WIDTH,2));
		}
		
		if(includedSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,includedSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,includedSelectOne,Cell.FIELD_WIDTH,2));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,1));	
		return cellsMaps;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(budgetaryActInitial != null) {
			strings.add(budgetaryActInitial.getName());
		}		
		if(budgetaryActVersionInitial != null) {
			strings.add(budgetaryActVersionInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	
	public String generateWindowTitleValue() {
		StringBuilder stringBuilder = new StringBuilder("Acte de gestion");
		if(budgetaryActInitial == null)
			return stringBuilder.toString();		
		if(budgetaryActVersionInitial == null) {
			stringBuilder.append(" disponible dans le "+budgetaryActInitial.getName());
			return stringBuilder.toString();
		}		
		if(includedInitial != null) {
			if(includedInitial)
				stringBuilder.append(" inclus ");
			else
				stringBuilder.append(" non inclus ");	
		}	
		stringBuilder.append("dans le "+budgetaryActInitial.getName()+" | "+budgetaryActVersionInitial.getName());
		return stringBuilder.toString();
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_YEAR,RegulatoryAct.FIELD_NAME,RegulatoryAct.FIELD_ENTRY_AUTHORIZATION_AMOUNT,RegulatoryAct.FIELD_PAYMENT_CREDIT_AMOUNT));
		if(includedInitial == null)
			columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_INCLUDED_AS_STRING));
		return columnsFieldsNames;
	}
	
	public BudgetaryAct getBudgetaryAct() {
		return (BudgetaryAct) AbstractInput.getValue(budgetaryActSelectOne);
	}
	
	public BudgetaryActVersion getBudgetaryActVersion() {
		return (BudgetaryActVersion) AbstractInput.getValue(budgetaryActVersionSelectOne);
	}
	
	public Boolean getIncluded() {
		return (Boolean) AbstractInput.getValue(includedSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,RegulatoryActFilterController controller,Boolean initial) {
		/*BudgetaryActVersion budgetaryActVersion = Boolean.TRUE.equals(initial) ? controller.budgetaryActVersionInitial : controller.getBudgetaryActVersion();
		if(budgetaryActVersion == null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.BUDGETARY_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.budgetaryActInitial : controller.getBudgetaryAct()), filter);
		else
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.BUDGETARY_ACT_VERSION_IDENTIFIER, FieldHelper.readSystemIdentifier(budgetaryActVersion), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.REGULATORY_ACT_INCLUDED, controller.getIncluded(), filter);
		*/
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(RegulatoryActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_BUDGETARY_ACT_SELECT_ONE = "budgetaryActSelectOne";
	public static final String FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE = "budgetaryActVersionSelectOne";
	public static final String FIELD_INCLUDED_SELECT_ONE = "includedSelectOne";
}