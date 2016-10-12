package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.AssessorRegistrationSkillsForm;
import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ProfileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
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
public class AssessorProfileSkillsControllerTest extends BaseControllerMockMVCTest<AssessorProfileSkillsController> {

    @Override
    protected AssessorProfileSkillsController supplyControllerUnderTest() {
        return new AssessorProfileSkillsController();
    }

    @Test
    public void getSkills() throws Exception {
        mockMvc.perform(get("/profile/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/innovation-areas"));
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

        when(userService.updateProfile(1L, profile)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas)
                .param("assessorType", businessType.name()))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(userService).updateProfile(1L, profile);
    }

    @Test
    public void submitProfileSkills_incomplete() throws Exception {
        String skillAreas = "skill1 skill2 skill3";

        MvcResult result = mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessorType"))
                .andExpect(view().name("profile/innovation-areas"))
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
}
