package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractReadController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;

import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class LegislativeActVersionReadController extends AbstractReadController implements Serializable {

	private LegislativeActVersion legislativeActVersion;
	private EntryAuthorizationReadController entryAuthorizationReadController;
	private PaymentCreditReadController paymentCreditReadController;
	private DataTable regulatoryActsDataTable;

	public LegislativeActVersionReadController(LegislativeActVersion legislativeActVersion) {
		this.legislativeActVersion = legislativeActVersion;
		entryAuthorizationReadController = new EntryAuthorizationReadController(legislativeActVersion.getEntryAuthorization());
		paymentCreditReadController = new PaymentCreditReadController(legislativeActVersion.getPaymentCredit());
	}
	
	@Override
	public void initialize() {
		entryAuthorizationReadController.initialize();
		paymentCreditReadController.initialize();
		super.initialize();
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		return CollectionHelper.listOf(
				MapHelper.instantiate(Cell.FIELD_CONTROL,buildInfosLayout(),Cell.FIELD_WIDTH,12)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,entryAuthorizationReadController.getLayout(),Cell.FIELD_WIDTH,12)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,paymentCreditReadController.getLayout(),Cell.FIELD_WIDTH,12)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,buildRegulatoryActsDataTableLayout(),Cell.FIELD_WIDTH,12)
			);
	}
	
	private Layout buildInfosLayout() {
		if(legislativeActVersion == null)
			return null;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		addLabelValue(cellsMaps, "Collectif Budgétaire", legislativeActVersion.getActAsString());
		addLabelValue(cellsMaps, "Numéro", StringHelper.get(legislativeActVersion.getNumber()));
		addLabelValue(cellsMaps, "Code", legislativeActVersion.getCode());
		addLabelValue(cellsMaps, "Libellé", legislativeActVersion.getName());
		addLabelValue(cellsMaps, "Audit", legislativeActVersion.get__audit__());
		
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.ConfiguratorImpl.FIELD_LABEL_VALUE,Boolean.TRUE,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	private Layout buildRegulatoryActsDataTableLayout() {
		if(regulatoryActsDataTable == null)
			return null;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,regulatoryActsDataTable,Cell.FIELD_WIDTH,12));
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}	
}