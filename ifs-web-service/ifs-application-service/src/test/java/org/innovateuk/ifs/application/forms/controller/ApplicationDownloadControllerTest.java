package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationDownloadControllerTest extends AbstractApplicationMockMVCTest<ApplicationDownloadController> {

    @Mock
    private FinanceService financeService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Override
    protected ApplicationDownloadController supplyControllerUnderTest() {
        return new ApplicationDownloadController();
    }

    @Before
    public void setUpData() {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
    }

    @Test
    @Ignore
    public void downloadApplicationFinanceFile() throws Exception {
        Long questionId = 1L;
        Long formInputId = 1L;
        Long fileEntryId = 1L;
        String fileName = "finance-file.pdf";

        ApplicationResource app = applications.get(0);
        MultipartFile file = new MockMultipartFile(fileName, fileName.getBytes());
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());

        when(userRestService.findProcessRole(anyLong())).thenReturn(restSuccess(Collections.emptyList()));
        when(formInputResponseRestService.getFile(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(restSuccess(byteArrayResource));
        when(formInputResponseRestService.getFileDetails(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/application/" + app.getId() + "/form/question/" + questionId + "/forminput/"
                + formInputId + "/file/" + fileEntryId + "/download"))
                .andExpect(status().isOk());

        verify(formInputResponseRestService).getFile(anyLong(), anyLong(), anyLong(), anyLong());
        verify(formInputResponseRestService).getFileDetails(anyLong(), anyLong(), anyLong(), anyLong());
    }
}
