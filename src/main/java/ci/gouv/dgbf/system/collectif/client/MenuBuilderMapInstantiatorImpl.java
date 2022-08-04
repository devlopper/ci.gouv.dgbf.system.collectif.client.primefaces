package ci.gouv.dgbf.system.collectif.client;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;

import ci.gouv.dgbf.system.collectif.client.act.RegulatoryActLegislativeActVersionEditIncludedPage;

@ci.gouv.dgbf.system.collectif.server.api.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		sessionMenuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setCommandableName("Collectif").setCommandableIcon(Icon.SUITCASE)
				.addChild(
						__inject__(MenuItemBuilder.class).setCommandableName("Création collectif budgétaire").setCommandableNavigationIdentifier("legislativeActCreateView").setCommandableIcon(Icon.PLUS)
						,__inject__(MenuItemBuilder.class).setCommandableName("Collectif budgétaire en cours").setCommandableNavigationIdentifier("legislativeActReadView").setCommandableIcon(Icon.PLUS)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Collectifs budgétaires").setCommandableNavigationIdentifier("legislativeActListView").setCommandableIcon(Icon.LIST)
						,__inject__(MenuItemBuilder.class).setCommandableName("Versions collectifs budgétaires").setCommandableNavigationIdentifier("legislativeActVersionListView").setCommandableIcon(Icon.LIST)
						
						,__inject__(MenuItemBuilder.class).setCommandableName("Marquer les actes").setCommandableNavigationIdentifier(RegulatoryActLegislativeActVersionEditIncludedPage.OUTCOME).setCommandableIcon(Icon.LINK)
						
						//,__inject__(MenuItemBuilder.class).setCommandableName("Lister les dépenses").setCommandableNavigationIdentifier("expenditureListView").setCommandableIcon(Icon.LIST)
						,__inject__(MenuItemBuilder.class).setCommandableName("Ajuster les dépenses").setCommandableNavigationIdentifier("expenditureAdjustView").setCommandableIcon(Icon.PENCIL)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Lister les ressources").setCommandableNavigationIdentifier("resourceListView").setCommandableIcon(Icon.LIST)
						,__inject__(MenuItemBuilder.class).setCommandableName("Ajuster les ressources").setCommandableNavigationIdentifier("resourceAdjustView").setCommandableIcon(Icon.PENCIL)
						,__inject__(MenuItemBuilder.class).setCommandableName("Actes de gestion").setCommandableNavigationIdentifier("regulatoryActListView").setCommandableIcon(Icon.BANK)
						,__inject__(MenuItemBuilder.class).setCommandableName("Actes générés").setCommandableNavigationIdentifier("generatedActListView").setCommandableIcon(Icon.BANK)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Incohérences").setCommandableNavigationIdentifier("activityCostUnitFundingDashboardListWhereAvailablePaymentCreditIsNotEnoughView").setCommandableIcon(Icon.EYE)
						)
				);		
	}	
}
