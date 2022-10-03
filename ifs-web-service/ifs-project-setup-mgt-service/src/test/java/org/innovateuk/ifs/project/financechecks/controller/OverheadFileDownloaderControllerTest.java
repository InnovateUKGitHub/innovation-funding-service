package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.project.financechecks.controller.OverheadFileDownloaderController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
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
        given(overheadFileRestServiceMock.getOverheadFileUsingProjectFinanceRowId(
                overheadId)).willReturn(RestResult.restSuccess(new ByteArrayResource(file.getBytes())));
        given(overheadFileRestServiceMock.getOverheadFileDetailsUsingProjectFinanceRowId(
                overheadId)).willReturn(RestResult.restSuccess(fileEntryResource));

        MvcResult result = mockMvc.perform(get("/application/download/overheadfile/" + overheadId))
                .andExpect(status().isOk())
                .andReturn();
        verify(overheadFileRestServiceMock).getOverheadFileDetailsUsingProjectFinanceRowId(projectId);
        assertThat(fileName).isEqualTo(result.getResponse().getContentAsString());
    }

    @Override
    protected OverheadFileDownloaderController supplyControllerUnderTest() {
        return new OverheadFileDownloaderController();
    }
}