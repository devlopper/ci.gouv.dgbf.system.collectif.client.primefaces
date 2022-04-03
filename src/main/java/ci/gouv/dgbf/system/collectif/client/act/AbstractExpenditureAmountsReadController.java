package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractReadController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.output.OutputText;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.panel.Panel;

import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureAmounts;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractExpenditureAmountsReadController<AMOUNTS extends ExpenditureAmounts> extends AbstractReadController implements Serializable {

	private AMOUNTS amounts;
	private String name;
	
	public AbstractExpenditureAmountsReadController(AMOUNTS amounts,String name) {
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
		addLabelValue(cellsMaps, "Budget actuel", amounts == null ? null : NumberHelper.format(amounts.getActual()));
		addLabelValue(cellsMaps, "Mouvement inclu", amounts == null ? null : NumberHelper.format(amounts.getMovementIncluded()));
		addLabelValue(cellsMaps, "Disponible", amounts == null ? null : NumberHelper.format(amounts.getAvailable()));
		
		addLabelValue(cellsMaps, "Ajustement attendu", amounts == null ? null : NumberHelper.format(amounts.getExpectedAdjustment()));
		addLabelValue(cellsMaps, "Ajustement saisi", amounts == null ? null : NumberHelper.format(amounts.getAdjustment()));
		addLabelValue(cellsMaps, "Ecart ajustement(attendu - saisi)", amounts == null ? null : NumberHelper.format(amounts.getExpectedAdjustmentMinusAdjustment()));
		
		addLabelValue(cellsMaps, "Total(Budget actuel - Mouvement inclu + Ajustement saisi)", amounts == null ? null : NumberHelper.format(amounts.getActualMinusMovementIncludedPlusAdjustment()));
		
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_LABEL_VALUE,Boolean.TRUE,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps
				,Layout.FIELD_CONTAINER,Panel.build(Panel.FIELD_HEADER,name,Panel.FIELD_TOGGLEABLE,Boolean.TRUE));
	}
	
	@Override
	protected OutputText buildValueOutputText(String value) {
		OutputText outputText = super.buildValueOutputText(value);
		outputText.setStyle("float: right;");
		return outputText;
	}
}