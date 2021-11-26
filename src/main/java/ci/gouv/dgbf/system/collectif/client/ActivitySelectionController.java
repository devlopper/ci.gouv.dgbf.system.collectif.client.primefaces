package ci.gouv.dgbf.system.collectif.client;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.ajax.Ajax;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.panel.Dialog;

import ci.gouv.dgbf.system.collectif.server.client.rest.Activity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ActivitySelectionController implements Serializable {

	private Dialog dialog;
	private CommandButton showDialogCommandButton;
	
	private AutoComplete activityAutoComplete;
	private Redirector.Arguments onSelectRedirectorArguments;
	
	private CommandButton selectCommandButton;
	
	private Layout layout;
	
	public ActivitySelectionController() {
		buildDialog();
		buildShowDialogCommandButton();
		
		buildActivityAutoComplete();
		buildLayout();
	}
	
	public Redirector.Arguments getOnSelectRedirectorArguments(Boolean injectIfNull) {
		if(onSelectRedirectorArguments == null && Boolean.TRUE.equals(injectIfNull))
			onSelectRedirectorArguments = new Redirector.Arguments();
		return onSelectRedirectorArguments;
	}
	
	private void buildDialog() {
		dialog = Dialog.build(Dialog.FIELD_HEADER,"Recherche d'une activité",Dialog.FIELD_MODAL,Boolean.TRUE
				,Dialog.ConfiguratorImpl.FIELD_COMMAND_BUTTONS_BUILDABLE,Boolean.FALSE);
		dialog.addStyleClasses("cyk-min-width-90-percent");
	}
	
	private void buildShowDialogCommandButton() {
		showDialogCommandButton = CommandButton.build(CommandButton.FIELD_VALUE,"Rechercher",CommandButton.FIELD_ICON,"fa fa-search"
				//,CommandButton.FIELD_IMMEDIATE,Boolean.TRUE,CommandButton.FIELD_PROCESS,"@this"
				,CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.SHOW_DIALOG,CommandButton.FIELD___DIALOG__,dialog);		
	}
	
	private void buildActivityAutoComplete() {
		activityAutoComplete = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,Activity.class,AutoComplete.FIELD_LISTENER,new AutoComplete.Listener.AbstractImpl<Activity>() {
			@Override
			public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
				
			}
		},AutoComplete.FIELD_PLACEHOLDER,"Veuillez saisir une partie du code ou du libellé de l'activité");
		
		activityAutoComplete.enableAjaxItemSelect();
		activityAutoComplete.getAjaxes().get("itemSelect").setListener(new Ajax.Listener.AbstractImpl() {
			@Override
			protected void run(AbstractAction action) {
				Activity activity = (Activity) FieldHelper.read(action.get__argument__(), "source.value");
				if(activity != null && onSelectRedirectorArguments != null) {
					onSelectRedirectorArguments.addParameters(Map.of(ParameterName.stringify(Activity.class),List.of(activity.getIdentifier())));
					Redirector.getInstance().redirect(onSelectRedirectorArguments);				
				}
			}
		});
		activityAutoComplete.getAjaxes().get("itemSelect").setDisabled(Boolean.FALSE);
	}
	
	private void buildLayout() {
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,List.of(
				MapHelper.instantiate(Cell.FIELD_CONTROL,activityAutoComplete,Cell.FIELD_WIDTH,12)
		));
	}
}