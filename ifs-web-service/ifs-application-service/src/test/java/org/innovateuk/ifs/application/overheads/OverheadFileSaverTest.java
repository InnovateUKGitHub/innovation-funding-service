package org.innovateuk.ifs.application.overheads;

import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RunWith(MockitoJUnitRunner.class)
public class OverheadFileSaverTest {

    @Mock
    private OverheadFileRestService overheadFileRestService;

    @InjectMocks
    private OverheadFileSaver saver;

    @Before
    public void setup() {

    }

    @Test
    public void uploadOverheadFileTestWrongInput() {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest)setupUploadRequest();

        when(request.getFileMap()).thenReturn(asMap("overheadfile", null));
        when(request.getParameter("fileoverheadid")).thenReturn("ash!@#!@tasa");

        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(0, result.getErrors().size());
        verify(overheadFileRestService, never()).updateOverheadCalculationFile(anyLong(), anyString(), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void uploadOverheadFileTestSuccess() throws IOException {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest)setupUploadRequest();
        MultipartFile file = new MockMultipartFile("overheadfile.xlsx", new byte[]{});

        when(request.getFileMap()).thenReturn(asMap("overheadfile", file));
        when(request.getParameter("fileoverheadid")).thenReturn("343");

        FileEntryResource fileEntryResource = newFileEntryResource().withId(343L).withFilesizeBytes(file.getSize()).build();

        when(overheadFileRestService.updateOverheadCalculationFile(343L, file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes()))
                .thenReturn(RestResult.restSuccess(fileEntryResource));
        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(0, result.getErrors().size());
        verify(overheadFileRestService, times(1)).updateOverheadCalculationFile(anyLong(), anyString(), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void uploadOverheadFileTestFailure() throws IOException {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest)setupUploadRequest();
        MultipartFile file = new MockMultipartFile("overheadfile.xlsx", new byte[]{});

        when(request.getFileMap()).thenReturn(asMap("overheadfile", file));
        when(request.getParameter("fileoverheadid")).thenReturn("343");

        when(overheadFileRestService.updateOverheadCalculationFile(343L, file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes()))
                .thenReturn(RestResult.restFailure(new Error("GENERAL_NOT_FOUND", BAD_REQUEST)));
        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(1, result.getErrors().size());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
        assertEquals(NOT_ACCEPTABLE, result.getErrors().get(0).getStatusCode());
        verify(overheadFileRestService, times(1)).updateOverheadCalculationFile(anyLong(), anyString(), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void uploadOverheadFileTestFailureReplaceMessage() throws IOException {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest)setupUploadRequest();
        MultipartFile file = new MockMultipartFile("overheadfile.xlsx", new byte[]{});

        when(request.getFileMap()).thenReturn(asMap("overheadfile", file));
        when(request.getParameter("fileoverheadid")).thenReturn("343");

        when(overheadFileRestService.updateOverheadCalculationFile(343L, file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes()))
                .thenReturn(RestResult.restFailure(new Error("UNSUPPORTED_MEDIA_TYPE", UNSUPPORTED_MEDIA_TYPE)));
        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(1, result.getErrors().size());
        assertEquals("validation.finance.overhead.file.type", result.getErrors().get(0).getErrorKey());
        verify(overheadFileRestService, times(1)).updateOverheadCalculationFile(anyLong(), anyString(), anyLong(), anyString(), any(byte[].class));
    }


    @Test
    public void deleteOverheadFileWrongInput() {
        HttpServletRequest request = setupDeleteRequest();

        when(request.getParameter("fileoverheadid")).thenReturn("askjdjbkr@#!");

        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(0, result.getErrors().size());
        verify(overheadFileRestService, never()).removeOverheadCalculationFile(anyLong());
    }

    @Test
    public void deleteOverheadFileSuccess() {
        HttpServletRequest request = setupDeleteRequest();

        when(request.getParameter("fileoverheadid")).thenReturn("343");

        when(overheadFileRestService.removeOverheadCalculationFile(343L)).thenReturn(RestResult.restSuccess());
        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(0, result.getErrors().size());
        verify(overheadFileRestService, times(1)).removeOverheadCalculationFile(anyLong());
    }


    @Test
    public void deleteOverheadFileFailure() {
        HttpServletRequest request = setupDeleteRequest();

        when(request.getParameter("fileoverheadid")).thenReturn("343");

        when(overheadFileRestService.removeOverheadCalculationFile(343L)).thenReturn(RestResult.restFailure(new Error("GENERAL_NOT_FOUND", BAD_REQUEST)));
        ValidationMessages result = saver.handleOverheadFileRequest(request);

        assertEquals(1, result.getErrors().size());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
        assertEquals(NOT_ACCEPTABLE, result.getErrors().get(0).getStatusCode());
        verify(overheadFileRestService, times(1)).removeOverheadCalculationFile(anyLong());
    }

    private HttpServletRequest setupUploadRequest() {
        HttpServletRequest request = mock(StandardMultipartHttpServletRequest.class);
        when(request.getParameter("overheadfilesubmit")).thenReturn("true");

        return request;
    }

    private HttpServletRequest setupDeleteRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("overheadfiledelete")).thenReturn("true");

        return request;
    }
}
