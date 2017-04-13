package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test Class for functionality in {@link AffiliationServiceImpl}
 */
public class AffiliationServiceImplTest extends BaseServiceUnitTest<AffiliationService> {

    @Mock
    private AffiliationRestService affiliationRestService;

    @Override
    protected AffiliationService supplyServiceUnderTest() {
        return new AffiliationServiceImpl();
    }

    @Test
    public void getUserAffilliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> expected = newAffiliationResource().build(2);

        when(affiliationRestService.getUserAffiliations(userId)).thenReturn(restSuccess(expected));

        List<AffiliationResource> response = service.getUserAffiliations(userId);
        assertSame(expected, response);
        verify(affiliationRestService, only()).getUserAffiliations(userId);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(affiliationRestService.updateUserAffiliations(userId, affiliations)).thenReturn(restSuccess());

        service.updateUserAffiliations(userId, affiliations).getSuccessObjectOrThrowException();
        verify(affiliationRestService, only()).updateUserAffiliations(userId, affiliations);
    }
}
