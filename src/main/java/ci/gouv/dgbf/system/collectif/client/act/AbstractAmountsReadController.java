package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractReadController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.output.OutputText;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.panel.Panel;

import ci.gouv.dgbf.system.collectif.server.client.rest.Amounts;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractAmountsReadController<AMOUNTS extends Amounts> extends AbstractReadController implements Serializable {

	protected AmountsGetter amountsGetter;
	protected AMOUNTS amounts;
	protected String name;
	
	public AbstractAmountsReadController(AMOUNTS amounts,String name) {
		this.amounts = amounts;
		this.name = name;
		labelWidth = 8;
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		return CollectionHelper.listOf(
				MapHelper.instantiate(Cell.FIELD_CONTROL,buildInfosLayout(),Cell.FIELD_WIDTH,12)
			);
	}
	
	private Layout buildInfosLayout() {
		if(amounts == null)
			return null;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		
		addLabelValue(cellsMaps, "Budget initial", amounts == null ? null : NumberHelper.format(amounts.getInitial()));
		addLabelValue(cellsMaps, "Mouvement", amounts == null ? null : NumberHelper.format(amounts.getMovement()));
		addLabelValue(cellsMaps, "Budget actuel(A)", amounts == null ? null : NumberHelper.format(amounts.getActual()));
		
		if(Boolean.TRUE.equals(hasIncludedMovement()))
			//addLabelValue(cellsMaps, "Mouvement inclu(B)", amounts == null ? null : NumberHelper.format(amounts.getMovementIncluded()));
			addLabelControlUsingMap(cellsMaps, "Mouvement inclu(B)", MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
				@Override
				public Object buildControl(Cell cell) {
					return buildValueOutputText(NumberHelper.format(amountsGetter == null ? amounts.getMovementIncluded() : amountsGetter.getMovementIncluded()));
				}
			}));
		if(Boolean.TRUE.equals(hasAvailable()))
			//addLabelValue(cellsMaps, "Disponible", amounts == null || amounts.getAvailable() == null ? null : NumberHelper.format(amounts.getAvailable()));
			addLabelControlUsingMap(cellsMaps, "Disponible", MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
				@Override
				public Object buildControl(Cell cell) {
					return buildValueOutputText(NumberHelper.format(amountsGetter == null ? amounts.getAvailable() : amountsGetter.getAvailable()));
				}
			}));
		
		addLabelValue(cellsMaps, LABEL_EXPECTED_ADJUSTMENT, amounts == null ? null : NumberHelper.format(amounts.getExpectedAdjustment()));
		addLabelValue(cellsMaps, LABEL_ADJUSTMENT+String.format("(%s)", hasIncludedMovement() ? "C" : "B"), amounts == null ? null : String.format("%s (%s%s%s)", NumberHelper.format(amounts.getAdjustment())
				,NumberHelper.format(amounts.getAdjustmentGreaterThanZero()),amounts.getAdjustmentLessThanZero() == null || amounts.getAdjustmentLessThanZero() == 0l ? "-":"",NumberHelper.format(amounts.getAdjustmentLessThanZero())));
		addLabelValue(cellsMaps, LABEL_ADJUSTMENT_GAP, amounts == null ? null : NumberHelper.format(amounts.getExpectedAdjustmentMinusAdjustment()));
		
		//addLabelValue(cellsMaps, String.format("Collectif(%s)",hasIncludedMovement() ? "A-B+C" : "A+B"), amounts == null ? null : NumberHelper.format(hasIncludedMovement() 
		//		? amounts.getActualMinusMovementIncludedPlusAdjustment() : amounts.getActualPlusAdjustment()));
		
		addLabelControlUsingMap(cellsMaps, String.format("Collectif(%s)",hasIncludedMovement() ? "A-B+C" : "A+B"), MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				return buildValueOutputText(NumberHelper.format(amountsGetter == null ? (hasIncludedMovement() ? amounts.getActualMinusMovementIncludedPlusAdjustment() : amounts.getActualPlusAdjustment()) : amountsGetter.getActualMinusMovementIncludedPlusAdjustment()));
			}
		}));
		
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_LABEL_VALUE,Boolean.TRUE,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps
				,Layout.FIELD_CONTAINER,Panel.build(Panel.FIELD_HEADER,name,Panel.FIELD_TOGGLEABLE,Boolean.TRUE));
	}
	
	protected Boolean hasIncludedMovement() {
		return Boolean.TRUE;
	}
	
	protected Boolean hasAvailable() {
		return Boolean.TRUE;
	}
	
	@Override
	protected OutputText buildValueOutputText(String value) {
		OutputText outputText = super.buildValueOutputText(value);
		outputText.addStyle("float: right;");
		return outputText;
	}
	
	@Override
	protected void addLabelControl(Collection<Map<Object, Object>> cellsMaps, String label, Object control) {
		if(label.equals(LABEL_ADJUSTMENT)) {
			OutputText outputText = (OutputText) control;
			outputText.addStyle("font-weight: bold;");
		}
		super.addLabelControl(cellsMaps, label, control);
	}
	
	/**/
	
	public static final String LABEL_EXPECTED_ADJUSTMENT = "Ajustement attendu";
	public static final String LABEL_ADJUSTMENT = "Ajustement saisi";
	public static final String LABEL_ADJUSTMENT_GAP = "Ecart ajustement(attendu - saisi)";
	
	public static interface AmountsGetter {
		Long getMovementIncluded();
		Long getAvailable();
		Long getActualMinusMovementIncludedPlusAdjustment();
	}
}