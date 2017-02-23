package org.innovateuk.ifs.assessment.controller.profile;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.profile.AssessorProfileContractController.ContractAnnexParameter;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileContractForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileContractAnnexModelPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileContractModelPopulator;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileContractAnnexViewModel;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileContractViewModel;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.resource.ProfileContractResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.controller.profile.AssessorProfileContractController.ContractAnnexParameter.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.innovateuk.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asListOfPairs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorProfileContractControllerTest extends BaseControllerMockMVCTest<AssessorProfileContractController> {

    @Spy
    @InjectMocks
    private AssessorProfileContractModelPopulator assessorProfileContractModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileContractAnnexModelPopulator assessorProfileContractAnnexModelPopulator;

    @Override
    protected AssessorProfileContractController supplyControllerUnderTest() {
        return new AssessorProfileContractController();
    }

    @Test
    public void getContract() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        LocalDateTime expectedContractSignedDate = LocalDateTime.now();
        String expectedText = "Contract text...";

        ProfileContractResource profileContract = newProfileContractResource()
                .withContractSignedDate(expectedContractSignedDate)
                .withCurrentAgreement(true)
                .withContract(newContractResource()
                        .withText(expectedText)
                        .build())
                .build();

        when(userService.getProfileContract(user.getId())).thenReturn(profileContract);

        AssessorProfileContractViewModel expectedViewModel = new AssessorProfileContractViewModel();
        expectedViewModel.setCurrentAgreement(true);
        expectedViewModel.setContractSignedDate(expectedContractSignedDate);
        expectedViewModel.setText(expectedText);

        AssessorProfileContractForm expectedForm = new AssessorProfileContractForm();
        expectedForm.setAgreesToTerms(TRUE);

        mockMvc.perform(get("/profile/contract"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/contract"));

        verify(userService, only()).getProfileContract(user.getId());
    }

    @Test
    public void getAnnex() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        String expectedAnnexA = "Annex A...";
        String expectedAnnexB = "Annex B...";
        String expectedAnnexC = "Annex C...";

        ContractResource contract = newContractResource()
                .withAnnexA(expectedAnnexA)
                .withAnnexB(expectedAnnexB)
                .withAnnexC(expectedAnnexC)
                .build();

        when(contractService.getCurrentContract()).thenReturn(contract);

        // Check that each of the possible params returns the correct annex text
        List<Pair<ContractAnnexParameter, String>> params = asListOfPairs(A, expectedAnnexA, B, expectedAnnexB, C, expectedAnnexC);
        params.stream().forEach(paramAndExpected -> {
            try {
                assertProfileContractAnnexView(paramAndExpected.getLeft(), paramAndExpected.getRight());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        verify(contractService, times(params.size())).getCurrentContract();
    }

    @Test
    public void submitContract() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(userService.updateProfileContract(user.getId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/profile/contract")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("agreesToTerms", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(userService, only()).updateProfileContract(user.getId());
    }

    @Test
    public void submitContract_invalidForm() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        LocalDateTime expectedContractSignedDate = LocalDateTime.now();
        String expectedText = "Contract text...";

        ProfileContractResource profileContract = newProfileContractResource()
                .withContractSignedDate(expectedContractSignedDate)
                .withCurrentAgreement(true)
                .withContract(newContractResource()
                        .withText(expectedText)
                        .build())
                .build();

        when(userService.getProfileContract(user.getId())).thenReturn(profileContract);

        AssessorProfileContractViewModel expectedViewModel = new AssessorProfileContractViewModel();
        expectedViewModel.setCurrentAgreement(true);
        expectedViewModel.setContractSignedDate(expectedContractSignedDate);
        expectedViewModel.setText(expectedText);

        MvcResult result = mockMvc.perform(post("/profile/contract")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("agreesToTerms", "false"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeHasFieldErrors("form", "agreesToTerms"))
                .andExpect(view().name("profile/contract"))
                .andReturn();

        AssessorProfileContractForm form = (AssessorProfileContractForm) result.getModelAndView().getModel().get("form");

        assertEquals(FALSE, form.getAgreesToTerms());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("agreesToTerms"));
        assertEquals("Please agree to the terms and conditions.", bindingResult.getFieldError("agreesToTerms").getDefaultMessage());

        verify(userService, only()).getProfileContract(user.getId());
    }

    private void assertProfileContractAnnexView(ContractAnnexParameter annexParameter, String expectedText) throws Exception {
        AssessorProfileContractAnnexViewModel expectedViewModel = new AssessorProfileContractAnnexViewModel(annexParameter, expectedText);
        mockMvc.perform(get("/profile/contract/annex/{annex}", annexParameter))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("profile/annex"));
    }
}
