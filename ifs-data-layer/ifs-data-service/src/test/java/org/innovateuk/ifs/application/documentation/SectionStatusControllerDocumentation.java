package org.innovateuk.ifs.application.documentation;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.SectionStatusController;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;
import java.util.Set;


import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
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

        final Map<Long, Set<Long>> result = asMap(1L, ImmutableSet.of(2L, 3L));

        when(sectionStatusServiceMock.getCompletedSections(id)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/get-completed-sections-by-organisation/{applicationId}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCompletedSections() throws Exception {
        final long organisationId = 1L;

        final long applicationId = 2L;

        final Set<Long> result = ImmutableSet.of(2L, 3L);

        when(sectionStatusServiceMock.getCompletedSections(applicationId, organisationId)).thenReturn(serviceSuccess(result));

        mockMvc.perform(get(baseURI + "/get-completed-sections/{applicationId}/{organisationId}", applicationId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void markAsComplete() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsCompleteById = 3L;

        when(sectionStatusServiceMock.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById)).thenReturn(serviceSuccess(noErrors()));

        mockMvc.perform(post(baseURI + "/mark-as-complete/{sectionId}/{applicationId}/{markedAsCompleteById}",
                sectionId, applicationId, markedAsCompleteById)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void markAsNotRequired() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsNotRequiredById = 3L;

        when(sectionStatusServiceMock.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/mark-as-not-required/{sectionId}/{applicationId}/{markedAsNotRequiredById}",
                sectionId, applicationId, markedAsNotRequiredById)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void markAsInComplete() throws Exception {
        final long sectionId = 1L;
        final long applicationId = 2L;
        final long markedAsInCompleteById = 3L;

        when(sectionStatusServiceMock.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseURI + "/mark-as-in-complete/{sectionId}/{applicationId}/{markedAsInCompleteById}",
                sectionId, applicationId, markedAsInCompleteById)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void allSectionsMarkedAsComplete() throws Exception {
        final long id = 1L;

        when(sectionStatusServiceMock.sectionsCompleteForAllOrganisations(id)).thenReturn(serviceSuccess(true));
        mockMvc.perform(get(baseURI + "/all-sections-marked-as-complete/{applicationId}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
