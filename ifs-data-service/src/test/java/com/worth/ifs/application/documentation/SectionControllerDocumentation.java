package com.worth.ifs.application.documentation;

import java.util.Map;
import java.util.Set;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.SectionController;
import com.worth.ifs.application.transactional.SectionService;

import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.SectionResourceDocs.sectionResourceBuilder;
import static com.worth.ifs.documentation.SectionResourceDocs.sectionResourceFields;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class SectionControllerDocumentation extends BaseControllerMockMVCTest<SectionController> {
    private static final String baseURI = "/section";
    private static final String docBase = "section/";

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }

    @Mock
    SectionService sectionService;

    @Test
    public void documentFindOne() throws Exception {
        final Long id = 1L;

        when(sectionService.getById(id)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/{id}", id))
                .andDo(document("section/find-one",
                        pathParameters(
                            parameterWithName("id").description("Id of the section to be retrieved")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void documentGetCompletedSectionsByOrganisation() throws Exception {
        final Long id = 1L;

        final Map<Long, Set<Long>> result = asMap(1L, asSet(2L, 3L));

        when (sectionService.getCompletedSections(id)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/getCompletedSectionsByOrganisation/{applicationId}", id))
                .andDo(document("section/get-completed-by-organisation",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of an application")
                        ),
                        responseFields(
                                fieldWithPath("1.[]").description("completed sections belonging to organisation with id 1")
                        )
                ));
    }

    @Test
    public void documentGetCompletedSectionsByApplicationAndOrganisation() throws Exception {
        final Long organisationId = 1L;

        final Long applicationId = 2L;

        final Set<Long> result = asSet(2L, 3L);

        when (sectionService.getCompletedSections(applicationId, organisationId)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/getCompletedSections/{applicationId}/{organisationId}", applicationId, organisationId))
                .andDo(document("section/get-completed-by-application-and-organisation",
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
    public void documentAllSectionsMarkedAsComplete() throws Exception {
        final Long id = 1L;

        when(sectionService.childSectionsAreCompleteForAllOrganisations(null, id, null)).thenReturn(serviceSuccess(true));
        mockMvc.perform(get(baseURI + "/allSectionsMarkedAsComplete/{applicationId}", id))
                .andDo(document("section/all-sections-complete",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application")
                        )
                ));
    }

    @Test
    public void documentGetIncompleteSections() throws Exception {
        final Long applicationId = 1L;

        when(sectionService.getIncompleteSections(applicationId)).thenReturn(serviceSuccess(asList(1L,2L)));

        mockMvc.perform(get(baseURI + "/getIncompleteSections/{applicationId}", applicationId))
                .andDo(document("section/get-incomplete",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of incomplete sections")
                        )
                ));
    }

    @Test
    public void documentFindByName() throws Exception {
        final String name = "SectionName";

        when(sectionService.findByName(name)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/findByName/{name}", name))
                .andDo(document("section/find-by-name",
                        pathParameters(
                                parameterWithName("name").description("name of the Section to be retrieved")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void documentgetNextSection() throws Exception {
        final Long sectionId = 1L;

        when(sectionService.getNextSection(sectionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));
        mockMvc.perform(get(baseURI + "/getNextSection/{sectionId}", sectionId))
                .andDo(document("section/get-next-section",
                        pathParameters(
                                parameterWithName("sectionId").description("id of current section")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void documentgetPreviousSection() throws Exception {
        final Long sectionId = 2L;

        when(sectionService.getPreviousSection(sectionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/getPreviousSection/{sectionId}", sectionId))
                .andDo(document("section/get-previous-section",
                        pathParameters(
                                parameterWithName("sectionId").description("id of current application")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void documentGetSectionByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(sectionService.getSectionByQuestionId(questionId)).thenReturn(serviceSuccess(sectionResourceBuilder.build()));

        mockMvc.perform(get(baseURI + "/getSectionByQuestionId/{questionId}", questionId))
                .andDo(document("section/get-by-question",
                        pathParameters(
                                parameterWithName("questionId").description("id of question for which to retrieve the section")
                        ),
                        responseFields(sectionResourceFields)
                ));
    }

    @Test
    public void documentGetQuestionsForSectionAndSubsections() throws Exception {
        final Long sectionId = 1L;

        when(sectionService.getQuestionsForSectionAndSubsections(sectionId)).thenReturn(serviceSuccess(asSet(1L, 2L, 3L)));

        mockMvc.perform(get(baseURI + "/getQuestionsForSectionAndSubsections/{sectionId}", sectionId))
                .andDo(document(docBase + "get-questions-for-section-and-subsections",
                        pathParameters(
                                parameterWithName("sectionId").description("id of the parent section")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of unique questions belonging to the section or one of its subsections")
                        )
                ));
    }


}