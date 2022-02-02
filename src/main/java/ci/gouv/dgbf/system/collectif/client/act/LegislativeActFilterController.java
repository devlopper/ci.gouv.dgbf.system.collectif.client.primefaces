package ci.gouv.dgbf.system.collectif.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.Amounts;
import ci.gouv.dgbf.system.collectif.server.client.rest.Exercise;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class LegislativeActFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo exerciseSelectOne;
	
	private Exercise exerciseInitial;
	
	public LegislativeActFilterController() {
		if(exerciseInitial == null)
			exerciseInitial = Helper.getExerciseFromRequestParameter();
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName))
			return exerciseInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_EXERCISE_SELECT_ONE, Exercise.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		exerciseSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		exerciseSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName))
			return buildExerciseSelectOne((Exercise) value);
		return null;
	}
	
	private SelectOneCombo buildExerciseSelectOne(Exercise exercise) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,exercise,SelectOneCombo.FIELD_CHOICE_CLASS,Exercise.class
				,SelectOneCombo.FIELD_CHOICES,Exercise.buildChoices(),SelectOneCombo.FIELD_CHOICES_INITIALIZED,Boolean.TRUE,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<Exercise>() {
			
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.Exercise.NAME);
		return input;
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName) || input == exerciseSelectOne)
			return Parameters.EXERCISE_IDENTIFIER;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(Exercise.class.equals(klass))
			return Boolean.TRUE;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(exerciseSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,exerciseSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,exerciseSelectOne,Cell.FIELD_WIDTH,9));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,1));	
		return cellsMaps;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(exerciseInitial != null) {
			strings.add(exerciseInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.addAll(List.of(LegislativeAct.FIELD_CODE,LegislativeAct.FIELD_NAME,LegislativeAct.FIELD_EXERCISE_AS_STRING,LegislativeAct.FIELD_DEFAULT_VERSION_AS_STRING,LegislativeAct.FIELD_IN_PROGRESS_AS_STRING));
		Helper.addAmountsColumnsNames(columnsFieldsNames,null,null, Amounts.FIELD_INITIAL,Amounts.FIELD_MOVEMENT,Amounts.FIELD_ACTUAL
				,Amounts.FIELD_MOVEMENT_INCLUDED,Amounts.FIELD_AVAILABLE,Amounts.FIELD_EXPECTED_ADJUSTMENT,Amounts.FIELD_ADJUSTMENT,Amounts.FIELD_EXPECTED_ADJUSTMENT_MINUS_ADJUSTMENT,Amounts.FIELD_ACTUAL_MINUS_MOVEMENT_INCLUDED_PLUS_ADJUSTMENT
				);
		columnsFieldsNames.addAll(List.of(LegislativeAct.FIELD___AUDIT__));
		return columnsFieldsNames;
	}
	
	public Exercise getExercise() {
		return (Exercise) AbstractInput.getValue(exerciseSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,LegislativeActFilterController controller,Boolean initial) {
		Exercise exercise = Boolean.TRUE.equals(initial) ? controller.exerciseInitial : controller.getExercise();
		if(exercise != null && exercise.getYear() != null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.EXERCISE_YEAR, exercise.getYear(), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(LegislativeActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_EXERCISE_SELECT_ONE = "exerciseSelectOne";
}