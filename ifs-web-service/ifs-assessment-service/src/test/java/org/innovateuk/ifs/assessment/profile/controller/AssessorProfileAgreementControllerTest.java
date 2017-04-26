package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileAgreementModelPopulator;
import org.innovateuk.ifs.assessment.profile.controller.AssessorProfileAgreementController;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileAgreementViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorProfileAgreementControllerTest extends BaseControllerMockMVCTest<AssessorProfileAgreementController> {

    @Spy
    @InjectMocks
    private AssessorProfileAgreementModelPopulator assessorProfileAgreementModelPopulator;

    @Override
    protected AssessorProfileAgreementController supplyControllerUnderTest() {
        return new AssessorProfileAgreementController();
    }

    @Test
    public void getAgreement() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.now();
        String expectedText = "Agreement text...";

        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .withCurrentAgreement(true)
                .withAgreement(newAgreementResource()
                        .withText(expectedText)
                        .build())
                .build();

        when(profileRestService.getProfileAgreement(user.getId())).thenReturn(restSuccess(profileAgreementResource));

        AssessorProfileAgreementViewModel expectedViewModel = new AssessorProfileAgreementViewModel();
        expectedViewModel.setCurrentAgreement(true);
        expectedViewModel.setAgreementSignedDate(expectedAgreementSignedDate);
        expectedViewModel.setText(expectedText);

        mockMvc.perform(get("/profile/agreement"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("profile/agreement"));

        verify(profileRestService, only()).getProfileAgreement(user.getId());
    }

    @Test
    public void submitAgreement() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(profileRestService.updateProfileAgreement(user.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/profile/agreement")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("agreesToTerms", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(profileRestService, only()).updateProfileAgreement(user.getId());
    }
}
