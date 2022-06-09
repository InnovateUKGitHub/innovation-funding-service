package org.innovateuk.ifs.form.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.form.controller.FormInputController;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.documentation.FormInputResourceDocs.formInputResourceBuilder;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FormInputControllerDocumentation extends BaseFileControllerMockMVCTest<FormInputController> {
    private static final String baseURI = "/forminput";

    @Mock
    private FormInputService formInputServiceMock;

    @Override
    protected FormInputController supplyControllerUnderTest() {
        return new FormInputController();
    }

    @Test
    public void documentFindById() throws Exception {
        FormInputResource testResource = formInputResourceBuilder.build();
        when(formInputServiceMock.findFormInput(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/{id}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void documentFindByQuestionId() throws Exception {
        List<FormInputResource> testResource = formInputResourceBuilder.build(1);
        when(formInputServiceMock.findByQuestionId(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/find-by-question-id/{id}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void documentFindByCompetitionId() throws Exception {
        List<FormInputResource> testResource = formInputResourceBuilder.build(1);
        when(formInputServiceMock.findByCompetitionId(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/find-by-competition-id/{id}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void documentDelete() throws Exception {
        when(formInputServiceMock.delete(1L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete(baseURI + "/{id}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }


    @Test
    public void findFile() throws Exception {
        final long formInputId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(formInputServiceMock.findFile(formInputId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get(baseURI + "/file-details/{formInputId}", formInputId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(formInputServiceMock).findFile(formInputId);
    }

    @Test
    public void downloadFile() throws Exception {
        final long formInputId = 22L;

        Function<FormInputService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> formInputServiceMock.downloadFile(formInputId);

        assertGetFileContents(baseURI + "/file/{formInputId}", new Object[]{formInputId},
                emptyMap(), formInputServiceMock, serviceCallToDownload)
                .andExpect(status().isOk());
    }

}
