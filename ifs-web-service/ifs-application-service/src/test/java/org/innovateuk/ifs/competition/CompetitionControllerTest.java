package org.innovateuk.ifs.competition;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();;
        loginUser(user);

        Long compId = 20L;

        CompetitionResource competition = newCompetitionResource().with(target -> setField("id", compId, target)).build();
        when(competitionService.getById(compId)).thenReturn(competition);

        mockMvc.perform(get("/competition/{id}/details/", compId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("userIsLoggedIn", true))
                .andExpect(view().name("competition/details"));
    }


    @Test
    public void testCompetitionDetailsWithInvalidAuthentication() throws Exception {
        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();;
        loginUser(user);

        Long compId = 20L;

        CompetitionResource competition = newCompetitionResource().with(target -> setField("id", compId, target)).build();
        when(competitionService.getById(compId)).thenReturn(competition);
        when(userAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(null);

        mockMvc.perform(get("/competition/{id}/details/", compId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("userIsLoggedIn", false))
                .andExpect(view().name("competition/details"));
    }


    @Test
    public void testCompetitionInfo() throws Exception {
        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();;
        loginUser(user);

        Long compId = 20L;
        String templateName = "a string";

        CompetitionResource competition = newCompetitionResource().with(target -> setField("id", compId, target)).build();
        when(competitionService.getById(compId)).thenReturn(competition);

        mockMvc.perform(get("/competition/{id}/info/{templateName}", compId, templateName))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("userIsLoggedIn", true))
                .andExpect(view().name("competition/info/" + templateName));
    }
}
