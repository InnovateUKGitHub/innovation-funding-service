package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.eugrant.builder.EuContactResourceBuilder.newEuContactResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EuInviteRestServiceImplTest {
    private static final boolean NOTIFIED = true;
    private static final int PAGE_INDEX = 1;
    private static final int PAGE_SIZE = 100;
    private String baseUrl;

    @InjectMocks
    private EuInviteRestServiceImpl euInviteRestService;

    @Mock
    private RootAnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor;

    @Before
    public void setUp() {
        baseUrl = "base";
        euInviteRestService.setServiceUrl(baseUrl);
    }

    @Test
    public void getEuContactsByNotified() {

        RestResult<EuContactPageResource> expected = mock(RestResult.class);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(format("%s/notified/%s", "/eu-contacts", NOTIFIED), PAGE_INDEX, PAGE_SIZE, null, params);

        when(anonymousRestTemplateAdaptor.getWithRestResult(baseUrl + uriWithParams, EuContactPageResource.class)).thenReturn(expected);
        RestResult<EuContactPageResource> result = euInviteRestService.getEuContactsByNotified(NOTIFIED, PAGE_INDEX, PAGE_SIZE);
        assertEquals(expected, result);
    }
}