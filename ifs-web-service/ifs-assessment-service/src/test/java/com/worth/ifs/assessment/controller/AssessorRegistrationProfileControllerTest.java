package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.form.AssessorRegistrationSkillsForm;
import com.worth.ifs.assessment.model.AssessorRegistrationDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationTermsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorRegistrationProfileControllerTest  extends BaseControllerMockMVCTest<AssessorRegistrationProfileController> {

    @Spy
    @InjectMocks
    private AssessorRegistrationSkillsModelPopulator assessorRegistrationSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationDeclarationModelPopulator assessorRegistrationDeclarationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationTermsModelPopulator assessorRegistrationTermsModelPopulator;

    @Override
    protected AssessorRegistrationProfileController supplyControllerUnderTest() {
        return new AssessorRegistrationProfileController();
    }

    @Test
    public void profileSkills() throws Exception {
        mockMvc.perform(get("/registration/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void submitProfileSkills() throws Exception {
        BusinessType businessType = BusinessType.BUSINESS;
        String skillAreas = "skill1 skill2 skill3";

        AssessorRegistrationSkillsForm expectedForm = new AssessorRegistrationSkillsForm();
        expectedForm.setSkillAreas(skillAreas);
        expectedForm.setAssessorType(businessType);

        ProfileResource profile = newProfileResource()
                .with(id(null))
                .withSkillsAreas(skillAreas)
                .withBusinessType(businessType)
                .build();

        UserResource user = newUserResource()
                .withProfile(profile)
                .build();

        when(userService.updateProfile(1L, profile)).thenReturn(serviceSuccess(user));

        mockMvc.perform(post("/registration/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas)
                .param("assessorType", businessType.name()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/declaration"));

        verify(userService).updateProfile(1L, profile);
    }

    @Test
    public void submitProfileSkills_incomplete() throws Exception {
        String skillAreas = "skill1 skill2 skill3";

        MvcResult result = mockMvc.perform(post("/registration/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessorType"))
                .andExpect(view().name("registration/innovation-areas"))
                .andReturn();


        AssessorRegistrationSkillsForm form = (AssessorRegistrationSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("assessorType"));
        assertEquals("Please select an assessor type", bindingResult.getFieldError("assessorType").getDefaultMessage());
    }

    @Test
    public void profileDeclaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void profileTerms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/terms"));
    }
}
