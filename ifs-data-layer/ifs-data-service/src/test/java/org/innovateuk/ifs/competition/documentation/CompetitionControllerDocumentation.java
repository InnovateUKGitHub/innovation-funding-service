package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.controller.CompetitionController;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.documentation.ApplicationDocs;
import org.innovateuk.ifs.documentation.CompetitionCountResourceDocs;
import org.innovateuk.ifs.documentation.CompetitionResourceDocs;
import org.innovateuk.ifs.documentation.CompetitionSearchResultDocs;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionController> {
    @Mock
    CompetitionService competitionService;
    @Mock
    CompetitionSetupService competitionSetupService;
    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Before
    public void setup() {
        this.document = document("competition/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findOne() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResourceBuilder.build()));

        mockMvc.perform(get("/competition/{id}", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to be retrieved")
                        ),
                        responseFields(competitionResourceFields)
                ));
    }

    @Test
    public void findAll() throws Exception {

        when(competitionService.findAll()).thenReturn(serviceSuccess(competitionResourceBuilder.build(2)));

        mockMvc.perform(get("/competition/findAll"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void getCompetitionsByUserId() throws Exception {
        final Long userId = 4929L;
        when(competitionService.getCompetitionsByUserId(userId))
                .thenReturn(serviceSuccess(competitionResourceBuilder.build(2)));

        mockMvc.perform(get("/competition/getCompetitionsByUserId/{userid}", userId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void markAsComplete() throws Exception {
        Long competitionId = 2L;
        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        when(competitionSetupService.markSectionComplete(competitionId, section)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/complete/{competitionId}/{section}", competitionId, section))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as complete"),
                                parameterWithName("section").description("the section to mark as complete")
                        )
                ));
    }

    @Test
    public void markAsInComplete() throws Exception {
        Long competitionId = 2L;
        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        when(competitionSetupService.markSectionInComplete(competitionId, section)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/incomplete/{competitionId}/{section}", competitionId, section))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as incomplete"),
                                parameterWithName("section").description("the section to mark as incomplete")
                        )
                ));
    }

    @Test
    public void live() throws Exception {
        when(competitionService.findLiveCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/live"))
            .andExpect(status().isOk())
            .andDo(document(
                    "competition/{method-name}",
                responseFields(
                        fieldWithPath("[]").description("list of live competitions the authenticated user has access to")
                )
            ));
    }

    @Test
    public void projectSetup() throws Exception {
        when(competitionService.findProjectSetupCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/project-setup"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of competitions in project set up the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void upcoming() throws Exception {
        when(competitionService.findUpcomingCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/upcoming"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of upcoming competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void nonIfs() throws Exception {
        when(competitionService.findNonIfsCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/non-ifs"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of non ifs competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void findUnsuccessfulApplications() throws Exception {
        final Long competitionId = 1L;

        List<ApplicationResource> unsuccessfulApplications = ApplicationResourceBuilder.newApplicationResource().build(2);

        when(competitionService.findUnsuccessfulApplications(competitionId)).thenReturn(serviceSuccess(unsuccessfulApplications));

        mockMvc.perform(get("/competition/{id}/unsuccessful-applications", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(unsuccessfulApplications)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which unsuccessful applications need to be found")
                        )
                ));

        verify(competitionService, only()).findUnsuccessfulApplications(competitionId);

    }

    @Test
    public void count() throws Exception {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionService.countCompetitions()).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition/count"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(CompetitionCountResourceDocs.competitionCountResourceFields)
                ));
    }

    @Test
    public void search() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        String searchQuery = "test";
        int page = 1;
        int size = 20;
        when(competitionService.searchCompetitions(searchQuery, page, size)).thenReturn(serviceSuccess(results));

        mockMvc.perform(get("/competition/search/{page}/{size}/?searchQuery=" + searchQuery, page, size))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        requestParameters(parameterWithName("searchQuery").description("The search query to lookup")),
                        pathParameters(
                                parameterWithName("page").description("The page number to be requested"),
                                parameterWithName("size").description("The number of competitions per page")
                        ),
                        responseFields(CompetitionSearchResultDocs.competitionSearchResultFields)
                ));
    }

    @Test
    public void initialiseFormForCompetitionType() throws Exception {
        Long competitionId = 2L;
        Long competitionTypeId = 3L;
        when(competitionSetupService.copyFromCompetitionTypeTemplate(competitionId, competitionTypeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/initialise-form/{competitionTypeId}", competitionId, competitionTypeId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition in competition setup on which the application form should be initialised"),
                                parameterWithName("competitionTypeId").description("id of the competitionType that is being chosen on setup")
                        )
                ));
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 2L;
        when (competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to close the assessment of")
                        )
                ));
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/notify-assessors", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationServiceMock.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/release-feedback", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void findInnovationLeads() throws Exception {
        final Long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionService.findInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/{id}/innovation-leads", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation leads need to be found")
                        )
                ));
    }

    @Test
    public void addInnovationLead() throws Exception {
        final Long competitionId = 1L;
        final Long innovationLeadUserId = 2L;

        when(competitionService.addInnovationLead(competitionId, innovationLeadUserId )).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/add-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation lead needs to be added"),
                                parameterWithName("innovationLeadUserId").description("The id of the innovation lead which is being added")
                        )
                ));

        verify(competitionService, only()).addInnovationLead(competitionId, innovationLeadUserId);

    }

    @Test
    public void removeInnovationLead() throws Exception {
        final Long competitionId = 1L;
        final Long innovationLeadUserId = 2L;

        when(competitionService.removeInnovationLead(competitionId, innovationLeadUserId )).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/remove-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation lead needs to be deleted"),
                                parameterWithName("innovationLeadUserId").description("The id of the innovation lead which is being deleted")
                        )
                ));

        verify(competitionService, only()).removeInnovationLead(competitionId, innovationLeadUserId);

    }

    @Test
    public void updateCompetitionInitialDetails() throws Exception {
        final Long competitionId = 1L;

        CompetitionResource competitionResource = competitionResourceBuilder.build();

        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(competitionSetupService.updateCompetitionInitialDetails(any(), any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/update-competition-initial-details", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(competitionResource)))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition whose initial details are being updated")
                        ),
                        requestFields(CompetitionResourceDocs.competitionResourceFields)
                        )
                );
    }

    @Test
    public void previous() throws Exception {
        when(competitionService.findPreviousCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/previous"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of previous competitions the authenticated user has access to")
                        )
                ));
    }

}
