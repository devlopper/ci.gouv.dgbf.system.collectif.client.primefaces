package ci.gouv.dgbf.system.collectif.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.collectif.client.Helper;
import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class LegislativeActFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo yearSelectOne;
	
	private LegislativeAct yearInitial;
	
	public LegislativeActFilterController() {
		if(yearInitial == null)
			yearInitial = Helper.getLegislativeActFromRequestParameter(null);
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_YEAR_SELECT_ONE.equals(fieldName))
			return yearInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_YEAR_SELECT_ONE, LegislativeActVersion.class);
		
		enableValueChangeListeners();
		selectByValueSystemIdentifier();		
	}
	
	private void enableValueChangeListeners() {
		yearSelectOne.enableValueChangeListener(CollectionHelper.listOf(Boolean.TRUE));
	}
	
	private void selectByValueSystemIdentifier() {
		yearSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_YEAR_SELECT_ONE.equals(fieldName))
			return buildLegislativeActSelectOne((LegislativeAct) value);
		return null;
	}
	
	private SelectOneCombo buildLegislativeActSelectOne(LegislativeAct legislativeAct) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,legislativeAct,SelectOneCombo.FIELD_CHOICE_CLASS,LegislativeAct.class
				,SelectOneCombo.FIELD_CHOICES,LegislativeAct.buildChoices(),SelectOneCombo.FIELD_CHOICES_INITIALIZED,Boolean.TRUE,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<LegislativeAct>() {
			
			
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
		return input;
	}
	
	@Override
	protected Boolean isSelectRedirectorArgumentsParameter(Class<?> klass, AbstractInput<?> input) {
		if(LegislativeActVersion.class.equals(klass))
			return Boolean.TRUE;
		return super.isSelectRedirectorArgumentsParameter(klass, input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(yearSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,yearSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,yearSelectOne,Cell.FIELD_WIDTH,9));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,1));	
		return cellsMaps;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(yearInitial != null) {
			strings.add(yearInitial.getName());
		}
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.addAll(List.of(LegislativeActVersion.FIELD_CODE,LegislativeActVersion.FIELD_NAME));
		columnsFieldsNames.addAll(List.of(LegislativeActVersion.FIELD___AUDIT__));
		return columnsFieldsNames;
	}
	
	public LegislativeAct getLegislativeAct() {
		return (LegislativeAct) AbstractInput.getValue(yearSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,LegislativeActFilterController controller,Boolean initial) {
		filter = Filter.Dto.addFieldIfValueNotNull(Parameters.LEGISLATIVE_ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.yearInitial : controller.getLegislativeAct()), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(LegislativeActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_YEAR_SELECT_ONE = "yearSelectOne";
}