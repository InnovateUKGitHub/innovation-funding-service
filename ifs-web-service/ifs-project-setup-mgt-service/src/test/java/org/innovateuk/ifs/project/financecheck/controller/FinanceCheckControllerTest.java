package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.builder.FileEntryResourceBuilder;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.project.financecheck.form.FinanceCheckForm;
import org.innovateuk.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Test
    public void testDownloadJesFileWhenFinanceFileEntryNotPresent() throws Exception {

        Long projectId = 1L;
        Long applicationId = 1L;
        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .build();

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(applicationFinanceResource);

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId + "/jes-file"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());

    }

    @Test
    public void testDownloadJesFileSuccess() throws Exception {

        Long projectId = 1L;
        Long applicationId = 1L;
        Long organisationId = 1L;
        Long financeFileEntry = 1L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withFinanceFileEntry(financeFileEntry)
                .build();

        FileEntryResource jesFileEntryResource = FileEntryResourceBuilder.newFileEntryResource()
                .withName("jes-file.pdf")
                .build();
        byte[] content = "HelloWorld".getBytes();
        ByteArrayResource jesByteArrayResource = new ByteArrayResource(content);


        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(applicationFinanceResource);
        when(financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry())).thenReturn(RestResult.restSuccess(jesFileEntryResource));
        when(financeService.getFinanceDocumentByApplicationFinance(applicationFinanceResource.getId())).thenReturn(RestResult.restSuccess(jesByteArrayResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId + "/jes-file"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"jes-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());

    }

    private <T extends Enum> T getEnumForIndex(Class<T> enums, int index) {
        T[] enumConstants = enums.getEnumConstants();
        return enumConstants[index % enumConstants.length];
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
