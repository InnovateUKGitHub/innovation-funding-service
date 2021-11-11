package org.innovateuk.ifs.assessment.profile.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.profile.form.AssessorProfileSkillsForm;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileEditSkillsModelPopulator;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileEditSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:application.properties", "classpath:ifs-web-core.properties" } )
public class AssessorProfileSkillsEditControllerTest extends BaseControllerMockMVCTest<AssessorProfileSkillsEditController> {

    @Spy
    @InjectMocks
    private AssessorProfileEditSkillsModelPopulator assessorProfileEditSkillsModelPopulator;

    @Mock
    private ProfileRestService profileRestService;

    @Override
    protected AssessorProfileSkillsEditController supplyControllerUnderTest() {
        return new AssessorProfileSkillsEditController();
    }

    @Test
    public void getSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = "skill1 skill2 skill3";

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(businessType, skillAreas, innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling", asList("Data", "Cyber Security"));

        AssessorProfileEditSkillsViewModel expectedModel = new AssessorProfileEditSkillsViewModel(expectedInnovationAreas);

        AssessorProfileSkillsForm expectedForm = new AssessorProfileSkillsForm();
        expectedForm.setAssessorType(businessType);
        expectedForm.setSkillAreas(skillAreas);

        mockMvc.perform(get("/profile/skills/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/skills-edit"));

        verify(profileRestService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withBusinessType(businessType)
                .withSkillsAreas(skillAreas)
                .build();

        when(profileRestService.updateProfileSkills(1L, profileSkillsEditResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/skills"));

        verify(profileRestService, only()).updateProfileSkills(1L, profileSkillsEditResource);
    }

    @Test
    public void submitSkills_exceedsCharacterSizeLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = RandomStringUtils.random(5001);

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling", asList("Data", "Cyber Security"));

        AssessorProfileEditSkillsViewModel expectedModel = new AssessorProfileEditSkillsViewModel(expectedInnovationAreas);

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
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

        verify(profileRestService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills_exceedsWordLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(101, "skill"));

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling", asList("Data", "Cyber Security"));

        AssessorProfileEditSkillsViewModel expectedModel = new AssessorProfileEditSkillsViewModel(expectedInnovationAreas);

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
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

        verify(profileRestService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills_incomplete() throws Exception {
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling", asList("Data", "Cyber Security"));

        AssessorProfileEditSkillsViewModel expectedModel = new AssessorProfileEditSkillsViewModel(expectedInnovationAreas);

        MvcResult result = mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("skillAreas", skillAreas))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessorType"))
                .andExpect(view().name("profile/skills-edit"))
                .andReturn();

        AssessorProfileSkillsForm form = (AssessorProfileSkillsForm) result.getModelAndView().getModel().get("form");
        assertNull(form.getAssessorType());
        assertEquals(skillAreas, form.getSkillAreas());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("assessorType"));
        assertEquals("Please select an assessor type.", bindingResult.getFieldError("assessorType").getDefaultMessage());

        verify(profileRestService, only()).getProfileSkills(userResource.getId());
    }

    private List<InnovationAreaResource> setUpInnovationAreasForSector(String sectorName, String... innovationAreaNames) {
        return newInnovationAreaResource()
                .withSectorName(sectorName)
                .withName(innovationAreaNames)
                .build(innovationAreaNames.length);
    }

    private UserResource setUpProfileSkills(BusinessType businessType, String skillAreas, List<InnovationAreaResource> innovationAreaResources) {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(profileRestService.getProfileSkills(user.getId())).thenReturn(restSuccess(newProfileSkillsResource()
                .withUser(user.getId())
                .withInnovationAreas(innovationAreaResources)
                .withBusinessType(businessType)
                .withSkillsAreas(skillAreas)
                .build()));

        return user;
    }
}
