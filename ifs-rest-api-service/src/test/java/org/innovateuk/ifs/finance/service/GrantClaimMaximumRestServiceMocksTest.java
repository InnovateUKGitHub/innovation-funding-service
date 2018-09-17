package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.junit.Test;

import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsSetType;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.CREATED;

public class GrantClaimMaximumRestServiceMocksTest extends BaseRestServiceUnitTest<GrantClaimMaximumRestServiceImpl> {

    private static final String grantClaimMaximumRestURL = "/grant-claim-maximum";

    @Override
    protected GrantClaimMaximumRestServiceImpl registerRestServiceUnderTest() {
        return new GrantClaimMaximumRestServiceImpl();
    }

    @Test
    public void getGrantClaimMaximumById() {
        GrantClaimMaximumResource expected = newGrantClaimMaximumResource().build();

        setupGetWithRestResultExpectations(format("%s/%s", grantClaimMaximumRestURL, expected
                .getId()), GrantClaimMaximumResource.class, expected);

        GrantClaimMaximumResource result = service.getGrantClaimMaximumById(expected.getId())
                .getSuccess();

        assertEquals(expected, result);
    }

    @Test
    public void getGrantClaimMaximumsForCompetitionType() {
        long competitionTypeId = 1L;

        Set<Long> expected = newGrantClaimMaximumResource().build(2).stream().map(GrantClaimMaximumResource::getId)
                .collect(toSet());

        setupGetWithRestResultExpectations(format("%s/get-for-competition-type/%s", grantClaimMaximumRestURL,
                competitionTypeId), longsSetType(), expected);

        Set<Long> result = service.getGrantClaimMaximumsForCompetitionType(competitionTypeId)
                .getSuccess();

        assertEquals(expected, result);
    }

    @Test
    public void save() {
        GrantClaimMaximumResource expected = newGrantClaimMaximumResource().build();
        GrantClaimMaximumResource expectedResponse = newGrantClaimMaximumResource().build();

        setupPostWithRestResultExpectations(format("%s/", grantClaimMaximumRestURL), GrantClaimMaximumResource
                .class, expected, expectedResponse, CREATED);

        GrantClaimMaximumResource result = service.save(expected).getSuccess();

        assertEquals(expectedResponse, result);
    }

    @Test
    public void isMaximumFundingLevelOverridden() {
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/maximum-funding-level-overridden/%s", grantClaimMaximumRestURL,
                competitionId), Boolean.class, true);

        assertTrue(service.isMaximumFundingLevelOverridden(competitionId).getSuccess());
    }
}