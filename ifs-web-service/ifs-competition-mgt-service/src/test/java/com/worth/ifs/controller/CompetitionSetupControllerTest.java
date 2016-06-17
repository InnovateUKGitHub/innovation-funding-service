
package com.worth.ifs.controller;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;

/**
 * Class for testing public functions of {@link CompetitionSetupController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupControllerTest {

    private static final Long COMPETITION_ID = Long.valueOf(12L);
    private static final String URL_PREFIX = "/competition/setup";

    @InjectMocks
	private CompetitionSetupController controller;
	
    @Mock
    private CompetitionService competitionService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(userService.findUserByType(UserRoleType.COMP_EXEC)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Exec").build()));

        when(userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Technologist").build()));


        CategoryResource c1 = new CategoryResource();
        c1.setType(CategoryType.INNOVATION_SECTOR);
        c1.setName("A Innovation Sector");
        c1.setId(1L);
        when(categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR)).thenReturn(asList(c1));


        CategoryResource c2 = new CategoryResource();
        c2.setType(CategoryType.INNOVATION_AREA);
        c2.setName("A Innovation Area");
        c2.setId(2L);
        c2.setParent(1L);
        when(categoryService.getCategoryByType(CategoryType.INNOVATION_AREA)).thenReturn(asList(c2));

        CompetitionTypeResource ct1 = new CompetitionTypeResource();
        ct1.setId(1L);
        ct1.setName("Comptype with stateAid");
        ct1.setStateAid(true);
        ct1.setCompetitions(asList(COMPETITION_ID));

        when(competitionService.getAllCompetitionTypes()).thenReturn(asList(ct1));

    }
    
    @Test
    public void initCompetitionSetupSection() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));
    }

    @Test
    public void editCompetitionSetupSectionInitial() throws Exception{

        CompetitionSetupInitialDetailsForm competitionSetupInitialDetailsForm = new CompetitionSetupInitialDetailsForm();
        competitionSetupInitialDetailsForm.setCompetitionCode("Code");
        competitionSetupInitialDetailsForm.setTitle("Test competition");
        competitionSetupInitialDetailsForm.setCompetitionTypeId(2L);

        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("competitionSetupForm", Matchers.hasProperty("competitionCode", Matchers.equalTo(competitionSetupInitialDetailsForm.getCompetitionCode()))))
                .andExpect(model().attribute("competitionSetupForm", Matchers.hasProperty("competitionTypeId", Matchers.equalTo(competitionSetupInitialDetailsForm.getCompetitionTypeId()))))
                .andExpect(model().attribute("competitionSetupForm", Matchers.hasProperty("title", Matchers.equalTo(competitionSetupInitialDetailsForm.getTitle()))))
                .andExpect(model().attribute("allSections", CompetitionSetupSection.values()));
    }

    @Test
    public void setSectionAsIncomplete() throws Exception {

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));
    }

    @Test
    public void getInnovationAreas() throws Exception {

        Long innovationSectorId = 1L;
        CategoryResource category = new CategoryResource();
        category.setType(CategoryType.INNOVATION_AREA);
        category.setId(1L);
        category.setName("Innovation Area 1");

        when(categoryService.getCategoryByParentId(innovationSectorId)).thenReturn(asList(category));

        mockMvc.perform(get(URL_PREFIX + "/getInnovationArea/" + innovationSectorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[0]name", is("Innovation Area 1")))
                .andExpect(jsonPath("[0]type", is(CategoryType.INNOVATION_AREA.toString())));

    }

    @Test
    public void generateCompetitionCode() throws Exception {
        LocalDateTime time = LocalDateTime.of(2016, 12, 1, 0, 0);

        when(competitionService.generateCompetitionCode(COMPETITION_ID, time)).thenReturn("1612-1");

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/generateCompetitionCode?day=01&month=12&year=2016"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("1612-1")));
    }

    @Test
    public void submitSectionInitialDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
        
        verify(competitionService, never()).update(competition);
    }
    
    @Test
    public void submitSectionInitialDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
        				.param("executiveUserId", "1")
        				.param("openingDateDay", "1")
        				.param("openingDateMonth", "1")
        				.param("openingDateYear", "2016")
        				.param("innovationSectorCategoryId", "1")
        				.param("innovationAreaCategoryId", "1")
        				.param("competitionTypeId", "1")
        				.param("leadTechnologistUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
        
        verify(competitionService).update(competition);
    }

}