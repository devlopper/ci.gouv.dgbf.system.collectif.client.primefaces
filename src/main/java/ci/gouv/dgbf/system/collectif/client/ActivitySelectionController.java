package ci.gouv.dgbf.system.collectif.client;
import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractSelectionController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.client.rest.Activity;
import ci.gouv.dgbf.system.collectif.server.client.rest.ActivityController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ActivitySelectionController extends AbstractSelectionController<Activity> implements Serializable {

	public ActivitySelectionController() {
		entityParameterName = Parameters.ACTIVITY_IDENTIFIER;
	}
	
	@Override
	protected Object[] getAutoCompleteArguments() {
		return ArrayUtils.addAll(super.getAutoCompleteArguments(), AutoComplete.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE,Boolean.FALSE
				,AutoComplete.FIELD_LISTENER,new AutoComplete.Listener.AbstractImpl<Activity>() {
			@Override
			public Collection<Activity> complete(AutoComplete autoComplete) {
				return __inject__(ActivityController.class).get(new Controller.GetArguments().setFilter(new Filter.Dto().addField(Parameters.SEARCH, autoComplete.get__queryString__())));
			}
		});
	}	
}