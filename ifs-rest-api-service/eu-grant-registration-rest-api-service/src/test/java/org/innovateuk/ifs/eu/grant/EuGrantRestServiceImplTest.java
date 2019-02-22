package org.innovateuk.ifs.eu.grant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EuGrantRestServiceImplTest {

    @InjectMocks
    private EuGrantRestServiceImpl euGrantRestService;

    @Mock
    private RootAnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor;

    private String baseUrl;

    @Before
    public void setUp() {
        baseUrl = "base";
        euGrantRestService.setServiceUrl(baseUrl);
    }

    @Test
    public void create() {
        EuGrantResource euGrantResource = newEuGrantResource().build();
        RestResult<EuGrantResource> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.postWithRestResult(baseUrl + "/eu-grant", EuGrantResource.class)).thenReturn(expected);

        RestResult<EuGrantResource> result = euGrantRestService.create();

        assertEquals(result, expected);
    }

    @Test
    public void save() {

        final UUID uuid = randomUUID();

        EuGrantResource euGrantResource = newEuGrantResource().build();
        euGrantResource.setId(uuid);

        RestResult<Void> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.putWithRestResult(baseUrl + "/eu-grant/" + uuid.toString(), euGrantResource, Void.class)).thenReturn(expected);

        RestResult<Void> result = euGrantRestService.update(euGrantResource);

        assertEquals(result, expected);
    }

    @Test
    public void findById() {

        final UUID uuid = randomUUID();

        EuGrantResource euGrantResource = newEuGrantResource().build();
        euGrantResource.setId(uuid);

        RestResult<EuGrantResource> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.getWithRestResult(baseUrl + "/eu-grant/" + uuid.toString(), EuGrantResource.class)).thenReturn(expected);

        RestResult<EuGrantResource> result = euGrantRestService.findById(uuid);

        assertEquals(result, expected);
    }

    @Test
    public void submit() {
        final UUID uuid = randomUUID();
        RestResult<EuGrantResource> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.postWithRestResult(baseUrl + "/eu-grant/" + uuid.toString() + "/submit", EuGrantResource.class)).thenReturn(expected);

        RestResult<EuGrantResource> result = euGrantRestService.submit(uuid);

        assertEquals(result, expected);
    }
}