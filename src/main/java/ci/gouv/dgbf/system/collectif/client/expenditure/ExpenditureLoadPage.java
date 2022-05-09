package ci.gouv.dgbf.system.collectif.client.expenditure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.Event;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.FileUpload;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.file.excel.SheetGetter;
import org.cyk.utility.file.excel.SheetReader;
import org.cyk.utility.file.excel.WorkBookGetter;
import org.cyk.utility.rest.ResponseHelper;
import org.omnifaces.util.Ajax;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import ci.gouv.dgbf.system.collectif.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.collectif.server.api.service.ExpenditureService;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import ci.gouv.dgbf.system.collectif.server.client.rest.ExpenditureController;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersion;
import ci.gouv.dgbf.system.collectif.server.client.rest.LegislativeActVersionController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ExpenditureLoadPage extends AbstractPageContainerManagedImpl implements Serializable  {

	private LegislativeActVersion legislativeActVersion;
	
	@Inject SheetReader sheetReader;
	
	@Inject ExpenditureController controller;
	@Inject LegislativeActVersionController legislativeActVersionController;
	
	private Layout layout;
	private Collection<Expenditure> expenditures = new ArrayList<>();
	private DataTable dataTable;
	
	private ExpenditureService.LoadableVerificationResultDto loadableVerificationResult;
	
	@Override
	protected void __listenAfterPostConstruct__() {
		super.__listenAfterPostConstruct__();
		legislativeActVersion = legislativeActVersionController.getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER));
		buildLayout();
	}
	
	private void buildLayout() {
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		FileUpload fileUpload = FileUpload.build(FileUpload.FIELD_MODE,"advanced",FileUpload.FIELD_MULTIPLE,Boolean.FALSE,FileUpload.FIELD_FILE_LIMIT,1,FileUpload.FIELD_AUTO,Boolean.TRUE,FileUpload.FIELD_ALLOW_TYPES,"/(\\.|\\/)(xlsx)$/");
		fileUpload.setEventScript(Event.START, "PF('statusDialog').show();");
		fileUpload.setListener(new FileUpload.Listener.AbstractImpl() {
			@Override
			protected void listenFileUploadedNotEmpty(FileUploadEvent event, byte[] bytes) {
				String[][] arrays = sheetReader.read(new SheetReader.Arguments().setFromRowIndex(1).setSheetGetterArguments(new SheetGetter.Arguments().setWorkBookGetterArguments(new WorkBookGetter.Arguments().setBytes(bytes))));
				if(arrays != null && arrays.length > 0) {
					for(Integer index = 0; index < arrays.length; index = index + 1) {
						String[] array = arrays[index];
						Optional<Expenditure> expenditure = expenditures.stream().filter(i -> ((i.getActivityCode() == null && array[0] == null) || (i.getActivityCode() != null && array[0] != null && i.getActivityCode().equals(array[0]))) 
								&& ((i.getEconomicNatureCode() == null && array[1] == null) || (i.getEconomicNatureCode() != null && array[1] != null && i.getEconomicNatureCode().equals(array[1])))
								&& ((i.getFundingSourceCode() == null && array[2] == null) || (i.getFundingSourceCode() != null && array[2] != null && i.getFundingSourceCode().equals(array[2])))
								&& ((i.getLessorCode() == null && array[3] == null) || (i.getLessorCode() != null && array[3] != null && i.getLessorCode().equals(array[3])))
								).findFirst();
						if(expenditure.isPresent())
							continue;
						expenditures.add(new Expenditure().setIdentifier(String.valueOf(index+1)).setActivityCode(array[0]).setEconomicNatureCode(array[1]).setFundingSourceCode(array[2]).setLessorCode(array[3])
								.setEntryAuthorizationAdjustment(NumberHelper.getLong(array[4])).setPaymentCreditAdjustment(NumberHelper.getLong(array[5])));
					}
				}
				if(!Boolean.TRUE.equals(fileUpload.getMultiple())) {
					if(!CollectionHelper.isEmpty(expenditures))
						verify();
				}
				dataTable.setValue(expenditures);
				fileUpload.setValue(null);
				Ajax.oncomplete("PF('statusDialog').hide();");
				PrimeFaces.current().ajax().update(ComponentHelper.GLOBAL_MESSAGES_TARGET_INLINE_CLIENT_IDENTIFIER,":form:"+fileUpload.getIdentifier(),":form:"+dataTable.getIdentifier());
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,fileUpload,Cell.FIELD_WIDTH,12));
		
		dataTable = DataTable.build(DataTable.FIELD_VALUE,expenditures,DataTable.FIELD_LAZY,Boolean.FALSE,DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES
				,List.of(Expenditure.FIELD_ACTIVITY_CODE,Expenditure.FIELD_ECONOMIC_NATURE_CODE,Expenditure.FIELD_FUNDING_SOURCE_CODE,Expenditure.FIELD_LESSOR_CODE
						,Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT,Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT)
				,DataTable.FIELD_RENDER_TYPE,DataTable.RenderType.OUTPUT_UNSELECTABLE,DataTable.FIELD_LISTENER,new DataTableListenerImpl());
		
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD_VALUE,"Vérifier",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,MenuItem.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES,List.of(RenderType.INLINE,RenderType.GROWL)
				,MenuItem.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				verify();
				return null;
			}
		});
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD_VALUE,"Charger",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,MenuItem.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES,List.of(RenderType.INLINE,RenderType.GROWL)
				,MenuItem.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE
				,MenuItem.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				load();
				return null;
			}
		});
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable,Cell.FIELD_WIDTH,12));
		
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	private void verify() {
		Response response = controller.verifyLoadable(legislativeActVersion,expenditures);
		renderMessages(response);
	}
	
	private void load() {
		Response response = controller.load(legislativeActVersion,expenditures);
		renderMessages(response);
	}
	
	private void renderMessages(Response response) {
		loadableVerificationResult = ResponseHelper.getEntity(ExpenditureService.LoadableVerificationResultDto.class, response);
		Severity severity = null;
		if(loadableVerificationResult.hasWarnings())
			severity = Severity.WARNING;
		else
			severity = Severity.INFORMATION;
		__inject__(MessageRenderer.class).render(loadableVerificationResult.getMessage(),severity, RenderType.INLINE);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return String.format("Chargement d'ajustements de dépenses dans %s à partir de fichier",legislativeActVersion == null ? "???" : legislativeActVersion.getName());
	}
	
	@Getter @Setter @Accessors(chain=true)
	public class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			if(Expenditure.FIELD_ACTIVITY_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Activité");
				map.put(Column.FIELD_WIDTH, "90");
			}else if(Expenditure.FIELD_ECONOMIC_NATURE_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Nature économique");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(Expenditure.FIELD_FUNDING_SOURCE_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Source de financement");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(Expenditure.FIELD_LESSOR_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Bailleur");
				map.put(Column.FIELD_WIDTH, "75");
			}else if(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Ajustement autorisation d'engagement");
			}else if(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Ajustement crédit de paiement");
			}
			return map;
		}
		
		@Override
		public Object getCellValueByRecordByColumn(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			Object value = super.getCellValueByRecordByColumn(record, recordIndex, column, columnIndex);
			if(record instanceof Expenditure && column != null && loadableVerificationResult != null) {
				if(isUndefined(record, recordIndex, column, columnIndex))
					return formatCellValueByRecordByColumnUndefined(value);
				if(isUnknown(record, recordIndex, column, columnIndex))
					return formatCellValueByRecordByColumnUnknown(value);
				
				if(column.getFieldName().equals(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT) || column.getFieldName().equals(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT))
					if(isAvailableNotEnough(record, recordIndex, column, columnIndex))
						return formatCellValueByRecordByColumnAvailableNotEnough(record,value,column.getFieldName());
					else
						return formatCellValueByRecordByColumnAvailableEnough(record,value,column.getFieldName());
			}			
			return value;
		}
		
		@Override
		public String getStyleClassByRecord(Object record, Integer recordIndex) {
			if(record instanceof Expenditure && loadableVerificationResult != null && Boolean.TRUE.equals(loadableVerificationResult.containsDuplicates(((Expenditure)record).getIdentifier())))
				return "cyk-background-red";
			return super.getStyleClassByRecord(record, recordIndex);
		}
		
		@Override
		public String getStyleClassByRecordByColumn(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			if(loadableVerificationResult != null) {
				if(isUndefined(record, recordIndex, column, columnIndex))
					return "cyk-background-undefined";
				if(isUnknown(record, recordIndex, column, columnIndex))
					return "cyk-background-unknown";
				if(isAvailableNotEnough(record, recordIndex, column, columnIndex))
					return "cyk-background-available-not-enough";
			}
			return super.getStyleClassByRecordByColumn(record, recordIndex, column, columnIndex);
		}
		
		private Boolean isUndefined(Object record, Column column,String fieldName,Collection<String> identifiers) {
			return record instanceof Expenditure && column != null && fieldName.equals(column.getFieldName()) && identifiers != null && identifiers.contains(FieldHelper.read(record,Expenditure.FIELD_IDENTIFIER));
		}
		
		private Boolean isUnknown(Object record, Column column,String fieldName,Collection<String> codes) {
			return record instanceof Expenditure && column != null && fieldName.equals(column.getFieldName()) && codes != null && codes.contains(FieldHelper.read(record,fieldName));
		}
		
		private Boolean isAvailableNotEnough(Object record, Column column,String fieldName,Collection<String> identifiers) {
			return record instanceof Expenditure && column != null && fieldName.equals(column.getFieldName()) && identifiers != null && identifiers.contains(FieldHelper.read(record,Expenditure.FIELD_IDENTIFIER));
		}
		
		private Boolean isUndefined(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			return isUndefined(record, column, Expenditure.FIELD_ACTIVITY_CODE, loadableVerificationResult.getUndefinedActivities()) 
				|| isUndefined(record, column, Expenditure.FIELD_ECONOMIC_NATURE_CODE, loadableVerificationResult.getUndefinedEconomicsNatures())
				|| isUndefined(record, column, Expenditure.FIELD_FUNDING_SOURCE_CODE, loadableVerificationResult.getUndefinedFundingsSources()) 
				|| isUndefined(record, column, Expenditure.FIELD_LESSOR_CODE, loadableVerificationResult.getUndefinedLessors())
				|| isUndefined(record, column, Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT, loadableVerificationResult.getUndefinedEntriesAuthorizationsAdjustments())
				|| isUndefined(record, column, Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT, loadableVerificationResult.getUndefinedPaymentsCreditsAdjustments())
			;	
		}
		
		private Boolean isUnknown(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			return isUnknown(record, column, Expenditure.FIELD_ACTIVITY_CODE, loadableVerificationResult.getUnknownActivities()) 
				|| isUnknown(record, column, Expenditure.FIELD_ECONOMIC_NATURE_CODE, loadableVerificationResult.getUnknownEconomicsNatures())
				|| isUnknown(record, column, Expenditure.FIELD_FUNDING_SOURCE_CODE, loadableVerificationResult.getUnknownFundingsSources()) 
				|| isUnknown(record, column, Expenditure.FIELD_LESSOR_CODE, loadableVerificationResult.getUnknownLessors())
				|| isUnknown(record, column, Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT, loadableVerificationResult.getUnknownEntriesAuthorizationsAdjustments())
				|| isUnknown(record, column, Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT, loadableVerificationResult.getUnknownPaymentsCreditsAdjustments())
			;
		}
		
		private Boolean isAvailableNotEnough(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			return isAvailableNotEnough(record, column, Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT, loadableVerificationResult.getEntryAuthorizationAvailableIsNotEnough()) 
					|| isAvailableNotEnough(record, column, Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT, loadableVerificationResult.getPaymentCreditAvailableIsNotEnough())
					;
		}
		
		private Object formatCellValueByRecordByColumnUndefined(Object value) {
			return formatCellValueByRecordByColumn(value, "Non défini", "collectif-expenditure-load-warning-undefined");
		}
		
		private Object formatCellValueByRecordByColumnUnknown(Object value) {
			return formatCellValueByRecordByColumn(value, "Inconnu", "collectif-expenditure-load-warning-unknown");
		}
		
		private Object formatCellValueByRecordByColumnAvailableNotEnough(Object record,Object value,String fieldName) {
			return formatCellValueByRecordByColumnAvailable(record,value,fieldName,"Disponible insuffisant|");
		}
		
		private Object formatCellValueByRecordByColumnAvailableEnough(Object record,Object value,String fieldName) {
			return formatCellValueByRecordByColumnAvailable(record,value,fieldName,"");
		}
		
		@SuppressWarnings("unchecked")
		private Object formatCellValueByRecordByColumnAvailable(Object record,Object value,String fieldName,String prefix) {
			if(Boolean.TRUE.equals(loadableVerificationResult.hasUndefinedActivityOrEconomicNatureOrFundingSourceOrLessorByIdentifier(((Expenditure)record).getIdentifier())))
				return value;
			Long adjustment = MapHelper.readByKey((Map<String,Long>)FieldHelper.read(loadableVerificationResult, fieldName),((Expenditure)record).getIdentifier());
			if(adjustment == null)
				return value;
			Long available = MapHelper.readByKey((Map<String,Long>)FieldHelper.read(loadableVerificationResult, StringUtils.replace(fieldName, "Adjustment", "Available")),((Expenditure)record).getIdentifier());
			if(available == null)
				return value;
			return formatCellValueByRecordByColumn(value, String.format("%sA=%s|D=%s",prefix, adjustment, available), "collectif-expenditure-load-warning-available-not-enough");
		}
		
		private Object formatCellValueByRecordByColumn(Object value,String text,String styleClass) {
			return StringUtils.defaultString(value == null ? "" : value.toString()) + String.format("(%s)",text);
		}
	}
	
	public static final String OUTCOME = "expenditureLoadView";
}