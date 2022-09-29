package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


public class CompetitionEoiEvidenceConfigControllerTest extends BaseControllerMockMVCTest<CompetitionEoiEvidenceConfigController> {

    @Mock
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Override
    protected CompetitionEoiEvidenceConfigController supplyControllerUnderTest() {
        return new CompetitionEoiEvidenceConfigController();
    }

    @Test
    public void getValidFileTypesIdsForEoiEvidence() throws Exception {
        long competitionEoiEvidenceConfigId = 1L;
        List<Long> fileTypeIds = asList(1L, 3L, 4L);
        when(competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigId)).thenReturn(serviceSuccess(fileTypeIds));

        mockMvc.perform(get("/competition-valid-file-type-ids/{competitionEoiEvidenceConfigId}", competitionEoiEvidenceConfigId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileTypeIds)));

        verify(competitionEoiEvidenceConfigService).getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigId);

    }
}