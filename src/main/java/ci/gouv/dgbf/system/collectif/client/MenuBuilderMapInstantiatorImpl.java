package ci.gouv.dgbf.system.collectif.client;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;

@ci.gouv.dgbf.system.collectif.server.api.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		sessionMenuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setCommandableName("Elaboration").setCommandableIcon(Icon.SUITCASE)
				/*.addChild(
						__inject__(MenuItemBuilder.class).setCommandableName("Saisir les dépenses").setCommandableNavigationIdentifier("activityCostUnitFundingEditAdjustmentsView").setCommandableIcon(Icon.PENCIL)
						,__inject__(MenuItemBuilder.class).setCommandableName("Saisir les resources").setCommandableNavigationIdentifier("activityRevenueCostUnitEditAdjustmentsView").setCommandableIcon(Icon.BANK)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Finex").setCommandableNavigationIdentifier("activityCostUnitFundingEditAdjustmentsFinexView").setCommandableIcon(Icon.PENCIL)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Lignes").setCommandableNavigationIdentifier("activityCostUnitFundingListView").setCommandableIcon(Icon.LIST)
						,__inject__(MenuItemBuilder.class).setCommandableName("Incohérences").setCommandableNavigationIdentifier("activityCostUnitFundingDashboardListWhereAvailablePaymentCreditIsNotEnoughView").setCommandableIcon(Icon.EYE)
						)*/
				);		
	}	
}
