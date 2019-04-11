package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AffiliationRestServiceImplTest extends BaseRestServiceUnitTest<AffiliationRestServiceImpl> {

    private static final String affiliationUrl = "/affiliation";

    @Override
    protected AffiliationRestServiceImpl registerRestServiceUnderTest() {
        AffiliationRestServiceImpl affiliationRestService = new AffiliationRestServiceImpl();
        return affiliationRestService;
    }

    @Test
    public void getUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> affiliationResources = newAffiliationResource().build(2);
        AffiliationListResource expected = newAffiliationListResource()
                .withAffiliationList(affiliationResources)
                .build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getUserAffiliations", affiliationUrl, userId), AffiliationListResource.class, expected);

        AffiliationListResource response = service.getUserAffiliations(userId).getSuccess();
        assertEquals(expected, response);
    }

    @Test
    public void updateUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> affiliationResources = newAffiliationResource().build(2);
        AffiliationListResource expected = newAffiliationListResource()
                .withAffiliationList(affiliationResources)
                .build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateUserAffiliations", affiliationUrl, userId), expected, OK);

        RestResult<Void> response = service.updateUserAffiliations(userId, expected);
        assertTrue(response.isSuccess());
    }
}
