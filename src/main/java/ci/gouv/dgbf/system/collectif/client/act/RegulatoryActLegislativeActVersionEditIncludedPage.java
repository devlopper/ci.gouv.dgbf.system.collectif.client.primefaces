package ci.gouv.dgbf.system.collectif.client.act;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.Input;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoice;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceMany;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceManyPickList;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOne;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOneCombo;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.data.Form;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectManyPickList;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityEditPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;
import org.primefaces.model.DualListModel;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.LegislativeActVersionDto;
import ci.gouv.dgbf.system.collectif.server.api.service.RegulatoryActDto;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import ci.gouv.dgbf.system.collectif.server.client.rest.RegulatoryAct;
import ci.gouv.dgbf.system.collectif.server.client.rest.RegulatoryActController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class RegulatoryActLegislativeActVersionEditIncludedPage extends AbstractEntityEditPageContainerManagedImpl<RegulatoryActLegislativeActVersionEditIncludedPage.Data> implements Serializable {

	private LegislativeActVersion legislativeActVersion;
	
	@Override
	protected void __listenBeforePostConstruct__(){
		super.__listenBeforePostConstruct__();
		legislativeActVersion = __inject__(LegislativeActVersionController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER)
				, new Controller.GetArguments().projections(LegislativeActVersionDto.JSON_IDENTIFIER,LegislativeActVersionDto.JSON_CODE,LegislativeActVersionDto.JSON_NAME));
	}
	
	@Override
	protected Form __buildForm__() {
		return buildForm(Form.FIELD_CONTAINER,this,LegislativeActVersion.class,legislativeActVersion);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		String string = "Inclusion "+ci.gouv.dgbf.system.collectif.server.api.persistence.RegulatoryAct.NAME;
		if(legislativeActVersion != null)
			string = string +" | "+ legislativeActVersion.getName();
		return string;
	}
	
	private static List<RegulatoryAct> getRegulatoryActs(LegislativeActVersion legislativeActVersion,Boolean included) {
		List<RegulatoryAct> list = (List<RegulatoryAct>) __inject__(RegulatoryActController.class).get(new Controller.GetArguments().projections(RegulatoryActDto.JSON_IDENTIFIER,RegulatoryActDto.JSON_CODE,RegulatoryActDto.JSON_NAME)
				.setFilter(new Filter.Dto()
						.addField(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER, legislativeActVersion.getIdentifier())
						.addField(Parameters.REGULATORY_ACT_INCLUDED, included)
						).setPageable(Boolean.FALSE));
		if(list == null)
			list = new ArrayList<>();
		return list;
	}
	
	public static Form buildForm(Map<Object,Object> map) {
		if(map == null)
			map = new HashMap<>();
		LegislativeActVersion legislativeActVersion = (LegislativeActVersion) MapHelper.readByKey(map, LegislativeActVersion.class);
		Data data = new Data();
		data.setLegislativeActVersion(legislativeActVersion);
		if(data.getLegislativeActVersion() != null) {
			data.getRegulatoryActs().setSource(getRegulatoryActs(data.getLegislativeActVersion(), Boolean.FALSE));
			data.getRegulatoryActs().setTarget(getRegulatoryActs(data.getLegislativeActVersion(), Boolean.TRUE));
		}
		MapHelper.writeByKeyDoNotOverride(map, Form.FIELD_ENTITY_CLASS, Data.class);
		MapHelper.writeByKeyDoNotOverride(map, Form.FIELD_ENTITY, data);
		MapHelper.writeByKeyDoNotOverride(map,Form.FIELD_ACTION, Action.UPDATE);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_INJECTABLE, Boolean.FALSE);
		Collection<String> fieldsNames = new ArrayList<String>();
		if(legislativeActVersion == null)
			fieldsNames.add(Data.FIELD_LEGISLATIVE_ACT_VERSION);
		fieldsNames.add(Data.FIELD_REGULATORY_ACTS);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_INPUTS_FIELDS_NAMES, fieldsNames);
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
			__inject__(RegulatoryActController.class).includeComprehensively(data.getRegulatoryActs().getTarget(), data.getLegislativeActVersion());
		}
		
		@Override
		public void redirect(Form form, Object request) {
			/*AbstractPageContainerManagedImpl page = (AbstractPageContainerManagedImpl) form.getContainer();
			if(page != null && !Boolean.TRUE.equals(page.getIsRenderTypeDialog()))
				Redirector.getInstance().redirect(new Redirector.Arguments().outcome(RegulatoryActListPage.OUTCOME));
			else
				super.redirect(form, request);*/
		}
	}
	
	public static class FormConfiguratorListenerImpl extends Form.ConfiguratorImpl.Listener.AbstractImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getInputArguments(Form form, String fieldName) {
			Map<Object, Object> map = super.getInputArguments(form, fieldName);
			if(Data.FIELD_REGULATORY_ACTS.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, ci.gouv.dgbf.system.collectif.server.api.persistence.RegulatoryAct.NAME_PLURAL);
				map.put(SelectManyPickList.FIELD_CHOICE_CLASS,RegulatoryAct.class);
				map.put(SelectManyPickList.FIELD_SHOW_CHECK_BOX,Boolean.TRUE);
				map.put(SelectManyPickList.FIELD_SHOW_SOURCE_FILTER,Boolean.TRUE);
				map.put(SelectManyPickList.FIELD_SHOW_TARGET_FILTER,Boolean.TRUE);
				map.put(SelectManyPickList.FIELD_LISTENER,new SelectManyPickList.Listener.AbstractImpl<RegulatoryAct>() {
					/*@Override
					protected List<RegulatoryAct> __computeChoices__(SelectManyPickList input, Class<?> entityClass) {
						return (List<RegulatoryAct>) __inject__(RegulatoryActController.class).get();
					}
					*/
					@Override
					public Object getChoiceLabel(SelectManyPickList input, RegulatoryAct choice) {
						if(choice == null)
							return super.getChoiceLabel(input, choice);
						return StringUtils.defaultIfBlank((String)choice.getName(),StringUtils.repeat("__NO_LABEL_FOUND__", 1));
					}
					
					/*@Override
					protected List<RegulatoryAct> __computeSelected__(SelectManyPickList input) {
						return new ArrayList<>();
					}*/
				});
				
				//map.put(SelectManyPickList.FIELD_CHOICES,__inject__(RegulatoryActController.class).get());
				//map.put(SelectManyPickList.ConfiguratorImpl.FIELD_AVAILABLE,__inject__(RegulatoryActController.class).get());
				//map.put(SelectManyPickList.ConfiguratorImpl.FIELD_SELECTED,null);
			}else if(Data.FIELD_LEGISLATIVE_ACT_VERSION.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, ci.gouv.dgbf.system.collectif.server.api.persistence.LegislativeActVersion.NAME);
				map.put(SelectOneCombo.FIELD_LISTENER, new SelectOneCombo.Listener.AbstractImpl<LegislativeActVersion>() {
					@Override
					protected Collection<LegislativeActVersion> __computeChoices__(AbstractInputChoice<LegislativeActVersion> input, Class<?> entityClass) {
						Collection<LegislativeActVersion> choices = DependencyInjection.inject(LegislativeActVersionController.class).get();
						CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
						return choices;
					}
					
					@Override
					public void select(AbstractInputChoiceOne input, LegislativeActVersion legislativeActVersion) {
						super.select(input, legislativeActVersion);
						
					}
				});
			}/*else if(Data.FIELD_DATE.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Date");
			}else if(Data.FIELD_EXERCISE.equals(fieldName)) {
				map.put(AbstractInputChoice.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Exercice");
				Collection<Exercise> choices = __inject__(ExerciseController.class).get();
				map.put(AbstractInputChoice.FIELD_CHOICES,choices);
			}*/
			return map;
		}
		
		@Override
		public Map<Object, Object> getCommandButtonArguments(Form form, Collection<AbstractInput<?>> inputs) {
			Map<Object, Object> map = super.getCommandButtonArguments(form, inputs);
			map.put(CommandButton.FIELD_VALUE, "Inclure");
			return map;
		}
	}
	
	@Getter @Setter
	public static class Data {
		@NotNull @Input @InputChoice @InputChoiceOne @InputChoiceOneCombo
		private LegislativeActVersion legislativeActVersion;
		
		@Input @InputChoice @InputChoiceMany @InputChoiceManyPickList
		private DualListModel<RegulatoryAct> regulatoryActs = new DualListModel<RegulatoryAct>();
		
		public static final String FIELD_LEGISLATIVE_ACT_VERSION = "legislativeActVersion";
		public static final String FIELD_REGULATORY_ACTS = "regulatoryActs";
	}
	
	public static final String OUTCOME = "regulatoryActLegislativeActVersionEditIncludedView";
}