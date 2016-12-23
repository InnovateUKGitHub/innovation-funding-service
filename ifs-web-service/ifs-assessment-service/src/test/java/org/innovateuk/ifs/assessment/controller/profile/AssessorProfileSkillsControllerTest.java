package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileSkillsForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
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

    @Spy
    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Override
    protected AssessorProfileSkillsController supplyControllerUnderTest() {
        return new AssessorProfileSkillsController();
    }

    @Test
    public void getReadonlySkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillsAreas = "skill1 skill2 skill3";

        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(userService.getProfileSkills(user.getId())).thenReturn(newProfileSkillsResource()
                .withUser(user.getId())
                .withBusinessType(businessType)
                .withSkillsAreas(skillsAreas)
                .build());

        AssessorProfileSkillsViewModel expectedModel = new AssessorProfileSkillsViewModel(skillsAreas, businessType);

        mockMvc.perform(get("/profile/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("profile/skills"));

        verify(userService).getProfileSkills(user.getId());
    }

    @Test
    public void getEditableSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillsAreas = "skill1 skill2 skill3";

        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(userService.getProfileSkills(user.getId())).thenReturn(newProfileSkillsResource()
                .withUser(user.getId())
                .withBusinessType(businessType)
                .withSkillsAreas(skillsAreas)
                .build());

        AssessorProfileSkillsForm expectedForm = new AssessorProfileSkillsForm();
        expectedForm.setAssessorType(businessType);
        expectedForm.setSkillAreas(skillsAreas);

        mockMvc.perform(get("/profile/skills/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/skills-edit"));

        verify(userService).getProfileSkills(user.getId());
    }

    @Test
    public void submitSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillsAreas = String.join(" ", nCopies(100, "skill"));

        when(userService.updateProfileSkills(1L, businessType, skillsAreas)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillsAreas))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(userService).updateProfileSkills(1L, businessType, skillsAreas);
    }

    @Test
    public void submitSkills_exceedsCharacterSizeLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = RandomStringUtils.random(5001);

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "skillAreas"))
                .andExpect(view().name("profile/skills-edit"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(businessType, form.getAssessorType());
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("skillAreas"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("skillAreas").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("skillAreas").getArguments()[1]);
    }

    @Test
    public void submitSkills_exceedsWordLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(101, "skill"));

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "skillAreas"))
                .andExpect(view().name("profile/skills-edit"))
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

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessorType"))
                .andExpect(view().name("profile/skills-edit"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("assessorType"));
        assertEquals("Please select an assessor type.", bindingResult.getFieldError("assessorType").getDefaultMessage());
    }
}
