package ci.gouv.dgbf.system.collectif.client.resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.InputNumber;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.rest.ResponseHelper;
import org.cyk.utility.service.client.SpecificServiceGetter;
import org.primefaces.PrimeFaces;

import ci.gouv.dgbf.system.collectif.server.client.rest.Resource;
import ci.gouv.dgbf.system.collectif.server.client.rest.ResourceController;
import ci.gouv.dgbf.system.collectif.server.client.rest.Revenue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ResourceAdjustPage extends AbstractPageContainerManagedImpl implements Serializable {

	@Inject private SpecificServiceGetter specificServiceGetter;
	@Inject private ResourceController resourceController;
	private ResourceFilterController filterController;
	private Layout layout;
	private DataTable dataTable;
	private List<Resource> resources;
	private InputNumber revenueAdjustmentInput;
	private Map<String,Object[]> initialValues = new HashMap<>();
	private CommandButton saveCommandButton;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ResourceFilterController();
		filterController.setIsRevenueAdjustmentEditable(Boolean.TRUE);
	}

	@Override
	protected String __getWindowTitleValue__() {
		return filterController.generateWindowTitleValue("Saisie des ajustements");
	}
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		revenueAdjustmentInput = buildAdjustmentInput(ResourceAdjustPage.class,FIELD_REVENUE_ADJUSTMENT_INPUT,Resource.FIELD_REVENUE);
		buildLayout();
	}
	
	private DataTable buildDataTable() {
		dataTable = ResourceListPage.buildDataTable(DataTable.FIELD_LISTENER,new DataTableListenerImpl()
				,DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,new LazyDataModel().setFilterController(filterController)
				,ResourceListPage.OUTCOME,OUTCOME
				,DataTable.FIELD_RENDER_TYPE,org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection.RenderType.INPUT
				);
		return dataTable;
	}
	
	private CommandButton buildSaveCommandButton() {
		saveCommandButton = CommandButton.build(CommandButton.FIELD_VALUE,"Enregistrer",CommandButton.FIELD_ICON,"fa fa-floppy-o"
				,CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES,List.of(RenderType.GROWL)
				,CommandButton.FIELD_LISTENER,new CommandButton.Listener.AbstractImpl() {
					@Override
					protected Object __runExecuteFunction__(AbstractAction action) {
						if(CollectionHelper.isEmpty(resources))
							throw new RuntimeException("Le tableau ne comporte aucune ligne à modifier");
						Collection<Resource> updatables = null;
						for(Resource resource : resources) {
							if(!Boolean.TRUE.equals(isHasBeenEdited(resource)))							
								continue;
							if(updatables == null)
								updatables = new ArrayList<>();
							updatables.add(resource);
						}
						if(CollectionHelper.isEmpty(updatables))
							throw new RuntimeException("Vous n'avez fait aucune modification");
						Response response = resourceController.adjust(updatables);
						updatables.forEach(updated -> {
							initialValues.put(updated.getIdentifier(), new Long[] {updated.getRevenue(Boolean.TRUE).getAdjustment()});
						});
						PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier());
						return ResponseHelper.getEntity(String.class, response);
					}
				},CommandButton.FIELD_STYLE_CLASS,"cyk-float-right");
		//saveCommandButton.addUpdates(":form:"+assignmentsDataTableCellIdentifier);
		return saveCommandButton;
	}
	
	private void buildLayout() {
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		cellsMaps.add(MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_BUILD_DEFFERED,Boolean.TRUE,Cell.FIELD_LISTENER,new Cell.Listener.AbstractImpl() {
			@Override
			public Object buildControl(Cell cell) {
				return buildDataTable();
			}
		},Cell.FIELD_WIDTH,12));
		//cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,buildDataTable(),Cell.FIELD_WIDTH,12));		
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,buildSaveCommandButton(),Cell.FIELD_WIDTH,12));
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	/**/
	
	public static InputNumber buildAdjustmentInput(Class<?> klass,String inputFieldName,String resourceFieldName) {
		InputNumber input = InputNumber.build(InputNumber.FIELD_MIN_VALUE,Long.MIN_VALUE,InputNumber.FIELD_MAX_VALUE,Long.MAX_VALUE
				,InputNumber.FIELD_DECIMAL_PLACES,0,InputNumber.FIELD_INPUT_STYLE_CLASS,"cyk-text-align-right");
		input.setBindingByDerivation(StringHelper.getVariableNameFrom(klass.getSimpleName())+"."+inputFieldName
				, "record."+resourceFieldName+".adjustment");
		return input;
	}
	
	/**/
	
/**/
	
	@Getter @Setter @Accessors(chain=true)
	public class DataTableListenerImpl extends ResourceListPage.DataTableListenerImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			if(Resource.FIELD_REVENUE_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Montant");
				map.put(Column.FIELD_INPUTABLE, Boolean.TRUE);
			}
			return map;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public class LazyDataModel extends ResourceListPage.LazyDataModel implements Serializable {	
		
		@Override
		protected void process(Collection<Resource> list) {
			initialValues.clear();
			super.process(list);
			resources = (List<Resource>) list;
		}
		
		@Override
		protected void process(Resource resource) {
			super.process(resource);
			initialValues.put(resource.getIdentifier(), new Object[] {resource.getRevenue(Boolean.TRUE).getAdjustment()});
		}
	}
	
	private Boolean isHasBeenEdited(Resource resource) {
		Object[] initials = initialValues.get(resource.getIdentifier());
		if(initials == null)
			return Boolean.FALSE;
		if(isHasBeenEdited(resource,FieldHelper.join(Resource.FIELD_REVENUE,Revenue.FIELD_ADJUSTMENT),0))
			return Boolean.TRUE;
		return  Boolean.FALSE;
	}

	private Boolean isHasBeenEdited(Resource resource,String fieldName,Integer initialValueIndex) {
		Object[] initials = initialValues.get(resource.getIdentifier());
		Long initialValue = (Long) initials[initialValueIndex];
		Long current = (Long) FieldHelper.read(resource, fieldName);
		if(initialValue == null)
			return current != null;
		else
			return current == null || !initialValue.equals(current);
	}
	
	/**/
	
	public static final String FIELD_REVENUE_ADJUSTMENT_INPUT = "revenueAdjustmentInput";
	
	public static final String OUTCOME = "resourceAdjustView";
}