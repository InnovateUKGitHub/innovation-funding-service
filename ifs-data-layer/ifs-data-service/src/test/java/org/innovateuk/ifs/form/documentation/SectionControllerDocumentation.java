package org.innovateuk.ifs.form.documentation;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.controller.SectionController;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.SectionDocs.sectionResourceBuilder;
import static org.innovateuk.ifs.documentation.SectionDocs.sectionResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerDocumentation extends BaseControllerMockMVCTest<SectionController> {

    private static final String baseURI = "/section";

    @Mock
    private SectionService sectionServiceMock;

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }

    @Test
    public void getById() throws Exception {
        final Long id = 1L;

        when(sectionServiceMock.getById(id)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/{id}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the section to be retrieved")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }


    @Test
    public void getNextSection() throws Exception {
        final Long sectionId = 1L;

        when(sectionServiceMock.getNextSection(sectionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));
        mockMvc.perform(get(baseURI + "/get-next-section/{sectionId}", sectionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("id of current section")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void getPreviousSection() throws Exception {
        final Long sectionId = 2L;

        when(sectionServiceMock.getPreviousSection(sectionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/get-previous-section/{sectionId}", sectionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("id of current application")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void getSectionByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(sectionServiceMock.getSectionByQuestionId(questionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/get-section-by-question-id/{questionId}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("id of question for which to retrieve the section")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void getQuestionsForSectionAndSubsections() throws Exception {
        final Long sectionId = 1L;

        when(sectionServiceMock.getQuestionsForSectionAndSubsections(sectionId)).thenReturn(serviceSuccess(ImmutableSet.of(1L, 2L, 3L)));

        mockMvc.perform(get(baseURI + "/get-questions-for-section-and-subsections/{sectionId}", sectionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("id of the parent section")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of unique questions belonging to the section or one of its subsections")
                        )
                ));
    }

    @Test
    public void getSectionsByCompetitionIdAndType() throws Exception {
        final long competitionId = 1L;
        final SectionType sectionType = SectionType.GENERAL;

        when(sectionServiceMock.getSectionsByCompetitionIdAndType(competitionId, sectionType)).thenReturn(serviceSuccess(
                singletonList(sectionResourceBuilder.build())));

        mockMvc.perform(get(baseURI + "/get-sections-by-competition-id-and-type/{competitionId}/{sectionType}", competitionId, sectionType)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("sectionType").description("Section type")
                        ),
                        responseFields(fieldWithPath("[]").description("List of sections"))
                                .andWithPrefix("[].", sectionResourceFields)
                ));
    }

    @Test
    public void getByCompetition() throws Exception {
        final long competitionId = 1L;

        when(sectionServiceMock.getByCompetitionId(competitionId)).thenReturn(serviceSuccess(singletonList(
                sectionResourceBuilder.build())));

        mockMvc.perform(get(baseURI + "/get-by-competition/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of sections"))
                                .andWithPrefix("[].", sectionResourceFields)
                ));
    }

    @Test
    public void getByCompetitionIdVisibleForAssessment() throws Exception {
        final long competitionId = 1L;

        when(sectionServiceMock.getByCompetitionIdVisibleForAssessment(competitionId)).thenReturn(serviceSuccess(
                singletonList(sectionResourceBuilder.build())));

        mockMvc.perform(get(baseURI + "/get-by-competition-id-visible-for-assessment/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of sections"))
                                .andWithPrefix("[].", sectionResourceFields)
                ));
    }

}
