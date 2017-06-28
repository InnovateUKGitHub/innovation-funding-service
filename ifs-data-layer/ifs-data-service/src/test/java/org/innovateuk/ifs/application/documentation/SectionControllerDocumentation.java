package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.SectionController;
import org.innovateuk.ifs.application.resource.SectionType;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.SectionDocs.sectionResourceBuilder;
import static org.innovateuk.ifs.documentation.SectionDocs.sectionResourceFields;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerDocumentation extends BaseControllerMockMVCTest<SectionController> {

    private static final String baseURI = "/section";

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }

    @Test
    public void getById() throws Exception {
        final Long id = 1L;

        when(sectionServiceMock.getById(id)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/{id}", id))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the section to be retrieved")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void getCompletedSectionsByOrganisation() throws Exception {
        final Long id = 1L;

        final Map<Long, Set<Long>> result = asMap(1L, asSet(2L, 3L));

        when(sectionServiceMock.getCompletedSections(id)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/getCompletedSectionsByOrganisation/{applicationId}", id))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of an application")
                        ),
                        responseFields(
                                fieldWithPath("1.[]").description("completed sections belonging to organisation with id 1")
                        )
                ));
    }

    @Test
    public void getCompletedSections() throws Exception {
        final Long organisationId = 1L;

        final Long applicationId = 2L;

        final Set<Long> result = asSet(2L, 3L);

        when(sectionServiceMock.getCompletedSections(applicationId, organisationId)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/getCompletedSections/{applicationId}/{organisationId}", applicationId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of an application"),
                                parameterWithName("organisationId").description("Id of an organisation")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("completed sections belonging to one organisation")
                        )
                ));
    }

    @Test
    public void markAsComplete() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsCompleteById = 3L;

        when(sectionServiceMock.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById)).thenReturn(serviceSuccess(emptyList()));

        mockMvc.perform(post(baseURI + "/markAsComplete/{sectionId}/{applicationId}/{markedAsCompleteById}",
                sectionId, applicationId, markedAsCompleteById))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the section"),
                                parameterWithName("applicationId").description("Id of the application"),
                                parameterWithName("markedAsCompleteById").description("Id of the process role marking the section as complete")
                        )
                ));
    }

    @Test
    public void markAsNotRequired() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsNotRequiredById = 3L;

        when(sectionServiceMock.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/markAsNotRequired/{sectionId}/{applicationId}/{markedAsNotRequiredById}",
                sectionId, applicationId, markedAsNotRequiredById))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the section"),
                                parameterWithName("applicationId").description("Id of the application"),
                                parameterWithName("markedAsNotRequiredById").description("Id of the process role marking the section as not required")
                        )
                ));
    }

    @Test
    public void markAsInComplete() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsInCompleteById = 3L;

        when(sectionServiceMock.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/markAsInComplete/{sectionId}/{applicationId}/{markedAsInCompleteById}",
                sectionId, applicationId, markedAsInCompleteById))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the section"),
                                parameterWithName("applicationId").description("Id of the application"),
                                parameterWithName("markedAsInCompleteById").description("Id of the process role marking the section as incomplete")
                        )
                ));
    }

    @Test
    public void allSectionsMarkedAsComplete() throws Exception {
        final Long id = 1L;

        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, id, null)).thenReturn(serviceSuccess(true));
        mockMvc.perform(get(baseURI + "/allSectionsMarkedAsComplete/{applicationId}", id))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application")
                        )
                ));
    }

    @Test
    public void getIncompleteSections() throws Exception {
        final Long applicationId = 1L;

        when(sectionServiceMock.getIncompleteSections(applicationId)).thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get(baseURI + "/getIncompleteSections/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of incomplete sections")
                        )
                ));
    }

    @Test
    public void getNextSection() throws Exception {
        final Long sectionId = 1L;

        when(sectionServiceMock.getNextSection(sectionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));
        mockMvc.perform(get(baseURI + "/getNextSection/{sectionId}", sectionId))
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

        mockMvc.perform(get(baseURI + "/getPreviousSection/{sectionId}", sectionId))
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

        mockMvc.perform(get(baseURI + "/getSectionByQuestionId/{questionId}", questionId))
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

        when(sectionServiceMock.getQuestionsForSectionAndSubsections(sectionId)).thenReturn(serviceSuccess(asSet(1L, 2L, 3L)));

        mockMvc.perform(get(baseURI + "/getQuestionsForSectionAndSubsections/{sectionId}", sectionId))
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

        mockMvc.perform(get(baseURI + "/getSectionsByCompetitionIdAndType/{competitionId}/{sectionType}", competitionId, sectionType))
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

        mockMvc.perform(get(baseURI + "/getByCompetition/{competitionId}", competitionId))
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

        mockMvc.perform(get(baseURI + "/getByCompetitionIdVisibleForAssessment/{competitionId}", competitionId))
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
