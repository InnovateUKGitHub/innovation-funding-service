package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupProjectDocumentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupCompetitionDocumentControllerTest extends BaseControllerMockMVCTest<CompetitionSetupProjectDocumentController> {

    @Mock
    private CompetitionSetupProjectDocumentService competitionSetupProjectDocumentServiceMock;

    @Override
    protected CompetitionSetupProjectDocumentController supplyControllerUnderTest() {
        return new CompetitionSetupProjectDocumentController();
    }

    @Test
    public void save() throws Exception {

        CompetitionDocumentResource competitionDocumentResource = newProjectDocumentResource()
                .withTitle("Title")
                .withGuidance("Guidance")
                .build();

        when(competitionSetupProjectDocumentServiceMock.save(competitionDocumentResource)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(post("/competition/setup/project-document/save")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResource)));

        verify(competitionSetupProjectDocumentServiceMock, only()).save(competitionDocumentResource);
    }

    @Test
    public void saveAll() throws Exception {

        List<CompetitionDocumentResource> competitionDocumentResources = newProjectDocumentResource()
                .withTitle("Title")
                .withGuidance("Guidance")
                .build(2);

        when(competitionSetupProjectDocumentServiceMock.saveAll(competitionDocumentResources)).thenReturn(serviceSuccess(competitionDocumentResources));

        mockMvc.perform(post("/competition/setup/project-document/save-all")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResources)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResources)));

        verify(competitionSetupProjectDocumentServiceMock, only()).saveAll(competitionDocumentResources);
    }

    @Test
    public void findOne() throws Exception {

        long projectDocumentId = 1L;

        CompetitionDocumentResource competitionDocumentResource = newProjectDocumentResource().build();

        when(competitionSetupProjectDocumentServiceMock.findOne(projectDocumentId)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(get("/competition/setup/project-document/{projectDocumentId}", projectDocumentId)
                       )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResource)));

        verify(competitionSetupProjectDocumentServiceMock, only()).findOne(projectDocumentId);
    }

    @Test
    public void findByCompetitionId() throws Exception {

        long competitionId = 1L;

        List<CompetitionDocumentResource> competitionDocumentResources = newProjectDocumentResource().build(2);

        when(competitionSetupProjectDocumentServiceMock.findByCompetitionId(competitionId)).thenReturn(serviceSuccess(competitionDocumentResources));

        mockMvc.perform(get("/competition/setup/project-document/find-by-competition-id/{competitionId}", competitionId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResources)));

        verify(competitionSetupProjectDocumentServiceMock, only()).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() throws Exception {

        long projectDocumentId = 1L;
        when(competitionSetupProjectDocumentServiceMock.delete(projectDocumentId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/competition/setup/project-document/{projectDocumentId}", projectDocumentId))
                .andExpect(status().isOk());

        verify(competitionSetupProjectDocumentServiceMock, only()).delete(projectDocumentId);
    }
}
