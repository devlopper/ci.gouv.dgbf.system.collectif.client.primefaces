package ci.gouv.dgbf.system.collectif.client.expenditure;
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
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
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

import ci.gouv.dgbf.system.collectif.server.client.rest.EntryAuthorization;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ExpenditureAdjustPage extends AbstractPageContainerManagedImpl implements Serializable {

	@Inject private SpecificServiceGetter specificServiceGetter;
	@Inject private ExpenditureController expenditureController;
	private ExpenditureFilterController filterController;
	private Layout layout;
	private DataTable dataTable;
	private List<Expenditure> expenditures;
	private InputNumber entryAuthorizationAdjustmentInput;
	private InputNumber paymentCreditAdjustmentInput;
	private Map<String,Object[]> initialValues = new HashMap<>();
	private CommandButton saveCommandButton;
	private Boolean isInvestment;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ExpenditureFilterController();
		if(Boolean.TRUE.equals(getIsRenderTypeDialog()))
			filterController.setRenderType(AbstractFilterController.RenderType.NONE);
		filterController.setIsEntryAuthorizationAdjustmentEditable(Boolean.TRUE).setIsPaymentCreditAdjustmentEditable(Boolean.TRUE);
	}

	@Override
	protected String __getWindowTitleValue__() {
		return filterController.generateWindowTitleValue("Saisie des ajustements");
	}
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		isInvestment = filterController != null && Boolean.TRUE.equals(filterController.isInvestment());
		entryAuthorizationAdjustmentInput = buildAdjustmentInput(ExpenditureAdjustPage.class,FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT_INPUT,Expenditure.FIELD_ENTRY_AUTHORIZATION,filterController.isAdjustable());
		paymentCreditAdjustmentInput = buildAdjustmentInput(ExpenditureAdjustPage.class,FIELD_PAYMENT_CREDIT_ADJUSTMENT_INPUT,Expenditure.FIELD_PAYMENT_CREDIT,filterController.isAdjustable());
		buildLayout();
	}
	
	private DataTable buildDataTable() {
		dataTable = ExpenditureListPage.buildDataTable(DataTable.FIELD_LISTENER,new DataTableListenerImpl().setAdjustmentEditable(Boolean.TRUE)
				,DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,new LazyDataModel().setFilterController(filterController)
				,ExpenditureListPage.OUTCOME,OUTCOME
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
						if(CollectionHelper.isEmpty(expenditures))
							throw new RuntimeException(String.format("Le tableau ne comporte aucune %s à modifier",ci.gouv.dgbf.system.collectif.server.api.persistence.Expenditure.NAME));
						Collection<Expenditure> updatables = null;
						for(Expenditure expenditure : expenditures) {
							if(!Boolean.TRUE.equals(isHasBeenEdited(expenditure)))							
								continue;
							if(updatables == null)
								updatables = new ArrayList<>();
							updatables.add(expenditure);
						}
						if(CollectionHelper.isEmpty(updatables))
							throw new RuntimeException("Vous n'avez fait aucune modification");
						Response response = isInvestment ? expenditureController.adjust(updatables) : expenditureController.adjustByEntryAuthorizations(updatables);
						updatables.forEach(updated -> {
							initialValues.put(updated.getIdentifier(), new Long[] {updated.getEntryAuthorization(Boolean.TRUE).getAdjustment(),updated.getPaymentCredit(Boolean.TRUE).getAdjustment()});
						});
						PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier());
						if(!Boolean.TRUE.equals(isRenderTypeDialog))
							dataTable.getRemoteCommandByName(ExpenditureListPage.REMOTE_COMMAND_UPDATE_INCLUDED_MOVEMENT_AVAILABLE_SUMS).executeScript();
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
	
	public static InputNumber buildAdjustmentInput(Class<?> klass,String inputFieldName,String expenditureFieldName,Boolean adjustable) {
		InputNumber input = InputNumber.build(InputNumber.FIELD_MIN_VALUE,Long.MIN_VALUE,InputNumber.FIELD_MAX_VALUE,Long.MAX_VALUE
				,InputNumber.FIELD_DECIMAL_PLACES,0,InputNumber.FIELD_INPUT_STYLE_CLASS,"cyk-text-align-right"
				,InputNumber.FIELD_READ_ONLY,!Boolean.TRUE.equals(adjustable));
		input.setBindingByDerivation(StringHelper.getVariableNameFrom(klass.getSimpleName())+"."+inputFieldName
				, "record."+expenditureFieldName+".adjustment");
		return input;
	}
	
	/**/
	
/**/
	
	@Getter @Setter @Accessors(chain=true)
	public class DataTableListenerImpl extends ExpenditureListPage.DataTableListenerImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			if(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "A.E.");
				map.put(Column.FIELD_INPUTABLE, Boolean.TRUE);
			}else if(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "C.P.");
				map.put(Column.FIELD_INPUTABLE, Boolean.TRUE);
			}
			return map;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public class LazyDataModel extends ExpenditureListPage.LazyDataModel implements Serializable {	
		
		@Override
		protected void process(Collection<Expenditure> list) {
			initialValues.clear();
			super.process(list);
			expenditures = (List<Expenditure>) list;
		}
		
		@Override
		protected void process(Expenditure expenditure) {
			super.process(expenditure);
			initialValues.put(expenditure.getIdentifier(), new Object[] {
					expenditure.getEntryAuthorization(Boolean.TRUE).getAdjustment()
					,expenditure.getPaymentCredit(Boolean.TRUE).getAdjustment()
				});
		}
	}
	
	private Boolean isHasBeenEdited(Expenditure expenditure) {
		Object[] initials = initialValues.get(expenditure.getIdentifier());
		if(initials == null)
			return Boolean.FALSE;
		if(isHasBeenEdited(expenditure,FieldHelper.join(Expenditure.FIELD_ENTRY_AUTHORIZATION,EntryAuthorization.FIELD_ADJUSTMENT),0))
			return Boolean.TRUE;
		if(isHasBeenEdited(expenditure,FieldHelper.join(Expenditure.FIELD_PAYMENT_CREDIT,EntryAuthorization.FIELD_ADJUSTMENT),1))
			return Boolean.TRUE;
		return  Boolean.FALSE;
	}

	private Boolean isHasBeenEdited(Expenditure expenditure,String fieldName,Integer initialValueIndex) {
		Object[] initials = initialValues.get(expenditure.getIdentifier());
		Long initialValue = (Long) initials[initialValueIndex];
		Long current = (Long) FieldHelper.read(expenditure, fieldName);
		if(initialValue == null)
			return current != null;
		else
			return current == null || !initialValue.equals(current);
	}
	
	/**/
	
	public static final String FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT_INPUT = "entryAuthorizationAdjustmentInput";
	public static final String FIELD_PAYMENT_CREDIT_ADJUSTMENT_INPUT = "paymentCreditAdjustmentInput";
	
	public static final String OUTCOME = "expenditureAdjustView";
}