package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.junit.Assert.assertSame;

public class TermsAndConditionsRestServiceMocksTest extends BaseRestServiceUnitTest<TermsAndConditionsRestServiceImpl> {

    @Override
    protected TermsAndConditionsRestServiceImpl registerRestServiceUnderTest() {
        return new TermsAndConditionsRestServiceImpl();
    }

    @Test
    public void test_getById() {
        TermsAndConditionsResource returnedResponse = newTermsAndConditionsResource().build();

        setupGetWithRestResultExpectations(termsAndConditionsRestUrl + "/getById/" + competitionId,
                TermsAndConditionsResource.class, returnedResponse);
        TermsAndConditionsResource response = service.getById(competitionId).getSuccess();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getLatestTermsAndConditions() {
        List<TermsAndConditionsResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(newTermsAndConditionsResource().build());

        setupGetWithRestResultExpectations(termsAndConditionsRestUrl + "/getLatest",
                termsAndConditionsResourceListType(), returnedResponse);
        List<TermsAndConditionsResource> response = service.getLatestVersionsForAllTermsAndConditions().getSuccess();

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
