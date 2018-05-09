package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.grantTermsAndConditionsResourceListType;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.junit.Assert.*;

public class TermsAndConditionsRestServiceMocksTest extends BaseRestServiceUnitTest<TermsAndConditionsRestServiceImpl> {

    private static final String termsAndConditionsRestUrl = "/terms-and-conditions";
    private static final Long competitionId = 1L;

    @Override
    protected TermsAndConditionsRestServiceImpl registerRestServiceUnderTest() {
        return new TermsAndConditionsRestServiceImpl();
    }

    @Test
    public void test_getById() {
        GrantTermsAndConditionsResource returnedResponse = newGrantTermsAndConditionsResource().build();

        setupGetWithRestResultExpectations(termsAndConditionsRestUrl + "/getById/" + competitionId,
                GrantTermsAndConditionsResource.class, returnedResponse);
        GrantTermsAndConditionsResource response = service.getById(competitionId).getSuccess();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getLatestTermsAndConditions() {
        List<GrantTermsAndConditionsResource> returnedResponse = newGrantTermsAndConditionsResource().build(1);

        setupGetWithRestResultExpectations(termsAndConditionsRestUrl + "/getLatest",
                grantTermsAndConditionsResourceListType(), returnedResponse);

        List<GrantTermsAndConditionsResource> response = service.getLatestVersionsForAllTermsAndConditions()
                .getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getLatestSiteTermsAndConditions() {
        SiteTermsAndConditionsResource expected = newSiteTermsAndConditionsResource().build();

        setupGetWithRestResultAnonymousExpectations("/terms-and-conditions/site",
                SiteTermsAndConditionsResource.class, expected);

        assertSame(expected, service.getLatestSiteTermsAndConditions().getSuccess());
    }
}
