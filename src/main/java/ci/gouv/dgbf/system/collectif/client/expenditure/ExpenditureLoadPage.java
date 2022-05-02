package ci.gouv.dgbf.system.collectif.client.expenditure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
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

import ci.gouv.dgbf.system.collectif.server.api.service.ExpenditureDto;
import ci.gouv.dgbf.system.collectif.server.api.service.ExpenditureService;
import ci.gouv.dgbf.system.collectif.server.client.rest.Expenditure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ExpenditureLoadPage extends AbstractPageContainerManagedImpl implements Serializable  {

	@Inject WorkBookGetter workBookGetter;
	@Inject SheetGetter sheetGetter;
	@Inject SheetReader sheetReader;
	
	private Layout layout;
	private Collection<Expenditure> expenditures = new ArrayList<>();
	private DataTable dataTable;
	
	@Override
	protected void __listenAfterPostConstruct__() {
		super.__listenAfterPostConstruct__();
		buildLayout();
	}
	
	private void buildLayout() {
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		FileUpload fileUpload = FileUpload.build(FileUpload.FIELD_AUTO,Boolean.TRUE,FileUpload.FIELD_ALLOW_TYPES,"/(\\.|\\/)(xlsx)$/");
		fileUpload.setListener(new FileUpload.Listener.AbstractImpl() {
			@Override
			protected void listenFileUploadedNotEmpty(FileUploadEvent event, byte[] bytes) {
				WorkBook workBook = workBookGetter.get(bytes);
				Sheet sheet = sheetGetter.get(workBook, 0);
				String[][] arrays = sheetReader.read(sheet,null,null,1,null);
				if(arrays != null && arrays.length > 0) {
					for(String[] array : arrays) {
						expenditures.add(new Expenditure().setActivityCode(array[0]).setEconomicNatureCode(array[1]).setFundingSourceCode(array[2]).setLessorCode(array[3]));
					}
				}
				dataTable.setValue(expenditures);
				fileUpload.setValue(null);
				PrimeFaces.current().ajax().update(":form:"+dataTable.getIdentifier(),":form:"+fileUpload.getIdentifier());
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,fileUpload,Cell.FIELD_WIDTH,12));
		
		CommandButton commandButton = CommandButton.build(CommandButton.FIELD_VALUE,"Vérifier",CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES,List.of(RenderType.GROWL)
				,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				if(CollectionHelper.isEmpty(expenditures))
					throw new RuntimeException("Aucune dépenses à vérifier");
				Response response = Expenditure.getService().verifyLoadable(expenditures.stream().map(expenditure -> new ExpenditureDto.LoadDto().setActivity(expenditure.getActivityCode()).setEconomicNature(expenditure.getEconomicNatureCode())
						.setFundingSource(expenditure.getFundingSourceCode()).setLessor(expenditure.getLessorCode())).collect(Collectors.toList()));
				if(response.getStatus() == Response.Status.OK.getStatusCode()) {
					if(CollectionUtils.containsAny(response.getHeaders().keySet(),List.of(ExpenditureService.HEADER_UNKNOWN_ACTIVITIES_CODES
							,ExpenditureService.HEADER_UNKNOWN_ECONOMICS_NATURES_CODES,ExpenditureService.HEADER_UNKNOWN_FUNDINGS_SOURCES_CODES,ExpenditureService.HEADER_UNKNOWN_LESSORS_CODES))) {
						String message = ResponseHelper.getEntity(String.class, response);
						__inject__(MessageRenderer.class).render(message,Severity.WARNING, RenderType.INLINE);
					}else {
						__inject__(MessageRenderer.class).render("",Severity.INFORMATION, RenderType.INLINE);
					}
				}
				
				return null;
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,commandButton,Cell.FIELD_WIDTH,12));
		
		dataTable = DataTable.build(DataTable.FIELD_VALUE,expenditures,DataTable.FIELD_LAZY,Boolean.FALSE,DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES
				,List.of(Expenditure.FIELD_ACTIVITY_CODE,Expenditure.FIELD_ECONOMIC_NATURE_CODE,Expenditure.FIELD_FUNDING_SOURCE_CODE,Expenditure.FIELD_LESSOR_CODE)
				,DataTable.FIELD_RENDER_TYPE,DataTable.RenderType.OUTPUT_UNSELECTABLE,DataTable.FIELD_LISTENER,new DataTableListenerImpl());
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,dataTable,Cell.FIELD_WIDTH,12));
		
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Chargement d'ajustements de dépenses par fichier";
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		
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
			}
			return map;
		}
	}
}