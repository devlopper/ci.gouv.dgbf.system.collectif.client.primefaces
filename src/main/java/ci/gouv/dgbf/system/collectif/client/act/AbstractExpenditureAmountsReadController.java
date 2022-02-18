package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractReadController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;

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
		build(cellsMaps, amounts, name);
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.ConfiguratorImpl.FIELD_LABEL_VALUE,Boolean.TRUE,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	/**/
	
	public static void build(Collection<Map<Object,Object>> cellsMaps,ExpenditureAmounts amounts,String name) {
		addLabelValue(cellsMaps, String.format("Ajustement %s attendu",name), amounts == null ? null : StringHelper.get(amounts.getExpectedAdjustment()));
		addLabelValue(cellsMaps, String.format("Total ajustement %s saisi",name), amounts == null ? null : StringHelper.get(amounts.getAdjustment()));
		addLabelValue(cellsMaps, String.format("Ecart ajustement %s (attendu - saisi)",name), amounts == null ? null : StringHelper.get(amounts.getExpectedAdjustmentMinusAdjustment()));
	}
}