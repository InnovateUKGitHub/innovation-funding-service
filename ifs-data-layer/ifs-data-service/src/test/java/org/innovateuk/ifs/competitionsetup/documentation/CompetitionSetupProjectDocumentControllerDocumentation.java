package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupProjectDocumentController;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupProjectDocumentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.ProjectDocumentResourceBuilder.*;
import static org.innovateuk.ifs.documentation.ProjectDocumentResourceDocs.projectDocumentResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupProjectDocumentControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupProjectDocumentController> {

    @Mock
    private CompetitionSetupProjectDocumentService competitionSetupProjectDocumentServiceMock;

    @Override
    protected CompetitionSetupProjectDocumentController supplyControllerUnderTest() {
        return new CompetitionSetupProjectDocumentController();
    }

    @Test
    public void save() throws Exception {

        ProjectDocumentResource projectDocumentResource = newProjectDocumentResource()
                .withId(1L)
                .withCompetition(2L)
                .withTitle("Title")
                .withGuidance("Guidance")
                .withEditable(true)
                .withEnabled(true)
                .withFileType(singletonList(3L))
                .build();

        when(competitionSetupProjectDocumentServiceMock.save(projectDocumentResource)).thenReturn(serviceSuccess(projectDocumentResource));

        mockMvc.perform(post("/competition/setup/project-document/save")
                .contentType(APPLICATION_JSON)
                .content(toJson(projectDocumentResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectDocumentResource)))
                .andDo(document("competition/setup/project-document/{method-name}",
                        requestFields(projectDocumentResourceFields),
                        responseFields(projectDocumentResourceFields)
                ));

        verify(competitionSetupProjectDocumentServiceMock, only()).save(projectDocumentResource);
    }

    @Test
    public void saveAll() throws Exception {

        List<ProjectDocumentResource> projectDocumentResources = newProjectDocumentResource()
                .withId(1L)
                .withCompetition(12L)
                .withTitle("Title")
                .withGuidance("Guidance")
                .withEditable(true)
                .withEnabled(true)
                .withFileType(singletonList(3L))
                .build(2);

        when(competitionSetupProjectDocumentServiceMock.saveAll(projectDocumentResources)).thenReturn(serviceSuccess(projectDocumentResources));

        mockMvc.perform(post("/competition/setup/project-document/save-all")
                .contentType(APPLICATION_JSON)
                .content(toJson(projectDocumentResources)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectDocumentResources)))
                .andDo(document("competition/setup/project-document/{method-name}",
                        requestFields(
                                fieldWithPath("[]").description("List of Project Documents to save")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of Project Documents which were just saved to the database")
                        )
                ));

        verify(competitionSetupProjectDocumentServiceMock, only()).saveAll(projectDocumentResources);
    }

    @Test
    public void findOne() throws Exception {

        long projectDocumentId = 1L;

        ProjectDocumentResource projectDocumentResource = newProjectDocumentResource().build();

        when(competitionSetupProjectDocumentServiceMock.findOne(projectDocumentId)).thenReturn(serviceSuccess(projectDocumentResource));

        mockMvc.perform(get("/competition/setup/project-document/{projectDocumentId}", projectDocumentId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectDocumentResource)))
                .andDo(document("competition/setup/project-document/{method-name}",
                        pathParameters(
                                parameterWithName("projectDocumentId").description("Id of the Project Document to be retrieved")
                        ),
                        responseFields(projectDocumentResourceFields)
                ));

        verify(competitionSetupProjectDocumentServiceMock, only()).findOne(projectDocumentId);
    }

    @Test
    public void findByCompetitionId() throws Exception {

        long competitionId = 1L;

        List<ProjectDocumentResource> projectDocumentResources = newProjectDocumentResource().build(2);

        when(competitionSetupProjectDocumentServiceMock.findByCompetitionId(competitionId)).thenReturn(serviceSuccess(projectDocumentResources));

        mockMvc.perform(get("/competition/setup/project-document/find-by-competition-id/{competitionId}", competitionId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectDocumentResources)))
                .andDo(document("competition/setup/project-document/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("The competition id for which Project Documents need to be retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of Project Documents which were just saved to the database")
                        )
                ));

        verify(competitionSetupProjectDocumentServiceMock, only()).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() throws Exception {

        long projectDocumentId = 1L;
        when(competitionSetupProjectDocumentServiceMock.delete(projectDocumentId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/competition/setup/project-document/{projectDocumentId}", projectDocumentId))
                .andExpect(status().isOk())
                .andDo(document("competition/setup/project-document/{method-name}",
                        pathParameters(
                                parameterWithName("projectDocumentId").description("Id of the Project Document to be deleted")
                        )
                ));

        verify(competitionSetupProjectDocumentServiceMock, only()).delete(projectDocumentId);
    }
}

