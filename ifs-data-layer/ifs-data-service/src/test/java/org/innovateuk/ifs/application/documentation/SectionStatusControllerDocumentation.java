package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.SectionStatusController;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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

public class SectionStatusControllerDocumentation extends BaseControllerMockMVCTest<SectionStatusController> {

    @Mock
    private SectionStatusService sectionStatusServiceMock;

    private static final String baseURI = "/section-status";

    @Override
    protected SectionStatusController supplyControllerUnderTest() {
        return new SectionStatusController();
    }


    @Test
    public void getCompletedSectionsByOrganisation() throws Exception {
        final Long id = 1L;

        final Map<Long, Set<Long>> result = asMap(1L, asSet(2L, 3L));

        when(sectionStatusServiceMock.getCompletedSections(id)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/get-completed-sections-by-organisation/{applicationId}", id))
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
        final long organisationId = 1L;

        final long applicationId = 2L;

        final Set<Long> result = asSet(2L, 3L);

        when(sectionStatusServiceMock.getCompletedSections(applicationId, organisationId)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/get-completed-sections/{applicationId}/{organisationId}", applicationId, organisationId))
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

        when(sectionStatusServiceMock.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById)).thenReturn(serviceSuccess(emptyList()));

        mockMvc.perform(post(baseURI + "/mark-as-complete/{sectionId}/{applicationId}/{markedAsCompleteById}",
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

        when(sectionStatusServiceMock.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/mark-as-not-required/{sectionId}/{applicationId}/{markedAsNotRequiredById}",
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

        when(sectionStatusServiceMock.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/mark-as-in-complete/{sectionId}/{applicationId}/{markedAsInCompleteById}",
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
        final long id = 1L;

        when(sectionStatusServiceMock.childSectionsAreCompleteForAllOrganisations(null, id, null)).thenReturn(serviceSuccess(true));
        mockMvc.perform(get(baseURI + "/all-sections-marked-as-complete/{applicationId}", id))
                .andExpect(status().isOk())
                .andDo(document("section/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application")
                        )
                ));
    }

    @Test
    public void getIncompleteSections() throws Exception {
        final long applicationId = 1L;

        when(sectionStatusServiceMock.getIncompleteSections(applicationId)).thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get(baseURI + "/get-incomplete-sections/{applicationId}", applicationId))
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
}
