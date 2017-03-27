package org.innovateuk.ifs.agreement.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.agreement.service.AgreementService;
import org.innovateuk.ifs.agreement.service.AgreementServiceImpl;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.service.AgreementRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AgreementServiceImplTest extends BaseServiceUnitTest<AgreementService> {

    @Mock
    private AgreementRestService agreementRestService;

    @Override
    protected AgreementService supplyServiceUnderTest() {
        return new AgreementServiceImpl();
    }

    @Test
    public void getCurrentAgreement() throws Exception {
        AgreementResource expected = newAgreementResource().build();

        when(agreementRestService.getCurrentAgreement()).thenReturn(restSuccess(expected));

        AgreementResource response = service.getCurrentAgreement();

        assertEquals(expected, response);
        verify(agreementRestService, only()).getCurrentAgreement();
    }
}
