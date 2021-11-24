package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupDocumentController;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupDocumentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupCompetitionDocumentControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupDocumentController> {

    @Mock
    private CompetitionSetupDocumentService competitionSetupDocumentServiceMock;

    @Override
    protected CompetitionSetupDocumentController supplyControllerUnderTest() {
        return new CompetitionSetupDocumentController();
    }

    @Test
    public void save() throws Exception {

        CompetitionDocumentResource competitionDocumentResource = newCompetitionDocumentResource()
                .withId(1L)
                .withCompetition(2L)
                .withTitle("Title")
                .withGuidance("Guidance")
                .withEditable(true)
                .withEnabled(true)
                .withFileType(singletonList(3L))
                .build();

        when(competitionSetupDocumentServiceMock.save(competitionDocumentResource)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(post("/competition/setup/project-document/save")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResource)));

        verify(competitionSetupDocumentServiceMock, only()).save(competitionDocumentResource);
    }

    @Test
    public void saveAll() throws Exception {

        List<CompetitionDocumentResource> competitionDocumentResources = newCompetitionDocumentResource()
                .withId(1L)
                .withCompetition(12L)
                .withTitle("Title")
                .withGuidance("Guidance")
                .withEditable(true)
                .withEnabled(true)
                .withFileType(singletonList(3L))
                .build(2);

        when(competitionSetupDocumentServiceMock.saveAll(competitionDocumentResources)).thenReturn(serviceSuccess(competitionDocumentResources));

        mockMvc.perform(post("/competition/setup/project-document/save-all")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResources)))
                .andExpect(status().isOk());

        verify(competitionSetupDocumentServiceMock, only()).saveAll(competitionDocumentResources);
    }

    @Test
    public void findOne() throws Exception {

        long projectDocumentId = 1L;

        CompetitionDocumentResource competitionDocumentResource = newCompetitionDocumentResource().build();

        when(competitionSetupDocumentServiceMock.findOne(projectDocumentId)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(get("/competition/setup/project-document/{projectDocumentId}", projectDocumentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResource)));

        verify(competitionSetupDocumentServiceMock, only()).findOne(projectDocumentId);
    }

    @Test
    public void findByCompetitionId() throws Exception {

        long competitionId = 1L;

        List<CompetitionDocumentResource> competitionDocumentResources = newCompetitionDocumentResource().build(2);

        when(competitionSetupDocumentServiceMock.findByCompetitionId(competitionId)).thenReturn(serviceSuccess(competitionDocumentResources));

        mockMvc.perform(get("/competition/setup/project-document/find-by-competition-id/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResources)));

        verify(competitionSetupDocumentServiceMock, only()).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() throws Exception {

        long projectDocumentId = 1L;
        when(competitionSetupDocumentServiceMock.delete(projectDocumentId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/competition/setup/project-document/{projectDocumentId}", projectDocumentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(competitionSetupDocumentServiceMock, only()).delete(projectDocumentId);
    }
}

