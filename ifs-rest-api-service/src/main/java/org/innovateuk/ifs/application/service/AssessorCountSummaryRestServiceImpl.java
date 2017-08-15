package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static java.lang.String.format;

/**
 * Implementing class for {@link AssessorCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class AssessorCountSummaryRestServiceImpl extends BaseRestService implements AssessorCountSummaryRestService {

    private static final String assessorCountRestUrl = "/assessorCountSummary";

    @Override
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Optional<Long> innovationSectorId, Optional<BusinessType> businessType, Integer pageIndex, Integer pageSize) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        innovationSectorId.ifPresent(i -> params.add("innovationSector", String.valueOf(i)));
        businessType.ifPresent(b -> params.add("businessType", b.name()));

        String uriWithParams = buildPaginationUri(format("%s/findByCompetitionId/%s", assessorCountRestUrl, competitionId), pageIndex, pageSize, null, params);
        return getWithRestResult(uriWithParams, AssessorCountSummaryPageResource.class);
    }
}