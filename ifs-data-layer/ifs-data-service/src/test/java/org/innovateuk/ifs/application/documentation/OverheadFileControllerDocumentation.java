package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.OverheadFileController;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.finance.transactional.OverheadFileService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.controller.OverheadFileControllerTest.OVERHEAD_BASE_URL;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OverheadFileControllerDocumentation extends BaseControllerMockMVCTest<OverheadFileController> {

    @Mock
    private OverheadFileService overheadFileServiceMock;

    @Mock
    private FileControllerUtils fileControllerUtils;

    @Override
    protected OverheadFileController supplyControllerUnderTest() {
        return new OverheadFileController();
    }

    @Test
    public void getProjectFileDetailsTest() throws Exception {
        Long overHeadIdSuccess = 123L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(overheadFileServiceMock.getProjectFileEntryDetails(overHeadIdSuccess)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/project-overhead-calculation-document-details?overheadId={overHeadIdSuccess}", overHeadIdSuccess)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)));
    }

    @Test
    public void getProjectFileContentsTest() throws Exception {
        Long overHeadIdSuccess = 123L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();
        FileAndContents successResult = new BasicFileAndContents(fileEntryResource, () -> mock(InputStream.class));
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity(successResult, HttpStatus.OK);

        when(fileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileServiceMock.getProjectFileEntryContents(overHeadIdSuccess)).thenReturn(serviceSuccess(successResult));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/project-overhead-calculation-document?overheadId={overHeadIdSuccess}", overHeadIdSuccess)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(successResult)));
    }
}