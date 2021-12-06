package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupDocumentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupCompetitionDocumentControllerTest extends BaseControllerMockMVCTest<CompetitionSetupDocumentController> {

    @Mock
    private CompetitionSetupDocumentService competitionSetupDocumentServiceMock;

    @Override
    protected CompetitionSetupDocumentController supplyControllerUnderTest() {
        return new CompetitionSetupDocumentController();
    }

    @Test
    public void save() throws Exception {

        CompetitionDocumentResource competitionDocumentResource = newCompetitionDocumentResource()
                .withTitle("Title")
                .withGuidance("Guidance")
                .build();

        when(competitionSetupDocumentServiceMock.save(competitionDocumentResource)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(post("/competition/setup/project-document/save")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResource)));

        verify(competitionSetupDocumentServiceMock, only()).save(competitionDocumentResource);
    }

    @Test
    public void saveAll() throws Exception {

        List<CompetitionDocumentResource> competitionDocumentResources = newCompetitionDocumentResource()
                .withTitle("Title")
                .withGuidance("Guidance")
                .build(2);

        when(competitionSetupDocumentServiceMock.saveAll(competitionDocumentResources)).thenReturn(serviceSuccess(competitionDocumentResources));

        mockMvc.perform(post("/competition/setup/project-document/save-all")
                .contentType(APPLICATION_JSON)
                .content(toJson(competitionDocumentResources)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResources)));

        verify(competitionSetupDocumentServiceMock, only()).saveAll(competitionDocumentResources);
    }

    @Test
    public void findOne() throws Exception {

        long projectDocumentId = 1L;

        CompetitionDocumentResource competitionDocumentResource = newCompetitionDocumentResource().build();

        when(competitionSetupDocumentServiceMock.findOne(projectDocumentId)).thenReturn(serviceSuccess(competitionDocumentResource));

        mockMvc.perform(get("/competition/setup/project-document/{projectDocumentId}", projectDocumentId)
                       )
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
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionDocumentResources)));

        verify(competitionSetupDocumentServiceMock, only()).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() throws Exception {

        long projectDocumentId = 1L;
        when(competitionSetupDocumentServiceMock.delete(projectDocumentId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.delete("/competition/setup/project-document/{projectDocumentId}", projectDocumentId))
                .andExpect(status().isOk());

        verify(competitionSetupDocumentServiceMock, only()).delete(projectDocumentId);
    }
}
