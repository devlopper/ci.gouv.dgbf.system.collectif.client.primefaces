package ci.gouv.dgbf.system.collectif.client;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import org.cyk.user.interface_.theme.web.jsf.primefaces.atlantis.dgbf.DesktopDefault;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.client.controller.component.menu.MenuBuilderMapInstantiator;
import org.cyk.utility.client.deployment.AbstractServletContextListener;

//import ci.gouv.dgbf.system.collectif.client.controller.impl.ApplicationScopeLifeCycleListener;

@WebListener
public class ServletContextListener extends AbstractServletContextListener implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(ServletContext context) {
		super.__initialize__(context);
		DependencyInjection.setQualifierClassTo(ci.gouv.dgbf.system.collectif.server.api.System.class, MenuBuilderMapInstantiator.class/*,EntitySaver.class*/);
		//__inject__(ApplicationScopeLifeCycleListener.class).initialize(null);
		
		DesktopDefault.initialize(null,null);
		org.cyk.utility.security.keycloak.client.ApplicationScopeLifeCycleListener.enable(context, "/keycloak/*","/private/*");
		/*
		DesktopDefault.MENU_IDENTIFIER = ConfigurationHelper.getValueAsString(VariableName.USER_INTERFACE_THEME_MENU_IDENTIFIER);
		DesktopDefault.DYNAMIC_MENU = ConfigurationHelper.is(VariableName.USER_INTERFACE_THEME_MENU_IS_DYNAMIC);
		DesktopDefault.IS_SHOW_USER_MENU = DesktopDefault.DYNAMIC_MENU;
		if(DesktopDefault.DYNAMIC_MENU) {
			
		}else {
			DesktopDefault.SYSTEM_LINK = "#";
		}
		*/
		//ClientRequestFilterImpl.LOGGABLE = Boolean.TRUE;
		//ClientRequestFilterImpl.LOG_LEVEL = Level.INFO;
	}	
}