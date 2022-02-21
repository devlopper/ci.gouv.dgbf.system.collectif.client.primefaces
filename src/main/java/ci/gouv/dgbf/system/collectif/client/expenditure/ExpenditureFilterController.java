package ci.gouv.dgbf.system.collectif.client.expenditure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueConverter;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.client.ActivitySelectionController;
import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.ActionDto;
import ci.gouv.dgbf.system.collectif.server.api.service.ActivityDto;
import ci.gouv.dgbf.system.collectif.server.api.service.BudgetSpecializationUnitDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.Action;
import ci.gouv.dgbf.system.collectif.server.client.rest.ActionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Activity;
import ci.gouv.dgbf.system.collectif.server.client.rest.ActivityController;
import ci.gouv.dgbf.system.collectif.server.client.rest.AdministrativeUnit;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetSpecializationUnit;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetSpecializationUnitController;
import ci.gouv.dgbf.system.collectif.server.client.rest.EconomicNature;
import ci.gouv.dgbf.system.collectif.server.client.rest.EconomicNatureController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureNature;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureNatureController;
import ci.gouv.dgbf.system.collectif.server.client.rest.FundingSource;
import ci.gouv.dgbf.system.collectif.server.client.rest.FundingSourceController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Lessor;
import ci.gouv.dgbf.system.collectif.server.client.rest.LessorController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Section;
import ci.gouv.dgbf.system.collectif.server.client.rest.SectionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ExpenditureFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo legislativeActSelectOne,legislativeActVersionSelectOne,sectionSelectOne,expenditureNatureSelectOne,budgetSpecializationUnitSelectOne,actionSelectOne
		,activitySelectOne,economicNatureSelectOne,fundingSourceSelectOne,lessorSelectOne,adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne,availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne;
	private ActivitySelectionController activitySelectionController;
	
	private Boolean isLegislativeActColumnShowable,isLegislativeActVersionColumnShowable,isSectionColumnShowable,isExpenditureNatureColumnShowable
	,isBudgetSpecializationUnitColumnShowable,isActionColumnShowable,isActivityColumnShowable,isFundingSourceColumnShowable,isLessorColumnShowable;
	
	private LegislativeAct legislativeActInitial;
	private LegislativeActVersion legislativeActVersionInitial;
	private Section sectionInitial;
	private AdministrativeUnit administrativeUnitInitial;
	private ExpenditureNature expenditureNatureInitial;
	private BudgetSpecializationUnit budgetSpecializationUnitInitial;
	private Action actionInitial;
	private Activity activityInitial;
	private EconomicNature economicNatureInitial;
	private FundingSource fundingSourceInitial;
	private Lessor lessorInitial;
	private Boolean adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial;
	private Boolean availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial;
	
	private Boolean isEntryAuthorizationAdjustmentEditable;
	private Boolean isPaymentCreditAdjustmentEditable;
	
	private Expenditure expendituresAmountsSum;
	
	public ExpenditureFilterController(Boolean computeLegislativeActVersionSumsAndTotal) {		
		if(legislativeActVersionInitial == null)
			legislativeActVersionInitial = Helper.getLegislativeActVersionFromRequestParameter(computeLegislativeActVersionSumsAndTotal);
		if(legislativeActInitial == null)
			legislativeActInitial = Helper.getLegislativeActFromRequestParameter(legislativeActVersionInitial);
		
		if(activityInitial == null) {
			activityInitial = __inject__(ActivityController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.ACTIVITY_IDENTIFIER)
					,new Controller.GetArguments().projections(ActivityDto.JSON_IDENTIFIER,ActivityDto.JSON_CODE,ActivityDto.JSON_NAME,ActivityDto.JSONS_SECTION_ADMINISTRATIVE_UNIT_EXPENDITURE_NATURE_BUDGET_SPECIALIZATION_UNIT_ACTION
							,ActivityDto.JSON_ECONOMIC_NATURES,ActivityDto.JSON_FUNDING_SOURCES,ActivityDto.JSON_LESSORS));
			if(activityInitial != null) {
				sectionInitial = activityInitial.getSection();
				administrativeUnitInitial = activityInitial.getAdministrativeUnit();
				expenditureNatureInitial = activityInitial.getExpenditureNature();
				budgetSpecializationUnitInitial = activityInitial.getBudgetSpecializationUnit();
				actionInitial = activityInitial.getAction();
			}
		}
		
		if(actionInitial == null) {
			actionInitial = __inject__(ActionController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.ACTION_IDENTIFIER),new Controller.GetArguments().projections(ActionDto.JSON_IDENTIFIER,ActionDto.JSON_CODE
					,ActionDto.JSON_NAME,ActionDto.JSONS_SECTION_BUDGET_SPECIALIZATION_UNIT));
			if(actionInitial != null) {
				sectionInitial = actionInitial.getSection();
				budgetSpecializationUnitInitial = actionInitial.getBudgetSpecializationUnit();
			}
		}
		
		if(budgetSpecializationUnitInitial == null) {
			budgetSpecializationUnitInitial = __inject__(BudgetSpecializationUnitController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER)
					,new Controller.GetArguments().projections(BudgetSpecializationUnitDto.JSON_IDENTIFIER,BudgetSpecializationUnitDto.JSON_CODE,BudgetSpecializationUnitDto.JSON_NAME,BudgetSpecializationUnitDto.JSON_SECTION));
			if(budgetSpecializationUnitInitial != null) {
				sectionInitial = budgetSpecializationUnitInitial.getSection();
			}
		}
		
		if(sectionInitial == null)
			sectionInitial = __inject__(SectionController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.SECTION_IDENTIFIER));

		if(expenditureNatureInitial == null)
			expenditureNatureInitial = __inject__(ExpenditureNatureController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.EXPENDITURE_NATURE_IDENTIFIER));
		
		if(economicNatureInitial == null)
			economicNatureInitial = __inject__(EconomicNatureController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.ECONOMIC_NATURE_IDENTIFIER));
		
		if(fundingSourceInitial == null)
			fundingSourceInitial = __inject__(FundingSourceController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.FUNDING_SOURCE_IDENTIFIER));
		
		if(lessorInitial == null)
			lessorInitial = __inject__(LessorController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.LESSOR_IDENTIFIER));
		
		adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial = ValueConverter.getInstance().convertToBoolean(WebController.getInstance().getRequestParameter(Parameters.ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO));
		availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial = ValueConverter.getInstance().convertToBoolean(WebController.getInstance().getRequestParameter(Parameters.AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO));
		//readExpenditureAountsSum();
	}
	
	public ExpenditureFilterController() {
		this(null);
	}
	
	public Expenditure sumExpendituresAmounts() {
		/*if(expendituresAmountsSum == null)
			expendituresAmountsSum = EntityReader.getInstance().readOne(Expenditure.class, new Arguments<Expenditure>()
				.queryIdentifier(ExpenditureQuerier.QUERY_IDENTIFIER_READ_DYNAMIC_ONE)
				.flags(ExpenditureQuerier.FLAG_SUM_ALL_AMOUNTS).filter(instantiateFilter(this,Boolean.TRUE)));
		*/
		return expendituresAmountsSum;
	}
	
	@Override
	public ExpenditureFilterController build() {
		if(activitySelectionController == null)
			activitySelectionController = new ActivitySelectionController();
		//activitySelectionController.setIsMultiple(Boolean.TRUE);
		activitySelectionController.build();
		return (ExpenditureFilterController) super.build();
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName))
			return legislativeActInitial;
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return legislativeActVersionInitial;
		if(FIELD_SECTION_SELECT_ONE.equals(fieldName))
			return sectionInitial;
		if(FIELD_EXPENDITURE_NATURE_SELECT_ONE.equals(fieldName))
			return expenditureNatureInitial;
		if(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE.equals(fieldName))
			return budgetSpecializationUnitInitial;
		if(FIELD_ACTION_SELECT_ONE.equals(fieldName))
			return actionInitial;
		if(FIELD_ACTIVITY_SELECT_ONE.equals(fieldName))
			return activityInitial;
		if(FIELD_ECONOMIC_NATURE_SELECT_ONE.equals(fieldName))
			return economicNatureInitial;
		if(FIELD_FUNDING_SOURCE_SELECT_ONE.equals(fieldName))
			return fundingSourceInitial;
		if(FIELD_LESSOR_SELECT_ONE.equals(fieldName))
			return lessorInitial;
		if(FIELD_ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO_SELECT_ONE.equals(fieldName))
			return adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial;
		if(FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE.equals(fieldName))
			return availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_SELECT_ONE, LegislativeAct.class);
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE, LegislativeActVersion.class);
		buildInputSelectOne(FIELD_SECTION_SELECT_ONE, Section.class);
		buildInputSelectOne(FIELD_EXPENDITURE_NATURE_SELECT_ONE, ExpenditureNature.class);
		buildInputSelectOne(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE, BudgetSpecializationUnit.class);
		buildInputSelectOne(FIELD_ACTION_SELECT_ONE, Action.class);
		buildInputSelectOne(FIELD_ACTIVITY_SELECT_ONE, Activity.class);
		buildInputSelectOne(FIELD_ECONOMIC_NATURE_SELECT_ONE, EconomicNature.class);
		buildInputSelectOne(FIELD_FUNDING_SOURCE_SELECT_ONE, FundingSource.class);
		buildInputSelectOne(FIELD_LESSOR_SELECT_ONE, Lessor.class);
		buildInputSelectOne(FIELD_ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO_SELECT_ONE, Boolean.class);
		buildInputSelectOne(FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE, Boolean.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,legislativeActVersionSelectOne));
		if(legislativeActVersionSelectOne != null)
			legislativeActVersionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
		sectionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,expenditureNatureSelectOne,budgetSpecializationUnitSelectOne,actionSelectOne,activitySelectOne));
		expenditureNatureSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,activitySelectOne));
		budgetSpecializationUnitSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,actionSelectOne,activitySelectOne));		
		actionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,activitySelectOne));
		activitySelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,economicNatureSelectOne,fundingSourceSelectOne,lessorSelectOne));
		economicNatureSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
		fundingSourceSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
		lessorSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		sectionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName))
			return buildLegislativeActSelectOne((LegislativeAct) value);
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return buildLegislativeActVersionSelectOne((LegislativeActVersion) value);
		if(FIELD_SECTION_SELECT_ONE.equals(fieldName))
			return buildSectionSelectOne((Section) value);
		if(FIELD_EXPENDITURE_NATURE_SELECT_ONE.equals(fieldName))
			return buildExpenditureNatureSelectOne((ExpenditureNature) value);
		if(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE.equals(fieldName))
			return buildBudgetSpecializationUnitSelectOne((BudgetSpecializationUnit) value);
		if(FIELD_ACTION_SELECT_ONE.equals(fieldName))
			return buildActionSelectOne((Action) value);
		if(FIELD_ACTIVITY_SELECT_ONE.equals(fieldName))
			return buildActivitySelectOne((Activity) value);
		if(FIELD_ECONOMIC_NATURE_SELECT_ONE.equals(fieldName))
			return buildEconomicNatureSelectOne((EconomicNature) value);
		if(FIELD_FUNDING_SOURCE_SELECT_ONE.equals(fieldName))
			return buildFundingSourceSelectOne((FundingSource) value);
		if(FIELD_LESSOR_SELECT_ONE.equals(fieldName))
			return buildLessorSelectOne((Lessor) value);
		if(FIELD_ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO_SELECT_ONE.equals(fieldName))
			return buildAdjustmentsNotEqualZeroOrIncludedMovementNotEqualZero((Boolean) value);
		if(FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE.equals(fieldName))
			return buildAvailableMinusIncludedMovementPlusAdjustmentLessThanZero((Boolean) value);
		return null;
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(input == legislativeActSelectOne)
			return Parameters.LEGISLATIVE_ACT_IDENTIFIER;
		if(input == legislativeActVersionSelectOne)
			return Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER;
		if(input == sectionSelectOne)
			return Parameters.SECTION_IDENTIFIER;
		if(input == expenditureNatureSelectOne)
			return Parameters.EXPENDITURE_NATURE_IDENTIFIER;
		if(input == budgetSpecializationUnitSelectOne)
			return Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER;
		if(input == actionSelectOne)
			return Parameters.ACTION_IDENTIFIER;
		if(input == activitySelectOne)
			return Parameters.ACTIVITY_IDENTIFIER;
		if(input == economicNatureSelectOne)
			return Parameters.ECONOMIC_NATURE_IDENTIFIER;
		if(input == fundingSourceSelectOne)
			return Parameters.FUNDING_SOURCE_IDENTIFIER;
		if(input == lessorSelectOne)
			return Parameters.LESSOR_IDENTIFIER;
		if(FIELD_ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO_SELECT_ONE.equals(fieldName) || input == adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne)
			return Parameters.ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO;
		if(FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE.equals(fieldName) || input == availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne)
			return Parameters.AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected String buildParameterValue(AbstractInput<?> input) {
		if(input == adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne)
			return input.getValue() == null ? null : input.getValue().toString();
		if(input == availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne)
			return input.getValue() == null ? null : input.getValue().toString();
		return super.buildParameterValue(input);
	}
	
	private SelectOneCombo buildLegislativeActSelectOne(LegislativeAct legislativeAct) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,legislativeAct,SelectOneCombo.FIELD_CHOICE_CLASS,LegislativeAct.class
				,SelectOneCombo.FIELD_CHOICES,LegislativeAct.buildChoices(),SelectOneCombo.FIELD_CHOICES_INITIALIZED,Boolean.TRUE,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<LegislativeAct>() {
			
			@Override
			public void select(AbstractInputChoiceOne input, LegislativeAct legislativeAct) {
				super.select(input, legislativeAct);
				if(legislativeActVersionSelectOne != null)  
					legislativeActVersionSelectOne.updateChoices();
				legislativeActVersionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier(); 
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Acte");
		//input.setValueAsFirstChoiceIfNull();
		//input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildLegislativeActVersionSelectOne(LegislativeActVersion legislativeActVersion) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,legislativeActVersion,SelectOneCombo.FIELD_CHOICE_CLASS,LegislativeActVersion.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<LegislativeActVersion>() {
			@Override
			protected Collection<LegislativeActVersion> __computeChoices__(AbstractInputChoice<LegislativeActVersion> input, Class<?> entityClass) {
				if(AbstractInput.getValue(legislativeActSelectOne) == null)
					return null;
				LegislativeAct legislativeAct = (LegislativeAct) legislativeActSelectOne.getValue();
				Collection<LegislativeActVersion> choices = DependencyInjection.inject(LegislativeActVersionController.class).getByActIdentifier(legislativeAct.getIdentifier());
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			
			@Override
			public void select(AbstractInputChoiceOne input, LegislativeActVersion legislativeActVersion) {
				super.select(input, legislativeActVersion);
				
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Version");
		return input;
	}
	
	private SelectOneCombo buildSectionSelectOne(Section section) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,section,SelectOneCombo.FIELD_CHOICE_CLASS,Section.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<Section>() {
			@Override
			protected Collection<Section> __computeChoices__(AbstractInputChoice<Section> input,Class<?> entityClass) {
				Collection<Section> choices = __inject__(SectionController.class).get();
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			
			@Override
			public void select(AbstractInputChoiceOne input, Section section) {
				super.select(input, section);
				
				if(budgetSpecializationUnitSelectOne != null) {
					budgetSpecializationUnitSelectOne.updateChoices();
					budgetSpecializationUnitSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
				if(actionSelectOne != null) {
					//actionSelectOne.updateChoices();
					//actionSelectOne.selectFirstChoice();
				}
				if(activitySelectOne != null) {
					//activitySelectOne.updateChoices();
					//activitySelectOne.selectFirstChoice();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Section");
		input.updateChoices();
		return input;
	}
	
	private SelectOneCombo buildExpenditureNatureSelectOne(ExpenditureNature expenditureNature) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,expenditureNature,SelectOneCombo.FIELD_CHOICE_CLASS,ExpenditureNature.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<ExpenditureNature>() {
			@Override
			protected Collection<ExpenditureNature> __computeChoices__(AbstractInputChoice<ExpenditureNature> input,Class<?> entityClass) {
				Collection<ExpenditureNature> choices = __inject__(ExpenditureNatureController.class).get();
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			@Override
			public void select(AbstractInputChoiceOne input, ExpenditureNature expenditureNature) {
				super.select(input, expenditureNature);
				if(activitySelectOne != null) {
					activitySelectOne.updateChoices();
					//activitySelectOne.selectFirstChoice();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"N.D.");
		input.updateChoices();
		input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildBudgetSpecializationUnitSelectOne(BudgetSpecializationUnit budgetSpecializationUnit) {		
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,budgetSpecializationUnit,SelectOneCombo.FIELD_CHOICE_CLASS,BudgetSpecializationUnit.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<BudgetSpecializationUnit>() {

			@Override
			protected Collection<BudgetSpecializationUnit> __computeChoices__(AbstractInputChoice<BudgetSpecializationUnit> input,Class<?> entityClass) {
				Collection<BudgetSpecializationUnit> choices = null;
				if(AbstractInput.getValue(sectionSelectOne) == null)
					return null;
				choices = __inject__(BudgetSpecializationUnitController.class).getByParentIdentifier(Parameters.SECTION_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(sectionSelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			
			@Override
			public void select(AbstractInputChoiceOne input, BudgetSpecializationUnit budgetSpecializationUnit) {
				super.select(input, budgetSpecializationUnit);
				if(actionSelectOne != null) {
					actionSelectOne.updateChoices();
					actionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
				if(activitySelectOne != null) {
					//activitySelectOne.updateChoices();
					//activitySelectOne.selectFirstChoice();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"U.S.B.");
		//input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildActionSelectOne(Action action) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,action,SelectOneCombo.FIELD_CHOICE_CLASS,Action.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<Action>() {
			
			@Override
			protected Collection<Action> __computeChoices__(AbstractInputChoice<Action> input,Class<?> entityClass) {
				Collection<Action> choices = null;
				if(AbstractInput.getValue(budgetSpecializationUnitSelectOne) == null)
					return null;
				choices = __inject__(ActionController.class).getByParentIdentifier(Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(budgetSpecializationUnitSelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			
			@Override
			public void select(AbstractInputChoiceOne input, Action action) {
				super.select(input, action);
				if(activitySelectOne != null) {
					activitySelectOne.updateChoices();
					activitySelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Action");
		return input;
	}
	
	private SelectOneCombo buildActivitySelectOne(Activity activity) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,activity,SelectOneCombo.FIELD_CHOICE_CLASS,Activity.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<Activity>() {
			@Override
			protected Collection<Activity> __computeChoices__(AbstractInputChoice<Activity> input,Class<?> entityClass) {
				Collection<Activity> choices = null;
				if(AbstractInput.getValue(actionSelectOne) == null)
					return null;
				choices = __inject__(ActivityController.class).getByParentIdentifier(Parameters.ACTION_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(actionSelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			@Override
			public void select(AbstractInputChoiceOne input, Activity activity) {
				super.select(input, activity);
				if(economicNatureSelectOne != null) {
					economicNatureSelectOne.updateChoices();
					economicNatureSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
				if(fundingSourceSelectOne != null) {
					fundingSourceSelectOne.updateChoices();
					fundingSourceSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
				if(lessorSelectOne != null) {
					lessorSelectOne.updateChoices();
					lessorSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Activité");
		//input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildEconomicNatureSelectOne(EconomicNature economicNature) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,economicNature,SelectOneCombo.FIELD_CHOICE_CLASS,EconomicNature.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<EconomicNature>() {
			@Override
			protected Collection<EconomicNature> __computeChoices__(AbstractInputChoice<EconomicNature> input,Class<?> entityClass) {
				Collection<EconomicNature> choices = null;
				if(AbstractInput.getValue(activitySelectOne) == null)
					return null;
				if(activityInitial != null && activityInitial.getIdentifier().equals(FieldHelper.readSystemIdentifier(activitySelectOne.getValue())))
					choices = activityInitial.getEconomicNatures();
				else
					choices = __inject__(EconomicNatureController.class).getByParentIdentifier(Parameters.ACTIVITY_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(activitySelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"N.E.");
		return input;
	}
	
	private SelectOneCombo buildFundingSourceSelectOne(FundingSource fundingSource) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,fundingSource,SelectOneCombo.FIELD_CHOICE_CLASS,FundingSource.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<FundingSource>() {
			@Override
			protected Collection<FundingSource> __computeChoices__(AbstractInputChoice<FundingSource> input,Class<?> entityClass) {
				Collection<FundingSource> choices = null;
				if(AbstractInput.getValue(activitySelectOne) == null)
					return null;
				if(activityInitial != null && activityInitial.getIdentifier().equals(FieldHelper.readSystemIdentifier(activitySelectOne.getValue())))
					choices = activityInitial.getFundingSources();
				else
					choices = __inject__(FundingSourceController.class).getByParentIdentifier(Parameters.ACTIVITY_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(activitySelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"S.F.");
		return input;
	}
	
	private SelectOneCombo buildLessorSelectOne(Lessor lessor) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,lessor,SelectOneCombo.FIELD_CHOICE_CLASS,Lessor.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<Lessor>() {
			@Override
			protected Collection<Lessor> __computeChoices__(AbstractInputChoice<Lessor> input,Class<?> entityClass) {
				Collection<Lessor> choices = null;
				if(AbstractInput.getValue(activitySelectOne) == null)
					return null;
				if(activityInitial != null && activityInitial.getIdentifier().equals(FieldHelper.readSystemIdentifier(activitySelectOne.getValue())))
					choices = activityInitial.getLessors();
				else
					choices = __inject__(LessorController.class).getByParentIdentifier(Parameters.ACTIVITY_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(activitySelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Bailleur");
		return input;
	}
	
	private SelectOneCombo buildAdjustmentsNotEqualZeroOrIncludedMovementNotEqualZero(Boolean adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial) {
		return SelectOneCombo.buildUnknownYesNoOnly((Boolean) adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial, "Ajustement ou mouvement inclus existe");
	}
	
	private SelectOneCombo buildAvailableMinusIncludedMovementPlusAdjustmentLessThanZero(Boolean availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial) {
		return SelectOneCombo.buildUnknownYesNoOnly((Boolean) availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial, "Disponible insuffisant");
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(legislativeActSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne.getOutputLabel().setTitle("Acte budgétaire"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne,Cell.FIELD_WIDTH,7));	
		}
		
		if(legislativeActVersionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne.getOutputLabel().setTitle("Version de l'acte budgétaire"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne,Cell.FIELD_WIDTH,3));
		}
		
		if(sectionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,sectionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,sectionSelectOne,Cell.FIELD_WIDTH,11));	
		}
		
		if(expenditureNatureSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,expenditureNatureSelectOne.getOutputLabel().setTitle("Nature de dépense"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,expenditureNatureSelectOne,Cell.FIELD_WIDTH,11));	
		}
		
		if(budgetSpecializationUnitSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetSpecializationUnitSelectOne.getOutputLabel().setTitle("Unité de spécialisation du budget"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetSpecializationUnitSelectOne,Cell.FIELD_WIDTH,3));
		}

		if(actionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,actionSelectOne.getOutputLabel().setTitle("Action"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,actionSelectOne,Cell.FIELD_WIDTH,7));
		}
		
		if(activitySelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectOne,Cell.FIELD_WIDTH,10));	
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectionController.getShowDialogCommandButton(),Cell.FIELD_WIDTH,1));
		}
		
		if(economicNatureSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,economicNatureSelectOne.getOutputLabel().setTitle("Nature économique"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,economicNatureSelectOne,Cell.FIELD_WIDTH,11));
		}
		
		if(fundingSourceSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,fundingSourceSelectOne.getOutputLabel().setTitle("Source de financement"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,fundingSourceSelectOne,Cell.FIELD_WIDTH,2));
		}
		
		if(lessorSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,lessorSelectOne.getOutputLabel().setTitle("Bailleur"),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,lessorSelectOne,Cell.FIELD_WIDTH,8));
		}
		
		if(adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne,Cell.FIELD_WIDTH,5));
		}
		
		if(availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne,Cell.FIELD_WIDTH,5));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,12));	
		return cellsMaps;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		
		Collection<String> __strings__ = new ArrayList<>();
		
		if(legislativeActInitial != null)
			__strings__.add(legislativeActInitial.getName());
		if(legislativeActVersionInitial != null)
			__strings__.add(legislativeActVersionInitial.getName());
		if(!__strings__.isEmpty())
			strings.add(StringHelper.concatenate(__strings__, " - "));
		
		__strings__ = new ArrayList<>();
		if(sectionInitial != null)
			__strings__.add("Section "+sectionInitial.getCode());
		
		if(activityInitial == null) {
			if(expenditureNatureInitial != null)
				__strings__.add("Nature de dépense "+expenditureNatureInitial.toString());
		}
		
		if(budgetSpecializationUnitInitial != null) {
			if(actionInitial == null && activityInitial == null)
				__strings__.add(budgetSpecializationUnitInitial.toString());
			else
				__strings__.add((budgetSpecializationUnitInitial.getCode().startsWith("1") ? "Dotation":"Programme")+" "+budgetSpecializationUnitInitial.getCode());
		}
		if(actionInitial != null) {
			if(activityInitial == null)
				__strings__.add(actionInitial.toString());
			else
				__strings__.add("Action "+actionInitial.getCode());
		}
		if(!__strings__.isEmpty())
			strings.add(StringHelper.concatenate(__strings__, " - "));
		
		if(activityInitial == null) {
			
		}else {
			strings.add(activityInitial.toString());
		}
		
		if(economicNatureInitial == null) {
			
		}else {
			strings.add(economicNatureInitial.toString());
		}
		
		if(fundingSourceInitial == null) {
			
		}else {
			strings.add(fundingSourceInitial.getName());
		}
		
		if(lessorInitial == null) {
			
		}else {
			strings.add("Bailleur : "+lessorInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isLegislativeActColumnShowable,Boolean.TRUE)) && legislativeActInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_BUDGETARY_ACT_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isLegislativeActVersionColumnShowable,Boolean.TRUE)) && legislativeActVersionInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_BUDGETARY_ACT_VERSION_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isSectionColumnShowable,Boolean.TRUE)) && sectionInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_SECTION_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isExpenditureNatureColumnShowable,Boolean.TRUE)) && expenditureNatureInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_NATURE_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isBudgetSpecializationUnitColumnShowable,Boolean.TRUE)) && budgetSpecializationUnitInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_BUDGET_SPECIALIZATION_UNIT_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isActionColumnShowable,Boolean.TRUE)) && actionInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_ACTION_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isActivityColumnShowable,Boolean.TRUE)) && activityInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_ACTIVITY_AS_STRING);
		if(economicNatureInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_ECONOMIC_NATURE_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isFundingSourceColumnShowable,Boolean.TRUE)) && fundingSourceInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_FUNDING_SOURCE_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isLessorColumnShowable,Boolean.TRUE)) && lessorInitial == null)
			columnsFieldsNames.add(Expenditure.FIELD_LESSOR_AS_STRING);
		
		Helper.addAmountsColumnsNames(columnsFieldsNames,isEntryAuthorizationAdjustmentEditable,isInvestment(), ExpenditureAmounts.FIELD_INITIAL,ExpenditureAmounts.FIELD_MOVEMENT,ExpenditureAmounts.FIELD_ACTUAL
				,ExpenditureAmounts.FIELD_MOVEMENT_INCLUDED,ExpenditureAmounts.FIELD_ADJUSTMENT,ExpenditureAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT
				);
		columnsFieldsNames.add(Expenditure.FIELD___AUDIT__);
		return columnsFieldsNames;
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(LegislativeAct.class.equals(klass))
			return AbstractInput.getValue(legislativeActVersionSelectOne) == null;
		if(LegislativeActVersion.class.equals(klass))
			return Boolean.TRUE;
		if(ExpenditureNature.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(Section.class.equals(klass))
			return AbstractInput.getValue(budgetSpecializationUnitSelectOne) == null && AbstractInput.getValue(actionSelectOne) == null 
				&& AbstractInput.getValue(activitySelectOne) == null;
		if(BudgetSpecializationUnit.class.equals(klass))
			return AbstractInput.getValue(actionSelectOne) == null && AbstractInput.getValue(activitySelectOne) == null;
		if(Action.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(Activity.class.equals(klass))
			return Boolean.TRUE;
		/*
		if(EconomicNature.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(FundingSource.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(Lessor.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		*/
		if(input == adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne)
			return input.getValue() != null;
		if(input == availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne)
			return input.getValue() != null;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	public LegislativeAct getLegislativeAct() {
		return (LegislativeAct) AbstractInput.getValue(legislativeActSelectOne);
	}
	
	public LegislativeActVersion getLegislativeActVersion() {
		return (LegislativeActVersion) AbstractInput.getValue(legislativeActVersionSelectOne);
	}
	
	public Section getSection() {
		return (Section) AbstractInput.getValue(sectionSelectOne);
	}
	
	public ExpenditureNature getExpenditureNature() {
		return (ExpenditureNature) AbstractInput.getValue(expenditureNatureSelectOne);
	}
	
	public BudgetSpecializationUnit getBudgetSpecializationUnit() {
		return (BudgetSpecializationUnit) AbstractInput.getValue(budgetSpecializationUnitSelectOne);
	}
	
	public Action getAction() {
		return (Action) AbstractInput.getValue(actionSelectOne);
	}
	
	public Activity getActivity() {
		return (Activity) AbstractInput.getValue(activitySelectOne);
	}
	
	public EconomicNature getEconomicNature() {
		return (EconomicNature) AbstractInput.getValue(economicNatureSelectOne);
	}
	
	public FundingSource getFundingSource() {
		return (FundingSource) AbstractInput.getValue(fundingSourceSelectOne);
	}
	
	public Lessor getLessor() {
		return (Lessor) AbstractInput.getValue(lessorSelectOne);
	}
	
	public Boolean getAdjustmentsNotEqualZeroOrIncludedMovementNotEqualZero() {
		return (Boolean) AbstractInput.getValue(adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne);
	}
	
	public Boolean getAvailableMinusIncludedMovementPlusAdjustmentLessThanZero() {
		return (Boolean) AbstractInput.getValue(availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne);
	}
	
	public Boolean isInvestment() {
		if(expenditureNatureInitial == null)
			return null;
		return expenditureNatureInitial.isInvestment();
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ExpenditureFilterController controller,Boolean initial) {
		LegislativeActVersion legislativeActVersion = Boolean.TRUE.equals(initial) ? controller.legislativeActVersionInitial : controller.getLegislativeActVersion();
		if(legislativeActVersion == null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.legislativeActInitial : controller.getLegislativeAct()), filter);
		else
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, FieldHelper.readSystemIdentifier(legislativeActVersion), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.SECTION_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.sectionInitial : controller.getSection()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.EXPENDITURE_NATURE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.expenditureNatureInitial : controller.getExpenditureNature()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.budgetSpecializationUnitInitial : controller.getBudgetSpecializationUnit()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ACTION_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.actionInitial : controller.getAction()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ACTIVITY_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.activityInitial : controller.getActivity()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ECONOMIC_NATURE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.economicNatureInitial : controller.getEconomicNature()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.FUNDING_SOURCE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.fundingSourceInitial : controller.getFundingSource()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LESSOR_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.lessorInitial : controller.getLessor()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO, Boolean.TRUE.equals(initial) ? controller.adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroInitial 
				: controller.getAdjustmentsNotEqualZeroOrIncludedMovementNotEqualZero(), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO, Boolean.TRUE.equals(initial) ? controller.availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial 
				: controller.getAvailableMinusIncludedMovementPlusAdjustmentLessThanZero(), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(ExpenditureFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	/**/
	
	public static final String FIELD_LEGISLATIVE_ACT_SELECT_ONE = "legislativeActSelectOne";
	public static final String FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE = "legislativeActVersionSelectOne";
	public static final String FIELD_SECTION_SELECT_ONE = "sectionSelectOne";
	public static final String FIELD_EXPENDITURE_NATURE_SELECT_ONE = "expenditureNatureSelectOne";
	public static final String FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE = "budgetSpecializationUnitSelectOne";
	public static final String FIELD_ACTION_SELECT_ONE = "actionSelectOne";
	public static final String FIELD_ACTIVITY_SELECT_ONE = "activitySelectOne";
	public static final String FIELD_ECONOMIC_NATURE_SELECT_ONE = "economicNatureSelectOne";
	public static final String FIELD_FUNDING_SOURCE_SELECT_ONE = "fundingSourceSelectOne";
	public static final String FIELD_LESSOR_SELECT_ONE = "lessorSelectOne";
	public static final String FIELD_ADJUSTMENTS_NOT_EQUAL_ZERO_OR_INCLUDED_MOVEMENT_NOT_EQUAL_ZERO_SELECT_ONE = "adjustmentsNotEqualZeroOrIncludedMovementNotEqualZeroSelectOne";
	public static final String FIELD_AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO_SELECT_ONE = "availableMinusIncludedMovementPlusAdjustmentLessThanZeroSelectOne";
	
	/*
	private static final String[] ACTIVITY_ECONOMIC_NATURES_FUNDING_SOURCES_LESSORS = new String[] {
			ci.gouv.dgbf.system.collectif.server.persistence.entities.Activity.FIELD_ECONOMIC_NATURES
			,ci.gouv.dgbf.system.collectif.server.persistence.entities.Activity.FIELD_FUNDING_SOURCES
			,ci.gouv.dgbf.system.collectif.server.persistence.entities.Activity.FIELD_LESSORS
		};
	*/
}