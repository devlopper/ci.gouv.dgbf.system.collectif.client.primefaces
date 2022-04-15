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
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class LegislativeActVersionReadController extends AbstractReadController implements Serializable {

	private LegislativeActVersion legislativeActVersion;
	private DataTable regulatoryActsDataTable;
	
	private LegislativeActVersion legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable;
	
	private LegislativeActVersion legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly;
	private LegislativeActVersion legislativeActVersionWithExpendituresAmountsWithAvailableOnly;

	public LegislativeActVersionReadController(LegislativeActVersion legislativeActVersion) {
		this.legislativeActVersion = legislativeActVersion;
		labelWidth = 6;
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		/*return CollectionHelper.listOf(
				MapHelper.instantiate(Cell.FIELD_CONTROL,buildInfosLayout(),Cell.FIELD_WIDTH,12)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,revenueReadController.getLayout(),Cell.FIELD_WIDTH,4)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,entryAuthorizationReadController.getLayout(),Cell.FIELD_WIDTH,4)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,paymentCreditReadController.getLayout(),Cell.FIELD_WIDTH,4)
				,MapHelper.instantiate(Cell.FIELD_CONTROL,buildRegulatoryActsDataTableLayout(),Cell.FIELD_WIDTH,12)
			);
		*/
		return CollectionHelper.listOf(
				MapHelper.instantiate(Cell.FIELD_CONTROL,buildInfosLayout(),Cell.FIELD_WIDTH,12)
				
				,MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
					@Override
					public Object buildControl(Cell cell) {
						LegislativeActVersion legislativeActVersion = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
								.projections(LegislativeActVersionDto.JSONS_RESOURCES_AMOUTNS).setFilter(new Filter.Dto().addField(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, LegislativeActVersionReadController.this.legislativeActVersion.getIdentifier())));
						RevenueReadController controller = new RevenueReadController(legislativeActVersion.getRevenue());
						controller.initialize();
						return controller.getLayout();
					}
				},Cell.FIELD_WIDTH,4)
				,MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
					@Override
					public Object buildControl(Cell cell) {
						readExpendituresAmountsWithoutIncludedMovementAndAvailable();
						EntryAuthorizationReadController controller = new EntryAuthorizationReadController(legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable.getEntryAuthorization());
						controller.setAmountsGetter(new EntryAuthorizationReadController.AmountsGetter() {
							@Override
							public Long getMovementIncluded() {
								readExpendituresAmountsWithIncludedMovementOnly();
								return legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getEntryAuthorization().getMovementIncluded();
							}
							
							@Override
							public Long getAvailable() {
								readExpendituresAmountsWithAvailableOnly();
								return legislativeActVersionWithExpendituresAmountsWithAvailableOnly.getEntryAuthorization().getAvailable();
							}
							
							@Override
							public Long getActualMinusMovementIncludedPlusAdjustment() {
								readExpendituresAmountsWithIncludedMovementOnly();
								return NumberHelper.getLong(NumberHelper.add(NumberHelper.subtract(legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable.getEntryAuthorization().getActual()
										,legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getEntryAuthorization().getMovementIncluded())
										,legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getEntryAuthorization().getAdjustment()));
							}
						});
						controller.initialize();
						return controller.getLayout();
					}
				},Cell.FIELD_WIDTH,4)
				,MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
					@Override
					public Object buildControl(Cell cell) {
						readExpendituresAmountsWithoutIncludedMovementAndAvailable();
						PaymentCreditReadController controller = new PaymentCreditReadController(legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable.getPaymentCredit());
						controller.setAmountsGetter(new PaymentCreditReadController.AmountsGetter() {
							@Override
							public Long getMovementIncluded() {
								readExpendituresAmountsWithIncludedMovementOnly();
								return legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getPaymentCredit().getMovementIncluded();
							}
							
							@Override
							public Long getAvailable() {
								readExpendituresAmountsWithAvailableOnly();
								return legislativeActVersionWithExpendituresAmountsWithAvailableOnly.getPaymentCredit().getAvailable();
							}
							
							@Override
							public Long getActualMinusMovementIncludedPlusAdjustment() {
								readExpendituresAmountsWithIncludedMovementOnly();
								return NumberHelper.getLong(NumberHelper.add(NumberHelper.subtract(legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable.getPaymentCredit().getActual()
										,legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getPaymentCredit().getMovementIncluded())
										,legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly.getPaymentCredit().getAdjustment()));
							}
						});
						controller.initialize();
						return controller.getLayout();
					}
				},Cell.FIELD_WIDTH,4)
				//,MapHelper.instantiate(Cell.FIELD_CONTROL,buildRegulatoryActsDataTableLayout(),Cell.FIELD_WIDTH,12)
			);
	}
	
	private void readExpendituresAmountsWithoutIncludedMovementAndAvailable() {
		synchronized(LegislativeActVersionReadController.class) {
			if(legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable == null)
				legislativeActVersionWithExpendituresAmountsWithoutIncludedMovementAndAvailable = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
						.projections(LegislativeActVersionDto.JSONS_EXPENDITURES_AMOUTNS_WITHOUT_INCLUDED_MOVEMENT_AND_AVAILABLE).setFilter(new Filter.Dto().addField(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, this.legislativeActVersion.getIdentifier())));
		}
	}
	
	private void readExpendituresAmountsWithIncludedMovementOnly() {
		synchronized(LegislativeActVersionReadController.class) {
			if(legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly == null)
				legislativeActVersionWithExpendituresAmountsWithIncludedMovementOnly = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
						.projections(LegislativeActVersionDto.JSONS_EXPENDITURES_AMOUTNS_WITH_INCLUDED_MOVEMENT_ONLY).setFilter(new Filter.Dto().addField(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, this.legislativeActVersion.getIdentifier())));
		}
	}
	
	private void readExpendituresAmountsWithAvailableOnly() {
		synchronized(LegislativeActVersionReadController.class) {
			if(legislativeActVersionWithExpendituresAmountsWithAvailableOnly == null)
				legislativeActVersionWithExpendituresAmountsWithAvailableOnly = __inject__(LegislativeActVersionController.class).getOne(new Controller.GetArguments()
						.projections(LegislativeActVersionDto.JSONS_EXPENDITURES_AMOUTNS_WITH_AVAILABLE_ONLY).setFilter(new Filter.Dto().addField(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, this.legislativeActVersion.getIdentifier())));
		}
	}
	
	private Layout buildInfosLayout() {
		if(legislativeActVersion == null)
			return null;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		addLabelValue(cellsMaps, "Collectif Budgétaire", legislativeActVersion.getActAsString());
		addLabelValue(cellsMaps, "Numéro", StringHelper.get(legislativeActVersion.getNumber()));
		addLabelValue(cellsMaps, "Code", legislativeActVersion.getCode());
		addLabelValue(cellsMaps, "Libellé", legislativeActVersion.getName());
		addLabelValue(cellsMaps, "Génération des actes", Boolean.TRUE.equals(legislativeActVersion.getActGeneratable()) ? "Actes à générer" : "Actes générés");	
		addLabelValue(cellsMaps, "Audit", legislativeActVersion.get__audit__());
		
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.ConfiguratorImpl.FIELD_LABEL_VALUE,Boolean.TRUE,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	/*private Layout buildRegulatoryActsDataTableLayout() {
		if(regulatoryActsDataTable == null)
			return null;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,regulatoryActsDataTable,Cell.FIELD_WIDTH,12));
		return Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}*/
}