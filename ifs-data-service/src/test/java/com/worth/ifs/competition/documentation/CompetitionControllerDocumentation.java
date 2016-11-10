package com.worth.ifs.competition.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.competition.controller.CompetitionController;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.documentation.CompetitionCountResourceDocs;
import com.worth.ifs.documentation.CompetitionSearchResultDocs;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                .andDo(this.document.snippets(
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
                .andDo(this.document.snippets(
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
                .andDo(this.document.snippets(
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
                .andDo(this.document.snippets(
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
            .andDo(this.document.snippets(
                responseFields(
                        fieldWithPath("[]").description("list of live competitions the authenticated user has access to")
                )
            ));
    }

    @Test
    public void projectSetup() throws Exception {
        when(competitionService.findProjectSetupCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/projectSetup"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
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
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list of upcoming competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void count() throws Exception {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionService.countCompetitions()).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition/count"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
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
                .andDo(this.document.snippets(
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
        when(competitionSetupService.initialiseFormForCompetitionType(competitionId, competitionTypeId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/initialise-form/{competitionTypeId}", competitionId, competitionTypeId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition in competition setup on which the application form should be initialised"),
                                parameterWithName("competitionTypeId").description("id of the competitionType that is being chosen on setup")
                        )
                ));
    }

}
