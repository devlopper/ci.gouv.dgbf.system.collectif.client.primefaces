package ci.gouv.dgbf.system.collectif.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.time.TimeHelper;
import org.cyk.utility.__kernel__.value.ValueConverter;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.Calendar;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.collectif.client.AbstractFilterControllerBasedLegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.RegulatoryAct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class RegulatoryActFilterController extends AbstractFilterControllerBasedLegislativeActVersion implements Serializable {

	private SelectOneCombo includedSelectOne;
	private Calendar dateLowerThanOrEqualCalendar,dateGreaterThanOrEqualCalendar;
	
	private Boolean includedInitial;
	private Date dateLowerThanOrEqualInitial,dateGreaterThanOrEqualInitial;
	
	public RegulatoryActFilterController() {
		includedInitial = ValueConverter.getInstance().convertToBoolean(WebController.getInstance().getRequestParameter(Parameters.REGULATORY_ACT_INCLUDED));
		dateLowerThanOrEqualInitial = ValueConverter.getInstance().convertToDate(NumberHelper.getLong(WebController.getInstance().getRequestParameter(Parameters.REGULATORY_ACT_DATE_LOWER_THAN_OR_EQUAL)));
		dateGreaterThanOrEqualInitial = ValueConverter.getInstance().convertToDate(NumberHelper.getLong(WebController.getInstance().getRequestParameter(Parameters.REGULATORY_ACT_DATE_GREATER_THAN_OR_EQUAL)));
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName))
			return legislativeActInitial;
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return legislativeActVersionInitial;
		if(FIELD_INCLUDED_SELECT_ONE.equals(fieldName))
			return includedInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected Date getInputCalendarInitialValue(String fieldName) {
		if(FIELD_DATE_LOWER_THAN_OR_EQUAL_CALENDAR.equals(fieldName))
			return dateLowerThanOrEqualInitial;
		if(FIELD_DATE_GREATER_THAN_OR_EQUAL_CALENDAR.equals(fieldName))
			return dateGreaterThanOrEqualInitial;
		return super.getInputCalendarInitialValue(fieldName);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_SELECT_ONE, LegislativeActVersion.class);
		buildInputSelectOne(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE, LegislativeActVersion.class);
		buildInputSelectOne(FIELD_INCLUDED_SELECT_ONE, Boolean.class);
		buildInputCalendar(FIELD_DATE_LOWER_THAN_OR_EQUAL_CALENDAR);
		buildInputCalendar(FIELD_DATE_GREATER_THAN_OR_EQUAL_CALENDAR);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE,legislativeActVersionSelectOne,dateGreaterThanOrEqualCalendar,dateLowerThanOrEqualCalendar));
		if(legislativeActVersionSelectOne != null)
			legislativeActVersionSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		if(legislativeActSelectOne != null)
			legislativeActSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		else if(legislativeActVersionSelectOne != null) {
			legislativeActVersionSelectOne.updateChoices();
			legislativeActVersionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier(); 
		}
		
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName))
			return buildLegislativeActSelectOne((LegislativeAct) value);
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName))
			return buildLegislativeActVersionSelectOne((LegislativeActVersion) value);
		if(FIELD_INCLUDED_SELECT_ONE.equals(fieldName))
			return buildIncludedSelectOne((Boolean) value);
		if(FIELD_DATE_LOWER_THAN_OR_EQUAL_CALENDAR.equals(fieldName))
			return buildDateLowerThanOrEqualCalendar((Date) value);
		if(FIELD_DATE_GREATER_THAN_OR_EQUAL_CALENDAR.equals(fieldName))
			return buildDateGreaterThanOrEqualCalendar((Date) value);
		return null;
	}
	
	private SelectOneCombo buildLegislativeActSelectOne(LegislativeAct legislativeAct) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,legislativeAct,SelectOneCombo.FIELD_CHOICE_CLASS,LegislativeAct.class
				,SelectOneCombo.FIELD_CHOICES,LegislativeAct.buildChoices(),SelectOneCombo.FIELD_CHOICES_INITIALIZED,Boolean.TRUE,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<LegislativeAct>() {
			
			@Override
			public void select(AbstractInputChoiceOne input, LegislativeAct legislativeAct) {
				super.select(input, legislativeAct);
				if(legislativeActVersionSelectOne != null) {
					legislativeActVersionSelectOne.updateChoices();
					legislativeActVersionSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier(); 
				}
				/*if(dateLowerThanOrEqualCalendar != null && legislativeAct != null && legislativeAct.getDateAsTimestamp() != null) {
					dateLowerThanOrEqualCalendar.setValue(new Date(legislativeAct.getDateAsTimestamp()));
				}*/
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
				LegislativeAct legislativeAct = legislativeActSelectOne == null ? legislativeActInitial : (LegislativeAct) AbstractInput.getValue(legislativeActSelectOne);
				if(legislativeAct == null)
					return null;
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
	
	private SelectOneCombo buildIncludedSelectOne(Boolean included) {
		return SelectOneCombo.buildUnknownYesNoOnly((Boolean) included, "Inclus");
	}
	
	private Calendar buildDateLowerThanOrEqualCalendar(Date date) {
		return Calendar.build(Calendar.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Au",Calendar.FIELD_VALUE,date);
	}
	
	private Calendar buildDateGreaterThanOrEqualCalendar(Date date) {
		return Calendar.build(Calendar.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Du",Calendar.FIELD_VALUE,date);
	}
	
	@Override
	protected Boolean isInputValueNotNull(AbstractInput<?> input) {
		if(input == includedSelectOne)
			return Boolean.TRUE;
		return super.isInputValueNotNull(input);
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(LegislativeAct.class.equals(klass))
			return AbstractInput.getValue(legislativeActVersionSelectOne) == null;
		if(LegislativeActVersion.class.equals(klass))
			return Boolean.TRUE;
		//if(input == includedSelectOne)
		//	return input.getValue() != null;
		//if(input == dateGreaterThanOrEqualCalendar)
		//	return dateGreaterThanOrEqualCalendar.getValue() != null;
		//if(input == dateLowerThanOrEqualCalendar)
		//	return dateLowerThanOrEqualCalendar.getValue() != null;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_LEGISLATIVE_ACT_SELECT_ONE.equals(fieldName) || input == legislativeActSelectOne)
			return Parameters.LEGISLATIVE_ACT_IDENTIFIER;
		if(FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE.equals(fieldName) || input == legislativeActVersionSelectOne)
			return Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER;
		if(FIELD_INCLUDED_SELECT_ONE.equals(fieldName) || input == includedSelectOne)
			return Parameters.REGULATORY_ACT_INCLUDED;
		if(FIELD_DATE_GREATER_THAN_OR_EQUAL_CALENDAR.equals(fieldName) || input == dateGreaterThanOrEqualCalendar)
			return Parameters.REGULATORY_ACT_DATE_GREATER_THAN_OR_EQUAL;
		if(FIELD_DATE_LOWER_THAN_OR_EQUAL_CALENDAR.equals(fieldName) || input == dateLowerThanOrEqualCalendar)
			return Parameters.REGULATORY_ACT_DATE_LOWER_THAN_OR_EQUAL;
		return super.buildParameterName(fieldName, input);
	}
	
	/*
	@Override
	protected String buildParameterValue(AbstractInput<?> input) {
		if(input == includedSelectOne)
			return input.getValue() == null ? null : input.getValue().toString();
		return super.buildParameterValue(input);
	}
	*/
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(legislativeActSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActSelectOne,Cell.FIELD_WIDTH,3));	
		}
		
		if(legislativeActVersionSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,legislativeActVersionSelectOne,Cell.FIELD_WIDTH,includedSelectOne == null ? 6 : 4));
		}
		
		if(includedSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,includedSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,includedSelectOne,Cell.FIELD_WIDTH,1));
		}
		
		if(dateGreaterThanOrEqualCalendar != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dateGreaterThanOrEqualCalendar.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dateGreaterThanOrEqualCalendar,Cell.FIELD_WIDTH,5));
		}
		
		if(dateLowerThanOrEqualCalendar != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dateLowerThanOrEqualCalendar.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dateLowerThanOrEqualCalendar,Cell.FIELD_WIDTH,5));
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
		StringBuilder stringBuilder = new StringBuilder(prefix);
		if(legislativeActInitial == null)
			return stringBuilder.toString();		
		if(legislativeActVersionInitial == null) {
			stringBuilder.append(" disponible dans le "+legislativeActInitial.getName());
			return stringBuilder.toString();
		}		
		if(includedInitial != null) {
			if(includedInitial)
				stringBuilder.append(" inclus ");
			else
				stringBuilder.append(" non inclus ");	
		}
		stringBuilder.append(" dans le "+legislativeActInitial.getName()+" | "+legislativeActVersionInitial.getName());
		if(dateGreaterThanOrEqualInitial != null || dateLowerThanOrEqualInitial != null) {
			stringBuilder.append(String.format("| %s - %s ",dateGreaterThanOrEqualInitial == null ? "<":TimeHelper.formatDate(dateGreaterThanOrEqualInitial),dateLowerThanOrEqualInitial == null ? ">":TimeHelper.formatDate(dateLowerThanOrEqualInitial)));
		}
		return stringBuilder.toString();
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(legislativeActInitial == null)
			columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_YEAR));
		columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_CODE,RegulatoryAct.FIELD_NAME,RegulatoryAct.FIELD_DATE_AS_STRING,RegulatoryAct.FIELD_ENTRY_AUTHORIZATION_AMOUNT,RegulatoryAct.FIELD_PAYMENT_CREDIT_AMOUNT));
		if(includedInitial == null)
			columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_INCLUDED_AS_STRING));
		columnsFieldsNames.addAll(List.of(RegulatoryAct.FIELD_AUDIT));
		return columnsFieldsNames;
	}
	
	public LegislativeAct getLegislativeAct() {
		return (LegislativeAct) AbstractInput.getValue(legislativeActSelectOne);
	}
	
	public LegislativeActVersion getLegislativeActVersion() {
		return (LegislativeActVersion) AbstractInput.getValue(legislativeActVersionSelectOne);
	}
	
	public Date getDateLowerThanOrEqual() {
		return (Date) AbstractInput.getValue(dateLowerThanOrEqualCalendar);
	}
	
	public Date getDateGreaterThanOrEqual() {
		return (Date) AbstractInput.getValue(dateGreaterThanOrEqualCalendar);
	}
	
	public Boolean getIncluded() {
		return (Boolean) AbstractInput.getValue(includedSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,RegulatoryActFilterController controller,Boolean initial) {
		LegislativeActVersion legislativeActVersion = Boolean.TRUE.equals(initial) ? controller.legislativeActVersionInitial : controller.getLegislativeActVersion();
		if(legislativeActVersion == null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.legislativeActInitial : controller.getLegislativeAct()), filter);
		else
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, FieldHelper.readSystemIdentifier(legislativeActVersion), filter);
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.REGULATORY_ACT_INCLUDED, controller.getIncluded(), filter);
		Date date = controller.getDateGreaterThanOrEqual();
		if(date != null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.REGULATORY_ACT_DATE_GREATER_THAN_OR_EQUAL, date.getTime(), filter);
		date = controller.getDateLowerThanOrEqual();
		if(date != null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.REGULATORY_ACT_DATE_LOWER_THAN_OR_EQUAL, date.getTime(), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(RegulatoryActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_LEGISLATIVE_ACT_SELECT_ONE = "legislativeActSelectOne";
	public static final String FIELD_LEGISLATIVE_ACT_VERSION_SELECT_ONE = "legislativeActVersionSelectOne";
	public static final String FIELD_INCLUDED_SELECT_ONE = "includedSelectOne";
	public static final String FIELD_DATE_LOWER_THAN_OR_EQUAL_CALENDAR = "dateLowerThanOrEqualCalendar";
	public static final String FIELD_DATE_GREATER_THAN_OR_EQUAL_CALENDAR = "dateGreaterThanOrEqualCalendar";
}