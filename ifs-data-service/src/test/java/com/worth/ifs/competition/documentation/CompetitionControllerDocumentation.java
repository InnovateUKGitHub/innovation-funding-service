package com.worth.ifs.competition.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.controller.CompetitionController;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

        when(competitionService.getCompetitionById(competitionId)).thenReturn(ServiceResult.serviceSuccess(competitionResourceBuilder.build()));

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

        when(competitionService.findAll()).thenReturn(ServiceResult.serviceSuccess(competitionResourceBuilder.build(2)));

        mockMvc.perform(get("/competition/findAll"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void findSetupSections() throws Exception {
        List<CompetitionSetupSectionResource> sections = new ArrayList<>();
        sections.add(new CompetitionSetupSectionResource(1L, "Section Name", "section-one", 1));
        sections.add(new CompetitionSetupSectionResource(2L, "Section Two", "section-two", 2));
        when(competitionSetupService.findAllCompetitionSections()).thenReturn(ServiceResult.serviceSuccess(sections));

        mockMvc.perform(get("/competition/sections/getAll"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list of all Competition Setup Sections"),
                                fieldWithPath("[].").description("a single Competition Setup Sections"),
                                fieldWithPath("[].id").description("the unique id"),
                                fieldWithPath("[].name").description("section name"),
                                fieldWithPath("[].path").description("url path that can be used in the client"),
                                fieldWithPath("[].priority").description("the priority for sorting this list")
                        )
                ));
    }

    @Test
    public void findSetupSectionsStatuses() throws Exception {
        Long competitionId = 2L;
        List<CompetitionSetupCompletedSectionResource> status = new ArrayList<>();
        status.add(new CompetitionSetupCompletedSectionResource(1L, 2L, 3L));
        status.add(new CompetitionSetupCompletedSectionResource(2L, 4L, 3L));
        when(competitionSetupService.findAllCompetitionSectionsStatuses(competitionId)).thenReturn(ServiceResult.serviceSuccess(status));

        mockMvc.perform(get("/competition/sectionStatus/find/{id}", competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the competition to get the statuses from")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("the id of the CompetitionSetupCompletedSection"),
                                fieldWithPath("[].competitionSetupSection").description("the CompetitionSetupSection id"),
                                fieldWithPath("[].competition").description("the competition id")
                        )
                ));
    }

    @Test
    public void markAsComplete() throws Exception {
        Long competitionId = 2L;
        Long sectionId = 2L;
        when(competitionSetupService.markSectionComplete(competitionId, sectionId)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/complete/{competitionId}/{sectionId}", competitionId, sectionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as complete"),
                                parameterWithName("sectionId").description("id of the section to mark as complete")
                        )
                ));
    }

    @Test
    public void markAsInComplete() throws Exception {
        Long competitionId = 2L;
        Long sectionId = 2L;
        when(competitionSetupService.markSectionInComplete(competitionId, sectionId)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/incomplete/{competitionId}/{sectionId}", competitionId, sectionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as incomplete"),
                                parameterWithName("sectionId").description("id of the section to mark as incomplete")
                        )
                ));
    }


}