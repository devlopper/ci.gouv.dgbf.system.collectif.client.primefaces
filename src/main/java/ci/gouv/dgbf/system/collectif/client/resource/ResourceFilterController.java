package ci.gouv.dgbf.system.collectif.client.resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
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

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.BudgetSpecializationUnitDto;
import ci.gouv.dgbf.system.collectif.server.api.service.ResourceActivityDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetSpecializationUnit;
import ci.gouv.dgbf.system.collectif.server.client.rest.BudgetSpecializationUnitController;
import ci.gouv.dgbf.system.collectif.server.client.rest.EconomicNature;
import ci.gouv.dgbf.system.collectif.server.client.rest.EconomicNatureController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Resource;
import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceActivity;
import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceActivityController;
import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceAmounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.Section;
import ci.gouv.dgbf.system.collectif.server.client.rest.SectionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ResourceFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo legislativeActSelectOne,legislativeActVersionSelectOne,sectionSelectOne,budgetSpecializationUnitSelectOne
		,activitySelectOne,economicNatureSelectOne;
	//private ActivitySelectionController activitySelectionController;
	
	private Boolean isLegislativeActColumnShowable,isLegislativeActVersionColumnShowable,isSectionColumnShowable
	,isBudgetSpecializationUnitColumnShowable,isActivityColumnShowable;
	
	private LegislativeAct legislativeActInitial;
	private LegislativeActVersion legislativeActVersionInitial;
	private Section sectionInitial;
	private BudgetSpecializationUnit budgetSpecializationUnitInitial;
	private ResourceActivity activityInitial;
	private EconomicNature economicNatureInitial;
	
	private Boolean isRevenueAdjustmentEditable;
	
	private Resource resourcesAmountsSum;
	
	public ResourceFilterController(Boolean computeLegislativeActVersionSumsAndTotal) {		
		if(legislativeActVersionInitial == null)
			legislativeActVersionInitial = Helper.getLegislativeActVersionFromRequestParameter(computeLegislativeActVersionSumsAndTotal);
		if(legislativeActInitial == null)
			legislativeActInitial = Helper.getLegislativeActFromRequestParameter(legislativeActVersionInitial);
		
		if(activityInitial == null) {
			activityInitial = __inject__(ResourceActivityController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.RESOURCE_ACTIVITY_IDENTIFIER)
					,new Controller.GetArguments().projections(ResourceActivityDto.JSON_IDENTIFIER,ResourceActivityDto.JSON_CODE,ResourceActivityDto.JSON_NAME,ResourceActivityDto.JSONS_SECTION_BUDGET_SPECIALIZATION_UNIT
							,ResourceActivityDto.JSON_ECONOMIC_NATURES));
			if(activityInitial != null) {
				sectionInitial = activityInitial.getSection();
				budgetSpecializationUnitInitial = activityInitial.getBudgetSpecializationUnit();
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

		if(economicNatureInitial == null)
			economicNatureInitial = __inject__(EconomicNatureController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.ECONOMIC_NATURE_IDENTIFIER));
		
		//readResourceAountsSum();
	}
	
	public ResourceFilterController() {
		this(null);
	}
	
	public Resource sumResourcesAmounts() {
		/*if(resourcesAmountsSum == null)
			resourcesAmountsSum = EntityReader.getInstance().readOne(Resource.class, new Arguments<Resource>()
				.queryIdentifier(ResourceQuerier.QUERY_IDENTIFIER_READ_DYNAMIC_ONE)
				.flags(ResourceQuerier.FLAG_SUM_ALL_AMOUNTS).filter(instantiateFilter(this,Boolean.TRUE)));
		*/
		return resourcesAmountsSum;
	}
	
	@Override
	public ResourceFilterController build() {
		//if(activitySelectionController == null)
		//	activitySelectionController = new ActivitySelectionController();
		return (ResourceFilterController) super.build();
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName))
			return legislativeActInitial;
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return legislativeActVersionInitial;
		if(FIELD_SECTION_SELECT_ONE.equals(fieldName))
			return sectionInitial;
		if(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE.equals(fieldName))
			return budgetSpecializationUnitInitial;
		if(FIELD_ACTIVITY_SELECT_ONE.equals(fieldName))
			return activityInitial;
		if(FIELD_ECONOMIC_NATURE_SELECT_ONE.equals(fieldName))
			return economicNatureInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_SELECT_ONE, LegislativeAct.class);
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE, LegislativeActVersion.class);
		buildInputSelectOne(FIELD_SECTION_SELECT_ONE, Section.class);
		buildInputSelectOne(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE, BudgetSpecializationUnit.class);
		buildInputSelectOne(FIELD_ACTIVITY_SELECT_ONE, ResourceActivity.class);
		buildInputSelectOne(FIELD_ECONOMIC_NATURE_SELECT_ONE, EconomicNature.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,legislativeActVersionSelectOne));
		if(legislativeActVersionSelectOne != null)
			legislativeActVersionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
		if(sectionSelectOne != null)
			sectionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,budgetSpecializationUnitSelectOne,activitySelectOne));
		if(budgetSpecializationUnitSelectOne != null)
			budgetSpecializationUnitSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,activitySelectOne));		
		if(activitySelectOne != null)
			activitySelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,economicNatureSelectOne));
		if(economicNatureSelectOne != null)
			economicNatureSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		if(sectionSelectOne != null)
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
		if(FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE.equals(fieldName))
			return buildBudgetSpecializationUnitSelectOne((BudgetSpecializationUnit) value);
		if(FIELD_ACTIVITY_SELECT_ONE.equals(fieldName))
			return buildActivitySelectOne((ResourceActivity) value);
		if(FIELD_ECONOMIC_NATURE_SELECT_ONE.equals(fieldName))
			return buildEconomicNatureSelectOne((EconomicNature) value);
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
		if(input == budgetSpecializationUnitSelectOne)
			return Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER;
		if(input == activitySelectOne)
			return Parameters.RESOURCE_ACTIVITY_IDENTIFIER;
		if(input == economicNatureSelectOne)
			return Parameters.ECONOMIC_NATURE_IDENTIFIER;
		return super.buildParameterName(fieldName, input);
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
				if(legislativeActVersionSelectOne != null)
					legislativeActVersionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier(); 
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
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
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
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
				
				if(activitySelectOne != null) {
					//activitySelectOne.updateChoices();
					//activitySelectOne.selectFirstChoice();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.Section.NAME);
		input.updateChoices();
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
				if(activitySelectOne != null) {
					activitySelectOne.updateChoices();
					activitySelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.BudgetSpecializationUnit.INITALS);
		//input.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		return input;
	}
	
	private SelectOneCombo buildActivitySelectOne(ResourceActivity activity) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,activity,SelectOneCombo.FIELD_CHOICE_CLASS,ResourceActivity.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<ResourceActivity>() {
			@Override
			protected Collection<ResourceActivity> __computeChoices__(AbstractInputChoice<ResourceActivity> input,Class<?> entityClass) {
				Collection<ResourceActivity> choices = null;
				if(AbstractInput.getValue(budgetSpecializationUnitSelectOne) == null)
					return null;
				choices = __inject__(ResourceActivityController.class).getByParentIdentifier(Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(budgetSpecializationUnitSelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
			@Override
			public void select(AbstractInputChoiceOne input, ResourceActivity activity) {
				super.select(input, activity);
				if(economicNatureSelectOne != null) {
					economicNatureSelectOne.updateChoices();
					economicNatureSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
				}
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.ResourceActivity.NAME);
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
					choices = __inject__(EconomicNatureController.class).getByParentIdentifier(Parameters.RESOURCE_ACTIVITY_IDENTIFIER
						, (String)FieldHelper.readSystemIdentifier(activitySelectOne.getValue()));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.EconomicNature.INITIALS);
		return input;
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(legislativeActSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne,Cell.FIELD_WIDTH,7));	
		}
		
		if(legislativeActVersionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne,Cell.FIELD_WIDTH,3));
		}
		
		if(sectionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,sectionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,sectionSelectOne,Cell.FIELD_WIDTH,11));	
		}
		
		if(budgetSpecializationUnitSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetSpecializationUnitSelectOne.getOutputLabel().setTitle(ci.gouv.dgbf.system.collectif.server.api.persistence.BudgetSpecializationUnit.NAME),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,budgetSpecializationUnitSelectOne,Cell.FIELD_WIDTH,11));
		}
		
		if(activitySelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectOne,Cell.FIELD_WIDTH,11));	
			//cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activitySelectionController.getShowDialogCommandButton(),Cell.FIELD_WIDTH,1));
		}
		
		if(economicNatureSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,economicNatureSelectOne.getOutputLabel().setTitle(ci.gouv.dgbf.system.collectif.server.api.persistence.EconomicNature.NAME),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,economicNatureSelectOne,Cell.FIELD_WIDTH,11));
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
		
		
		if(budgetSpecializationUnitInitial != null) {
			if(activityInitial == null)
				__strings__.add(budgetSpecializationUnitInitial.toString());
			else
				__strings__.add((budgetSpecializationUnitInitial.getCode().startsWith("1") ? "Dotation":"Programme")+" "+budgetSpecializationUnitInitial.getCode());
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
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isLegislativeActColumnShowable,Boolean.TRUE)) && legislativeActInitial == null)
			columnsFieldsNames.add(Resource.FIELD_BUDGETARY_ACT_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isLegislativeActVersionColumnShowable,Boolean.TRUE)) && legislativeActVersionInitial == null)
			columnsFieldsNames.add(Resource.FIELD_BUDGETARY_ACT_VERSION_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isSectionColumnShowable,Boolean.TRUE)) && sectionInitial == null)
			columnsFieldsNames.add(Resource.FIELD_SECTION_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isBudgetSpecializationUnitColumnShowable,Boolean.TRUE)) && budgetSpecializationUnitInitial == null)
			columnsFieldsNames.add(Resource.FIELD_BUDGET_SPECIALIZATION_UNIT_AS_STRING);
		if(Boolean.TRUE.equals(ValueHelper.defaultToIfBlank(isActivityColumnShowable,Boolean.TRUE)) && activityInitial == null)
			columnsFieldsNames.add(Resource.FIELD_ACTIVITY_AS_STRING);
		if(economicNatureInitial == null)
			columnsFieldsNames.add(Resource.FIELD_ECONOMIC_NATURE_AS_STRING);

		addAmountsColumnsNames(columnsFieldsNames, ResourceAmounts.FIELD_INITIAL,ResourceAmounts.FIELD_MOVEMENT,ResourceAmounts.FIELD_ACTUAL
				,ResourceAmounts.FIELD_MOVEMENT_INCLUDED//,ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED
				//,ResourceAmounts.FIELD_AVAILABLE
				,ResourceAmounts.FIELD_ADJUSTMENT,ResourceAmounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT
				);
		columnsFieldsNames.add(Resource.FIELD___AUDIT__);
		return columnsFieldsNames;
	}
	
	private void addAmountsColumnsNames(Collection<String> collection,Collection<String> names) {
		if(collection == null || CollectionHelper.isEmpty(names))
			return;
		for(String name : names) {
			if(ResourceAmounts.FIELD_INITIAL.equals(name)) {
				collection.add(FieldHelper.join(Resource.FIELD_REVENUE,name));
			}else if(ResourceAmounts.FIELD_ADJUSTMENT.equals(name)) {
				if(Boolean.TRUE.equals(isRevenueAdjustmentEditable)) {
					collection.add(Resource.FIELD_REVENUE_ADJUSTMENT);
				}else {
					collection.add(FieldHelper.join(Resource.FIELD_REVENUE,name));
				}
			}else {
				collection.add(FieldHelper.join(Resource.FIELD_REVENUE,name));
			}
		}
	}
	
	private void addAmountsColumnsNames(Collection<String> collection,String...names) {
		if(collection == null || ArrayHelper.isEmpty(names))
			return;
		addAmountsColumnsNames(collection, CollectionHelper.listOf(names));
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(LegislativeAct.class.equals(klass))
			return AbstractInput.getValue(legislativeActVersionSelectOne) == null;
		if(LegislativeActVersion.class.equals(klass))
			return Boolean.TRUE;
		if(Section.class.equals(klass))
			return AbstractInput.getValue(budgetSpecializationUnitSelectOne) == null && AbstractInput.getValue(activitySelectOne) == null;
		if(BudgetSpecializationUnit.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(ResourceActivity.class.equals(klass))
			return Boolean.TRUE;
		/*
		if(EconomicNature.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(FundingSource.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		if(Lessor.class.equals(klass))
			return AbstractInput.getValue(activitySelectOne) == null;
		*/
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
	
	public BudgetSpecializationUnit getBudgetSpecializationUnit() {
		return (BudgetSpecializationUnit) AbstractInput.getValue(budgetSpecializationUnitSelectOne);
	}
	
	public ResourceActivity getActivity() {
		return (ResourceActivity) AbstractInput.getValue(activitySelectOne);
	}
	
	public EconomicNature getEconomicNature() {
		return (EconomicNature) AbstractInput.getValue(economicNatureSelectOne);
	}
	
	@Override
	public Map<String, List<String>> asMap() {
		Map<String, List<String>> map = new HashMap<>();
		addParameter(map, Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, legislativeActVersionInitial);
		if(!map.containsKey(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER))
			addParameter(map, Parameters.LEGISLATIVE_ACT_IDENTIFIER, legislativeActInitial);
		addParameter(map, Parameters.RESOURCE_ACTIVITY_IDENTIFIER, activityInitial);
		//addParameter(map, Parameters.AVAILABLE_MINUS_INCLUDED_MOVEMENT_PLUS_ADJUSTMENT_LESS_THAN_ZERO, availableMinusIncludedMovementPlusAdjustmentLessThanZeroInitial);
		return map;
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ResourceFilterController controller,Boolean initial) {
		LegislativeActVersion legislativeActVersion = Boolean.TRUE.equals(initial) ? controller.legislativeActVersionInitial : controller.getLegislativeActVersion();
		if(legislativeActVersion == null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.legislativeActInitial : controller.getLegislativeAct()), filter);
		else
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, FieldHelper.readSystemIdentifier(legislativeActVersion), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.SECTION_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.sectionInitial : controller.getSection()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.BUDGET_SPECIALIZATION_UNIT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.budgetSpecializationUnitInitial : controller.getBudgetSpecializationUnit()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.RESOURCE_ACTIVITY_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.activityInitial : controller.getActivity()), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ECONOMIC_NATURE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.economicNatureInitial : controller.getEconomicNature()), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(ResourceFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	/**/
	
	public static final String FIELD_LEGISLATIVE_ACT_SELECT_ONE = "legislativeActSelectOne";
	public static final String FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE = "legislativeActVersionSelectOne";
	public static final String FIELD_SECTION_SELECT_ONE = "sectionSelectOne";
	public static final String FIELD_BUDGET_SPECIALIZATION_UNIT_SELECT_ONE = "budgetSpecializationUnitSelectOne";
	public static final String FIELD_ACTIVITY_SELECT_ONE = "activitySelectOne";
	public static final String FIELD_ECONOMIC_NATURE_SELECT_ONE = "economicNatureSelectOne";
	
}