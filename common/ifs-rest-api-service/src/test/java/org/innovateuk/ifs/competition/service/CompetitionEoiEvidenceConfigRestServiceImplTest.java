package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionEoiEvidenceConfigRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionEoiEvidenceConfigRestServiceImpl> {

    @Test
    public void getValidFileTypesIdsForEoiEvidence() {
        List<Long> fileTypeIds = asList(1L, 2L);
        long competitionEoiEvidenceConfigId = 1L;

        setupGetWithRestResultAnonymousExpectations(format("/competition-valid-file-type-ids/%s", competitionEoiEvidenceConfigId), longsListType(), fileTypeIds, HttpStatus.OK);
        RestResult<List<Long>> result = service.getValidFileTypeIdsForEoiEvidence(competitionEoiEvidenceConfigId);

        assertTrue(result.isSuccess());
        assertEquals(fileTypeIds, result.getSuccess());
    }

    @Override
    protected CompetitionEoiEvidenceConfigRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionEoiEvidenceConfigRestServiceImpl();
    }
}