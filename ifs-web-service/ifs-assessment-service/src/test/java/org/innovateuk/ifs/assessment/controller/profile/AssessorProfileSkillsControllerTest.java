package org.innovateuk.ifs.assessment.controller.profile;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileSkillsForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileEditSkillsModelPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileEditSkillsViewModel;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorProfileSkillsControllerTest extends BaseControllerMockMVCTest<AssessorProfileSkillsController> {

    @Spy
    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileEditSkillsModelPopulator assessorProfileEditSkillsModelPopulator;

    @Override
    protected AssessorProfileSkillsController supplyControllerUnderTest() {
        return new AssessorProfileSkillsController();
    }

    @Test
    public void getReadonlySkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = "skill1 skill2 skill3";

        List<InnovationAreaResource> innovationAreaResources = CollectionFunctions.combineLists(
                setUpInnovationAreasForSector("Emerging and enabling technologies",
                        "Data", "Cyber Security"),
                setUpInnovationAreasForSector("Health and life sciences",
                        "Biosciences", "Independent living and wellbeing"));
        UserResource userResource = setUpProfileSkills(businessType, skillAreas, innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling technologies", asList("Data", "Cyber Security"));
        expectedInnovationAreas.put("Health and life sciences", asList("Biosciences", "Independent living and wellbeing"));

        AssessorProfileSkillsViewModel expectedModel = new AssessorProfileSkillsViewModel(expectedInnovationAreas,
                skillAreas, businessType);

        mockMvc.perform(get("/profile/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("profile/skills"));

        verify(userService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void getSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = "skill1 skill2 skill3";

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling technologies", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(businessType, skillAreas, innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling technologies", asList("Data", "Cyber Security"));

        AssessorProfileEditSkillsViewModel expectedModel = new AssessorProfileEditSkillsViewModel(expectedInnovationAreas);

        AssessorProfileSkillsForm expectedForm = new AssessorProfileSkillsForm();
        expectedForm.setAssessorType(businessType);
        expectedForm.setSkillAreas(skillAreas);

        mockMvc.perform(get("/profile/skills/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/skills-edit"));

        verify(userService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        when(userService.updateProfileSkills(1L, businessType, skillAreas)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/profile/skills/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessorType", businessType.name())
                .param("skillAreas", skillAreas))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/skills"));

        verify(userService, only()).updateProfileSkills(1L, businessType, skillAreas);
    }

    @Test
    public void submitSkills_exceedsCharacterSizeLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = RandomStringUtils.random(5001);

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling technologies", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling technologies", asList("Data", "Cyber Security"));

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

        verify(userService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills_exceedsWordLimit() throws Exception {
        BusinessType businessType = BUSINESS;
        String skillAreas = String.join(" ", nCopies(101, "skill"));

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling technologies", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling technologies", asList("Data", "Cyber Security"));

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

        verify(userService, only()).getProfileSkills(userResource.getId());
    }

    @Test
    public void submitSkills_incomplete() throws Exception {
        String skillAreas = String.join(" ", nCopies(100, "skill"));

        List<InnovationAreaResource> innovationAreaResources = setUpInnovationAreasForSector("Emerging and enabling technologies", "Data", "Cyber Security");
        UserResource userResource = setUpProfileSkills(ACADEMIC, "skill1 skill2 skill3", innovationAreaResources);

        Map<String, List<String>> expectedInnovationAreas = new LinkedHashMap<>();
        expectedInnovationAreas.put("Emerging and enabling technologies", asList("Data", "Cyber Security"));

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

        verify(userService, only()).getProfileSkills(userResource.getId());
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

        when(userService.getProfileSkills(user.getId())).thenReturn(newProfileSkillsResource()
                .withUser(user.getId())
                .withInnovationAreas(innovationAreaResources)
                .withBusinessType(businessType)
                .withSkillsAreas(skillAreas)
                .build());

        return user;
    }
}
