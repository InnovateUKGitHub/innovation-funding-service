package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseFileEntryResourceBuilder.newFormInputResponseFileEntryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationDownloadControllerTest extends AbstractApplicationMockMVCTest<ApplicationDownloadController> {

    private ArgumentCaptor<Long> fileProcessRoleArgumentCaptor = ArgumentCaptor.forClass(Long.class);

    private ArgumentCaptor<Long> fileDetailsProcessRoleArgumentCaptor = ArgumentCaptor.forClass(Long.class);

    @Override
    protected ApplicationDownloadController supplyControllerUnderTest() {
        return new ApplicationDownloadController();
    }

    @Before
    public void setUpData() {
        UserResource userResource = newUserResource().withRoleGlobal(Role.SUPPORTER).build();
        setLoggedInUser(userResource);
        this.setupCompetition(FundingType.KTP, AssessorFinanceView.ALL);
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
    }

    @Test
    public void downloadApplicationFinanceFileAsSupporter() throws Exception {
        Long questionId = 1L;
        Long formInputId = 1L;
        Long fileEntryId = 1L;
        Long leadApplicantProcessRoleId = 2L;
        String fileName = "finance-file.pdf";


        ApplicationResource app = applications.get(0);
        ProcessRoleResource processRoleResource = newProcessRoleResource()
                .withId(leadApplicantProcessRoleId)
                .withRole(Role.LEADAPPLICANT).build();
        MultipartFile file = new MockMultipartFile(fileName, fileName.getBytes());
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource().withMediaType("application/pdf").build();
        FormInputResponseFileEntryResource formInputResponseFileEntryResource = newFormInputResponseFileEntryResource()
                .withFileEntryResource(fileEntryResource)
                .build();

        when(processRoleRestService.findProcessRole(anyLong())).thenReturn(restSuccess(Collections.singletonList(processRoleResource)));
        when(formInputResponseRestService.getFile(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(restSuccess(byteArrayResource));
        when(formInputResponseRestService.getFileDetails(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(restSuccess(formInputResponseFileEntryResource));

        mockMvc.perform(get("/application/" + app.getId() + "/form/question/" + questionId + "/forminput/"
                + formInputId + "/file/" + fileEntryId + "/download"))
                .andExpect(status().isOk());

        verify(formInputResponseRestService).getFile(anyLong(), anyLong(), fileProcessRoleArgumentCaptor.capture(), anyLong());
        Long impersonatedFileProcessRoleId = fileProcessRoleArgumentCaptor.getValue();
        assertEquals(leadApplicantProcessRoleId, impersonatedFileProcessRoleId);

        verify(formInputResponseRestService).getFileDetails(anyLong(), anyLong(), fileDetailsProcessRoleArgumentCaptor.capture(), anyLong());
        Long impersonatedFileDetailsProcessRoleId = fileDetailsProcessRoleArgumentCaptor.getValue();
        assertEquals(leadApplicantProcessRoleId, impersonatedFileDetailsProcessRoleId);
    }
}
