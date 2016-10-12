package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.profile.AssessorProfileSkillsForm;
import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static java.util.Collections.nCopies;
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
        BusinessType businessType = BUSINESS;
        String skillsAreas = "skill1 skill2 skill3";

        UserResource user = newUserResource()
                .withProfile(newProfileResource()
                        .withBusinessType(businessType)
                        .withSkillsAreas(skillsAreas)
                        .build())
                .build();
        setLoggedInUser(user);

        AssessorProfileSkillsForm expectedForm = new AssessorProfileSkillsForm();
        expectedForm.setAssessorType(businessType);
        expectedForm.setSkillAreas(skillsAreas);

        mockMvc.perform(get("/profile/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/innovation-areas"));
    }

    @Test
    public void submitSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        ProfileResource profile = newProfileResource()
                .with(id(null))
                .withSkillsAreas(skillAreas)
                .withBusinessType(businessType)
                .build();

        when(userService.updateProfile(1L, profile)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(userService).updateProfile(1L, profile);
    }

    @Test
    public void submitSkills_exceedsCharacterSizeLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = RandomStringUtils.random(5001);

        MvcResult result = mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "skillAreas"))
                .andExpect(view().name("profile/innovation-areas"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(businessType, form.getAssessorType());
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("skillAreas"));
        assertEquals("This field cannot contain more than {1} characters", bindingResult.getFieldError("skillAreas").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("skillAreas").getArguments()[1]);
    }

    @Test
    public void submitSkills_exceedsWordLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(101, "skill"));

        MvcResult result = mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "skillAreas"))
                .andExpect(view().name("profile/innovation-areas"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(businessType, form.getAssessorType());
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("skillAreas"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("skillAreas").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("skillAreas").getArguments()[1]);
    }

    @Test
    public void submitSkills_incomplete() throws Exception {
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        MvcResult result = mockMvc.perform(post("/profile/skills")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessorType"))
                .andExpect(view().name("profile/innovation-areas"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("assessorType"));
        assertEquals("Please select an assessor type", bindingResult.getFieldError("assessorType").getDefaultMessage());
    }
}
