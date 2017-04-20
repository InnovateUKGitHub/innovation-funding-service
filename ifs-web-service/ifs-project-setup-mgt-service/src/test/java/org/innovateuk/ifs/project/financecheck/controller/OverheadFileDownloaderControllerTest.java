package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.financechecks.controller.OverheadFileDownloaderController;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

public class OverheadFileDownloaderControllerTest extends BaseControllerMockMVCTest<OverheadFileDownloaderController>{

    @Mock
    private OverheadFileRestService overheadFileRestServiceMock;

    @Test
    public void testDownloadJesFileSuccess() throws Exception {
        final String fileName = "overhead-spread-sheet";
        final String extension = ".xlsx";
        Long projectId = 1L;
        Long overheadId = 1L;

        MultipartFile file = new MockMultipartFile(fileName+extension, fileName.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource().withId(overheadId).withFilesizeBytes(file.getSize()).build();
        given(overheadFileRestServiceMock.getOverheadFile(
                overheadId)).willReturn(RestResult.restSuccess(new ByteArrayResource(file.getBytes())));
        given(overheadFileRestServiceMock.getOverheadFileDetails(
                overheadId)).willReturn(RestResult.restSuccess(fileEntryResource));

        MvcResult result = mockMvc.perform(get("/download/overheadfile/" + overheadId))
                .andExpect(status().isOk())
                .andReturn();
        verify(overheadFileRestServiceMock).getOverheadFileDetails(projectId);
        assertThat(fileName).isEqualTo(result.getResponse().getContentAsString());
    }

    @Override
    protected OverheadFileDownloaderController supplyControllerUnderTest() {
        return new OverheadFileDownloaderController();
    }
}