package org.innovateuk.ifs.application.overheads;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OverheadFileDownloadControllerTest extends BaseControllerMockMVCTest<OverheadFileDownloadController> {

    @Mock
    private OverheadFileRestService overheadFileRestServiceMock;

    @Test
    public void downloadSuccess() throws Exception {
        final String fileName = "overhead-spread-sheet";
        final String extension = ".xlsx";
        Long projectId = 1L;
        Long overheadId = 1L;

        MultipartFile file = new MockMultipartFile(fileName + extension, fileName.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource()
                .withId(overheadId)
                .withFilesizeBytes(file.getSize())
                .build();

        when(overheadFileRestServiceMock.getOverheadFile(overheadId))
                .thenReturn(RestResult.restSuccess(new ByteArrayResource(file.getBytes())));

        when(overheadFileRestServiceMock.getOverheadFileDetails(overheadId))
                .thenReturn(RestResult.restSuccess(fileEntryResource));

        MvcResult result = mockMvc.perform(get("/application/download/overheadfile/" + overheadId))
                .andExpect(status().isOk())
                .andReturn();
        verify(overheadFileRestServiceMock).getOverheadFileDetails(projectId);
        assertThat(fileName).isEqualTo(result.getResponse().getContentAsString());
    }

    @Override
    protected OverheadFileDownloadController supplyControllerUnderTest() {
        return new OverheadFileDownloadController();
    }

}
