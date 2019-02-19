package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.invite.builder.EuContactPageResourceBuilder.newEuContactPageResource;
import static org.innovateuk.ifs.invite.builder.EuContactResourceBuilder.newEuContactResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class EuContactRestServiceImplTest extends BaseRestServiceUnitTest<EuContactRestServiceImpl> {

    private static final String BASE_URL = "/eu-contacts";
    private static final boolean NOTIFIED = true;
    private static final int PAGE_INDEX = 1;
    private static final int PAGE_SIZE = 100;

    @Override
    protected EuContactRestServiceImpl registerRestServiceUnderTest() {
        return new EuContactRestServiceImpl();
    }

    @Test
    public void getEuContactsByNotified() {

        List<EuContactResource> euContactResources = newEuContactResource().build(3);
        EuContactPageResource euContactPageResource = newEuContactPageResource()
                .withContent(euContactResources)
                .build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(format("%s/notified/%s", BASE_URL, NOTIFIED), PAGE_INDEX, PAGE_SIZE, null, params);

        setupGetWithRestResultAnonymousExpectations(uriWithParams, EuContactPageResource.class, euContactPageResource, OK);
        RestResult<EuContactPageResource> result = service.getEuContactsByNotified(NOTIFIED, PAGE_INDEX, PAGE_SIZE);
        assertEquals(euContactPageResource, result.getSuccess());
    }
}
