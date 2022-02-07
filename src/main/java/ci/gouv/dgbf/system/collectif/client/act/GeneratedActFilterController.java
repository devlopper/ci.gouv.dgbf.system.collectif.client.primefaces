package ci.gouv.dgbf.system.collectif.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.GeneratedAct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class GeneratedActFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo legislativeActSelectOne,legislativeActVersionSelectOne;
	
	private LegislativeAct legislativeActInitial;
	private LegislativeActVersion legislativeActVersionInitial;
	
	public GeneratedActFilterController() {
		if(legislativeActVersionInitial == null)
			legislativeActVersionInitial = Helper.getLegislativeActVersionFromRequestParameter(null);
		if(legislativeActInitial == null)
			legislativeActInitial = Helper.getLegislativeActFromRequestParameter(legislativeActVersionInitial);
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_BUDGETARY_ACT_SELECT_ONE.equals(fieldName))
			return legislativeActInitial;
		if(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return legislativeActVersionInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_BUDGETARY_ACT_SELECT_ONE, LegislativeActVersion.class);
		buildInputSelectOne(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE, LegislativeActVersion.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		legislativeActSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,legislativeActVersionSelectOne));
		legislativeActVersionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		legislativeActSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_BUDGETARY_ACT_SELECT_ONE.equals(fieldName))
			return buildLegislativeActSelectOne((LegislativeAct) value);
		if(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return buildLegislativeActVersionSelectOne((LegislativeActVersion) value);
		return null;
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
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(LegislativeAct.class.equals(klass))
			return AbstractInput.getValue(legislativeActVersionSelectOne) == null;
		if(LegislativeActVersion.class.equals(klass))
			return Boolean.TRUE;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(legislativeActSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne,Cell.FIELD_WIDTH,4));
		}
		
		if(legislativeActVersionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne,Cell.FIELD_WIDTH,5));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,1));	
		return cellsMaps;
	}
	/*
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(legislativeActInitial != null) {
			strings.add(legislativeActInitial.getName());
		}		
		if(legislativeActVersionInitial != null) {
			strings.add(legislativeActVersionInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	*/
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(legislativeActInitial != null) {
			strings.add(legislativeActInitial.getName());
		}		
		if(legislativeActVersionInitial != null) {
			strings.add(legislativeActVersionInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.addAll(List.of(GeneratedAct.FIELD_CODE,GeneratedAct.FIELD_NAME));
		columnsFieldsNames.addAll(List.of(GeneratedAct.FIELD_AUDIT));
		return columnsFieldsNames;
	}
	
	public LegislativeAct getLegislativeAct() {
		return (LegislativeAct) AbstractInput.getValue(legislativeActSelectOne);
	}
	
	public LegislativeActVersion getLegislativeActVersion() {
		return (LegislativeActVersion) AbstractInput.getValue(legislativeActVersionSelectOne);
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_BUDGETARY_ACT_SELECT_ONE.equals(fieldName) || input == legislativeActSelectOne)
			return Parameters.LEGISLATIVE_ACT_IDENTIFIER;
		if(FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE.equals(fieldName) || input == legislativeActVersionSelectOne)
			return Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	public Map<String, List<String>> asMap() {
		Map<String, List<String>> map = new LinkedHashMap<>();
		if(legislativeActVersionInitial != null)
			map.put(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, List.of(legislativeActVersionInitial.getIdentifier()));
		return map;
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,GeneratedActFilterController controller,Boolean initial) {
		LegislativeActVersion legislativeActVersion = Boolean.TRUE.equals(initial) ? controller.legislativeActVersionInitial : controller.getLegislativeActVersion();
		if(legislativeActVersion == null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.legislativeActInitial : controller.getLegislativeAct()), filter);
		else
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, FieldHelper.readSystemIdentifier(legislativeActVersion), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(GeneratedActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_BUDGETARY_ACT_SELECT_ONE = "legislativeActSelectOne";
	public static final String FIELD_BUDGETARY_ACT_VERSION_SELECT_ONE = "legislativeActVersionSelectOne";
}