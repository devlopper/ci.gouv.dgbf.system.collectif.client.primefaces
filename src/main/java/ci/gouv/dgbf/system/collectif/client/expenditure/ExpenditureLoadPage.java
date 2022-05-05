package ci.gouv.dgbf.system.collectif.client.expenditure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.FileUpload;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.file.excel.Sheet;
import org.cyk.utility.file.excel.SheetGetter;
import org.cyk.utility.file.excel.SheetReader;
import org.cyk.utility.file.excel.WorkBook;
import org.cyk.utility.file.excel.WorkBookGetter;
import org.cyk.utility.rest.ResponseHelper;
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
	
	@Inject WorkBookGetter workBookGetter;
	@Inject SheetGetter sheetGetter;
	@Inject SheetReader sheetReader;
	
	@Inject ExpenditureController controller;
	@Inject LegislativeActVersionController legislativeActVersionController;
	
	private Layout layout;
	private Collection<Expenditure> expenditures = new ArrayList<>();
	private DataTable dataTable;
	
	private Collection<String> undefinedActivitiesCodesIdentifiers,undefinedEconomicsNaturesCodesIdentifiers,undefinedFundingsSourcesCodesIdentifiers,undefinedLessorsCodesIdentifiers
	,duplicatesIdentifiers,unknownActivitiesCodes,unknownEconomicsNaturesCodes,unknownFundingsSourcesCodes,unknownLessorsCodes;
	
	@Override
	protected void __listenAfterPostConstruct__() {
		super.__listenAfterPostConstruct__();
		legislativeActVersion = legislativeActVersionController.getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.LEGISLATIVE_ACT_VERSION_IDENTIFIER));
		buildLayout();
	}
	
	private void buildLayout() {
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		FileUpload fileUpload = FileUpload.build(FileUpload.FIELD_AUTO,Boolean.TRUE,FileUpload.FIELD_ALLOW_TYPES,"/(\\.|\\/)(xlsx)$/");
		fileUpload.setEventScript(Event.START, "alert('Uploading...');");
		fileUpload.setListener(new FileUpload.Listener.AbstractImpl() {
			@Override
			protected void listenFileUploadedNotEmpty(FileUploadEvent event, byte[] bytes) {
				WorkBook workBook = workBookGetter.get(bytes);
				Sheet sheet = sheetGetter.get(workBook, 0);
				String[][] arrays = sheetReader.read(sheet,null,null,1,null);
				if(arrays != null && arrays.length > 0) {
					for(Integer index = 0; index < arrays.length; index = index + 1) {
						String[] array = arrays[index];
						expenditures.add(new Expenditure().setIdentifier(String.valueOf(index+1)).setActivityCode(array[0]).setEconomicNatureCode(array[1]).setFundingSourceCode(array[2]).setLessorCode(array[3])
								.setEntryAuthorizationAdjustment(NumberHelper.getLong(array[4])).setPaymentCreditAdjustment(NumberHelper.getLong(array[5])));
					}
				}
				verify();
				dataTable.setValue(expenditures);
				fileUpload.setValue(null);
				PrimeFaces.current().ajax().update(ComponentHelper.GLOBAL_MESSAGES_TARGET_INLINE_CLIENT_IDENTIFIER,":form:"+fileUpload.getIdentifier(),":form:"+dataTable.getIdentifier());
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,fileUpload,Cell.FIELD_WIDTH,12));
		
		CommandButton commandButton = CommandButton.build(CommandButton.FIELD_VALUE,"Vérifier",CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES,List.of(RenderType.GROWL)
				,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				verify();
				return null;
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,commandButton,Cell.FIELD_WIDTH,12));
		
		dataTable = DataTable.build(DataTable.FIELD_VALUE,expenditures,DataTable.FIELD_LAZY,Boolean.FALSE,DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES
				,List.of(Expenditure.FIELD_ACTIVITY_CODE,Expenditure.FIELD_ECONOMIC_NATURE_CODE,Expenditure.FIELD_FUNDING_SOURCE_CODE,Expenditure.FIELD_LESSOR_CODE
						,Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT,Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT)
				,DataTable.FIELD_RENDER_TYPE,DataTable.RenderType.OUTPUT_UNSELECTABLE,DataTable.FIELD_LISTENER,new DataTableListenerImpl());
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable,Cell.FIELD_WIDTH,12));
		
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	private void verify() {
		Response response = controller.verifyLoadable(legislativeActVersion,expenditures);
		String message = ResponseHelper.getEntity(String.class, response);
		Severity severity = null;
		if(ResponseHelper.hasHeaderAny(response,ExpenditureService.HEADER_DUPLICATES_IDENTIFIERS,ExpenditureService.HEADER_UNDEFINED_ACTIVITIES_CODES_IDENTIFIERS,ExpenditureService.HEADER_UNDEFINED_ECONOMICS_NATURES_CODES_IDENTIFIERS
				,ExpenditureService.HEADER_UNDEFINED_FUNDINGS_SOURCES_CODES_IDENTIFIERS,ExpenditureService.HEADER_UNDEFINED_LESSORS_CODES_IDENTIFIERS
				,ExpenditureService.HEADER_UNKNOWN_ACTIVITIES_CODES,ExpenditureService.HEADER_UNKNOWN_ECONOMICS_NATURES_CODES,ExpenditureService.HEADER_UNKNOWN_FUNDINGS_SOURCES_CODES,ExpenditureService.HEADER_UNKNOWN_LESSORS_CODES)) {
			severity = Severity.WARNING;
			duplicatesIdentifiers = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_DUPLICATES_IDENTIFIERS),","));
			
			undefinedActivitiesCodesIdentifiers = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNDEFINED_ACTIVITIES_CODES_IDENTIFIERS),","));
			undefinedEconomicsNaturesCodesIdentifiers = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNDEFINED_ECONOMICS_NATURES_CODES_IDENTIFIERS),","));
			undefinedFundingsSourcesCodesIdentifiers = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNDEFINED_FUNDINGS_SOURCES_CODES_IDENTIFIERS),","));
			undefinedLessorsCodesIdentifiers = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNDEFINED_LESSORS_CODES_IDENTIFIERS),","));
			
			unknownActivitiesCodes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNKNOWN_ACTIVITIES_CODES),","));
			unknownEconomicsNaturesCodes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNKNOWN_ECONOMICS_NATURES_CODES),","));
			unknownFundingsSourcesCodes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNKNOWN_FUNDINGS_SOURCES_CODES),","));
			unknownLessorsCodes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(response.getHeaderString(ExpenditureService.HEADER_UNKNOWN_LESSORS_CODES),","));
		}else
			severity = Severity.INFORMATION;
		__inject__(MessageRenderer.class).render(message,severity, RenderType.INLINE);
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
			}else if(Expenditure.FIELD_ECONOMIC_NATURE_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Nature économique");
			}else if(Expenditure.FIELD_FUNDING_SOURCE_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Source de financement");
			}else if(Expenditure.FIELD_LESSOR_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Bailleur");
			}else if(Expenditure.FIELD_ENTRY_AUTHORIZATION_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Autorisation d'engagement");
			}else if(Expenditure.FIELD_PAYMENT_CREDIT_ADJUSTMENT.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Crédit de paiement");
			}
			return map;
		}
		
		@Override
		public String getStyleClassByRecord(Object record, Integer recordIndex) {
			if(record instanceof Expenditure && duplicatesIdentifiers != null && duplicatesIdentifiers.contains(((Expenditure)record).getIdentifier()))
				return "cyk-background-red";
			return super.getStyleClassByRecord(record, recordIndex);
		}
		
		@Override
		public String getStyleClassByRecordByColumn(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			if(isUndefinedCode(record, column, Expenditure.FIELD_ACTIVITY_CODE, undefinedActivitiesCodesIdentifiers) || isUndefinedCode(record, column, Expenditure.FIELD_ECONOMIC_NATURE_CODE, undefinedEconomicsNaturesCodesIdentifiers)
					|| isUndefinedCode(record, column, Expenditure.FIELD_FUNDING_SOURCE_CODE, undefinedFundingsSourcesCodesIdentifiers) || isUndefinedCode(record, column, Expenditure.FIELD_LESSOR_CODE, undefinedLessorsCodesIdentifiers))
				return "cyk-background-undefined-code";
			if(isUnknownCode(record, column, Expenditure.FIELD_ACTIVITY_CODE, unknownActivitiesCodes) || isUnknownCode(record, column, Expenditure.FIELD_ECONOMIC_NATURE_CODE, unknownEconomicsNaturesCodes)
					|| isUnknownCode(record, column, Expenditure.FIELD_FUNDING_SOURCE_CODE, unknownFundingsSourcesCodes) || isUnknownCode(record, column, Expenditure.FIELD_LESSOR_CODE, unknownLessorsCodes))
				return "cyk-background-unknown-code";
			return super.getStyleClassByRecordByColumn(record, recordIndex, column, columnIndex);
		}
		
		private Boolean isUndefinedCode(Object record, Column column,String fieldName,Collection<String> identifiers) {
			return record instanceof Expenditure && column != null && fieldName.equals(column.getFieldName()) && identifiers != null && identifiers.contains(FieldHelper.read(record,Expenditure.FIELD_IDENTIFIER));
		}
		
		private Boolean isUnknownCode(Object record, Column column,String fieldName,Collection<String> codes) {
			return record instanceof Expenditure && column != null && fieldName.equals(column.getFieldName()) && codes != null && codes.contains(FieldHelper.read(record,fieldName));
		}
	}
}