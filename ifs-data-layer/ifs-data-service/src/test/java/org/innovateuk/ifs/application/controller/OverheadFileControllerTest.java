package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.finance.transactional.OverheadFileService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_EXCEPTION_WHILE_RETRIEVING_FILE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OverheadFileControllerTest extends BaseControllerMockMVCTest<OverheadFileController> {

    public static final String OVERHEAD_BASE_URL = "/overheadcalculation";

    private static final long maxFilesize = 1234L;
    private static final List<String> mediaTypes = singletonList("application/pdf");

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidatorMock;

    @Mock
    private OverheadFileService overheadFileService;

    @Mock
    private FileControllerUtils fileControllerUtils;

    @Override
    protected OverheadFileController supplyControllerUnderTest() {
        OverheadFileController controller = new OverheadFileController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForOverheadCalculation", maxFilesize);
        ReflectionTestUtils.setField(controller, "validMediaTypesForOverheadCalculation", mediaTypes);
        return controller;
    }

    @Test
    public void getFileDetailsTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(overheadFileService.getFileEntryDetails(overHeadIdSuccess)).thenReturn(serviceSuccess(fileEntryResource));
        when(overheadFileService.getFileEntryDetails(overHeadIdFailure)).thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/overheadCalculationDocumentDetails?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/overheadCalculationDocumentDetails?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is4xxClientError())
                .andExpect(contentError(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
    }

    @Test
    public void getProjectFileDetailsTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(overheadFileService.getProjectFileEntryDetails(overHeadIdSuccess)).thenReturn(serviceSuccess(fileEntryResource));
        when(overheadFileService.getProjectFileEntryDetails(overHeadIdFailure)).thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocumentDetails?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocumentDetails?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is4xxClientError())
                .andExpect(contentError(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
    }

    @Test
    public void getFileContentsTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();
        FileAndContents successResult = new BasicFileAndContents(fileEntryResource, () -> mock(InputStream.class));
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity(successResult, HttpStatus.OK);

        when(fileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileService.getFileEntryContents(overHeadIdSuccess)).thenReturn(serviceSuccess(successResult));


        mockMvc.perform(get(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(successResult)));

        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.addError(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST));
        objectResponseEntity = new ResponseEntity(new RestErrorResponse(new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE)), INTERNAL_SERVER_ERROR);

        when(fileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileService.getFileEntryContents(overHeadIdFailure)).thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is5xxServerError())
                .andExpect(contentError(new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void getProjectFileContentsTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();
        FileAndContents successResult = new BasicFileAndContents(fileEntryResource, () -> mock(InputStream.class));
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity(successResult, HttpStatus.OK);

        when(fileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileService.getProjectFileEntryContents(overHeadIdSuccess)).thenReturn(serviceSuccess(successResult));


        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(successResult)));

        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.addError(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST));
        objectResponseEntity = new ResponseEntity(new RestErrorResponse(new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE)), INTERNAL_SERVER_ERROR);

        when(fileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileService.getProjectFileEntryContents(overHeadIdFailure)).thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocument?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is5xxServerError())
                .andExpect(contentError(new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void createCalculationFileTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(fileControllerUtils.handleFileUpload(anyString(), anyString(), anyString(), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
                .thenReturn(RestResult.restSuccess(fileEntryResource, HttpStatus.OK));
        when(overheadFileService.createFileEntry(anyLong(), any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntryResource));


        mockMvc.perform(post(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess)
                .contentType("customType/type"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)));

        fileEntryResource = newFileEntryResource().withId(overHeadIdFailure).build();

        when(fileControllerUtils.handleFileUpload(anyString(), anyString(), anyString(), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
                .thenReturn(RestResult.restFailure(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));
        when(overheadFileService.createFileEntry(anyLong(), any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(post(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is5xxServerError())
                .andExpect(contentError(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void updateCalculationFileTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(fileControllerUtils.handleFileUpload(anyString(), anyString(), anyString(), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
                .thenReturn(RestResult.restSuccess(fileEntryResource, HttpStatus.OK));
        when(overheadFileService.createFileEntry(anyLong(), any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntryResource));


        mockMvc.perform(put(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess)
                .contentType("customType/type"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)));

        fileEntryResource = newFileEntryResource().withId(overHeadIdFailure).build();

        when(fileControllerUtils.handleFileUpload(anyString(), anyString(), anyString(), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
                .thenReturn(RestResult.restFailure(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));
        when(overheadFileService.createFileEntry(anyLong(), any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(put(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is5xxServerError())
                .andExpect(contentError(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void deleteCalculationFileTest() throws Exception {
        Long overHeadIdSuccess = 123L;
        Long overHeadIdFailure = 456L;

        when(overheadFileService.deleteFileEntry(overHeadIdSuccess)).thenReturn(serviceSuccess());
        when(overheadFileService.deleteFileEntry(overHeadIdFailure)).thenReturn(serviceFailure(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));


        mockMvc.perform(delete(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk());

        verify(overheadFileService, times(1)).deleteFileEntry(overHeadIdSuccess);

        mockMvc.perform(delete(OVERHEAD_BASE_URL + "/overheadCalculationDocument?overheadId={overHeadIdFailure}", overHeadIdFailure))
                .andExpect(status().is5xxServerError())
                .andExpect(contentError(new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR)));

        verify(overheadFileService, times(1)).deleteFileEntry(overHeadIdFailure);
    }
}
