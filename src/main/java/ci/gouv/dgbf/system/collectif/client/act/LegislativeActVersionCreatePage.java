package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.Input;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoice;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOne;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOneCombo;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputText;
import org.cyk.utility.client.controller.component.annotation.InputNumber;
import org.cyk.utility.client.controller.web.jsf.primefaces.data.Form;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityEditPageContainerManagedImpl;

import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class LegislativeActVersionCreatePage extends AbstractEntityEditPageContainerManagedImpl<LegislativeActVersionCreatePage.Data> implements Serializable{

	@Override
	protected Form __buildForm__() {		
		return buildForm();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Création "+ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME;
	}
	
	public static Form buildForm(Map<Object,Object> map) {
		if(map == null)
			map = new HashMap<>();
		MapHelper.writeByKeyDoNotOverride(map, Form.FIELD_ENTITY_CLASS, Data.class);
		MapHelper.writeByKeyDoNotOverride(map,Form.FIELD_ACTION, Action.CREATE);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_INJECTABLE, Boolean.FALSE);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_INPUTS_FIELDS_NAMES, List.of(Data.FIELD_ACT,Data.FIELD_CODE,Data.FIELD_NAME));
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_LISTENER, new FormConfiguratorListenerImpl());		
		MapHelper.writeByKeyDoNotOverride(map,Form.FIELD_LISTENER, new FormListenerImpl());
		Form form = Form.build(map);
		return form;
	}
	
	public static Form buildForm(Object...arguments) {
		return buildForm(MapHelper.instantiate(arguments));
	}
	
	public static class FormListenerImpl extends Form.Listener.AbstractImpl implements Serializable {
		@Override
		public void act(Form form) {
			Data data = (Data) form.getEntity();
			__inject__(LegislativeActVersionController.class).create(new LegislativeActVersion().setCode(data.getCode()).setName(data.getName()).setNumber(data.getNumber()).setAct(data.getAct()));
		}
	}
	
	public static class FormConfiguratorListenerImpl extends Form.ConfiguratorImpl.Listener.AbstractImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getInputArguments(Form form, String fieldName) {
			Map<Object, Object> map = super.getInputArguments(form, fieldName);
			if(LegislativeActVersion.FIELD_CODE.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Code");
			}else if(LegislativeActVersion.FIELD_NAME.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Libellé");
			}else if(LegislativeActVersion.FIELD_ACT.equals(fieldName)) {
				map.put(AbstractInputChoice.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeAct.NAME);
				Collection<LegislativeAct> choices = __inject__(LegislativeActController.class).get();
				map.put(AbstractInputChoice.FIELD_CHOICES,choices);
			}
			return map;
		}
		
		@Override
		public Map<Object, Object> getCommandButtonArguments(Form form, Collection<AbstractInput<?>> inputs) {
			Map<Object, Object> map = super.getCommandButtonArguments(form, inputs);
			map.put(CommandButton.FIELD_VALUE, "Créer");
			return map;
		}
	}
	
	@Getter @Setter
	public static class Data {
		@NotNull @Input @InputChoice @InputChoiceOne @InputChoiceOneCombo private LegislativeAct act;
		@Input @InputNumber private Byte number;
		@Input @InputText private String code;
		@Input @InputText private String name;
		
		public static final String FIELD_ACT = "act";
		public static final String FIELD_CODE = "code";
		public static final String FIELD_NAME = "name";
	}
}