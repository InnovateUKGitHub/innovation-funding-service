package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class ProjectGrantOfferLetterControllerTest extends BaseControllerMockMVCTest<ProjectGrantOfferLetterController> {

    @Test
    public void testViewGrantOfferLetterPageWithSignedOffer() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        FileEntryResource grantOfferLetter = newFileEntryResource().build();
        FileEntryResource signedGrantOfferLetter = newFileEntryResource().build();
        FileEntryResource additionalContractFile = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isUserLeadPartner(projectId, userId)).thenReturn(true);
        when(projectService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.of(signedGrantOfferLetter));
        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.of(grantOfferLetter));
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(additionalContractFile));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        ProjectGrantOfferLetterViewModel model = (ProjectGrantOfferLetterViewModel) result.getModelAndView().getModel().get("model");


        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertNull(model.getSubmitDate());
        assertTrue(model.isShowSubmitButton());
        assertNull(model.getSubmitDate());
        assertFalse(model.isSubmitted());
    }


    @Override
    protected ProjectGrantOfferLetterController supplyControllerUnderTest() {
        return new ProjectGrantOfferLetterController();
    }
}