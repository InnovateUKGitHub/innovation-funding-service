package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EuGrantRestServiceImplTest {

    @InjectMocks
    private EuGrantRestServiceImpl euGrantRestService;

    @Mock
    private RootAnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor;

    @Test
    public void create() throws Exception {
        String baseUrl = "base";
        euGrantRestService.setServiceUrl(baseUrl);

        EuGrantResource euGrantResource = newEuGrantResource().build();
        RestResult<EuGrantResource> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.postWithRestResult(baseUrl + "/eu-grant", EuGrantResource.class)).thenReturn(expected);

        RestResult<EuGrantResource> result = euGrantRestService.create();

        assertEquals(result, expected);
    }

    @Test
    public void save() throws Exception {

        final UUID uuid = randomUUID();

        String baseUrl = "base";
        euGrantRestService.setServiceUrl(baseUrl);

        EuGrantResource euGrantResource = newEuGrantResource().build();
        euGrantResource.setId(uuid);

        RestResult<Void> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.putWithRestResult(baseUrl + "/eu-grant/" + uuid.toString(), euGrantResource, Void.class)).thenReturn(expected);

        RestResult<Void> result = euGrantRestService.update(euGrantResource);

        assertEquals(result, expected);
    }

    @Test
    public void findById() throws Exception {

        final UUID uuid = randomUUID();

        String baseUrl = "base";
        euGrantRestService.setServiceUrl(baseUrl);

        EuGrantResource euGrantResource = newEuGrantResource().build();
        euGrantResource.setId(uuid);

        RestResult<EuGrantResource> expected = mock(RestResult.class);

        when(anonymousRestTemplateAdaptor.getWithRestResult(baseUrl + "/eu-grant/" + uuid.toString(), EuGrantResource.class)).thenReturn(expected);

        RestResult<EuGrantResource> result = euGrantRestService.findById(uuid);

        assertEquals(result, expected);
    }
}