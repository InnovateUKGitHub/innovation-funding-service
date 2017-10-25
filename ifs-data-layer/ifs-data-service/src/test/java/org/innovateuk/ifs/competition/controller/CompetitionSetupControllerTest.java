package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupControllerTest extends BaseControllerMockMVCTest<CompetitionSetupController> {

    @Override
    protected CompetitionSetupController supplyControllerUnderTest() {
        return new CompetitionSetupController();
    }

    @Test
    public void testUpdateCompetitionInitialDetails() throws Exception {
        final Long competitionId = 1L;
        final Long leadTechnologistUserId = 7L;

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withInnovationAreaNames(Collections.emptySet())
                .withLeadTechnologist(leadTechnologistUserId)
                .build();

        when(competitionServiceMock.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(competitionSetupServiceMock.updateCompetitionInitialDetails(any(), any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/setup/{id}/update-competition-initial-details", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(competitionResource)))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).getCompetitionById(competitionId);
        verify(competitionSetupServiceMock, only()).updateCompetitionInitialDetails(competitionId, competitionResource, leadTechnologistUserId);
    }

    @Test
    public void testGetSectionStatuses() throws Exception {
        final Long competitionId = 5L;
        final Map<CompetitionSetupSection, Optional<Boolean>> sectionStatuses = asMap(CompetitionSetupSection.INITIAL_DETAILS, Optional.of(TRUE),
                CompetitionSetupSection.CONTENT, Optional.of(TRUE),
                CompetitionSetupSection.APPLICATION_FORM, Optional.of(FALSE));

        when(competitionSetupServiceMock.getSectionStatuses(competitionId)).thenReturn(serviceSuccess(sectionStatuses));

        mockMvc.perform(get("/competition/setup/sectionStatus/{competitionId}", competitionId))
                .andExpect(status().is2xxSuccessful());

        verify(competitionSetupServiceMock, only()).getSectionStatuses(competitionId);
    }
}
