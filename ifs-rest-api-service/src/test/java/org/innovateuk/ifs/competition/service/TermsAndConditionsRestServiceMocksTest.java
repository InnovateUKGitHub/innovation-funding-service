package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.termsAndConditionsResourceListType;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TermsAndConditionsRestServiceMocksTest extends BaseRestServiceUnitTest<TermsAndConditionsRestServiceImpl> {

    private static final String termsAndConditionsRestUrl = "/terms-and-conditions";
    private static final Long competitionId = 1L;

    @Override
    protected TermsAndConditionsRestServiceImpl registerRestServiceUnderTest() {
        TermsAndConditionsRestServiceImpl termsAndConditionsRestService = new TermsAndConditionsRestServiceImpl();
        return termsAndConditionsRestService;
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
        List<TermsAndConditionsResource> response = service.getLatestTermsAndConditions().getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
