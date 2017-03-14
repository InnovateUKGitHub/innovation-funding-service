package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class AgreementRestServiceImplTest extends BaseRestServiceUnitTest<AgreementRestServiceImpl> {

    protected AgreementRestServiceImpl registerRestServiceUnderTest() {
        return new AgreementRestServiceImpl();
    }

    private static final String agreementUrl = "/agreement";

    @Test
    public void getCurrentContract() {
        AgreementResource agreementResource = new AgreementResource();

        setupGetWithRestResultExpectations(agreementUrl + "/findCurrent", AgreementResource.class, agreementResource, OK);
        RestResult<AgreementResource> result = service.getCurrentAgreement();

        assertEquals(agreementResource, result.getSuccessObject());
    }
}
