package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
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

        RestResult<EuGrantPageResource> expected = mock(RestResult.class);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(format("%s/notified/%s", "/eu-grants", NOTIFIED), PAGE_INDEX, PAGE_SIZE, null, params);

        when(anonymousRestTemplateAdaptor.getWithRestResult(baseUrl + uriWithParams, EuGrantPageResource.class)).thenReturn(expected);
        RestResult<EuGrantPageResource> result = euInviteRestService.getEuGrantsByNotified(NOTIFIED, PAGE_INDEX, PAGE_SIZE);
        assertEquals(expected, result);
    }

    @Test
    public void sendInvites() {
        RestResult<Void> expected = mock(RestResult.class);

        UUID uuid1 = new UUID(1L, 1L);
        UUID uuid2 = new UUID(1L, 1L);
        UUID uuid3 = new UUID(1L, 1L);
        List<UUID> euGrantUuids = Arrays.asList(uuid1, uuid2, uuid3);

        when(anonymousRestTemplateAdaptor.postWithRestResult(baseUrl + "/eu-grants/send-invites", euGrantUuids, Void.class)).thenReturn(expected);

        RestResult<Void> result = euInviteRestService.sendInvites(euGrantUuids);

        assertEquals(result, expected);
    }
}