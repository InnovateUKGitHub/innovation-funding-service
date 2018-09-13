package org.innovateuk.ifs.eugrant.overview.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@RunWith(MockitoJUnitRunner.class)
public class EuGrantCookieServiceTest extends BaseServiceUnitTest<EuGrantCookieService> {

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private EuGrantRestService euGrantRestService;

    @Mock
    private EuGrantHttpServlet euGrantHttpServlet;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    public static final String EU_GRANT_ID = "EU_GRANT_ID";


    protected EuGrantCookieService supplyServiceUnderTest() {
        return new EuGrantCookieService();
    }

    @Test
    public void getEuGrantResourceWhenCookieIsPresent() throws Exception {
        UUID euGrantUUID = UUID.fromString("31a05805-c748-492d-a862-c047102516be");

        when(euGrantHttpServlet.request()).thenReturn(request);

        when(cookieUtil.getCookieValue(request, EU_GRANT_ID)).thenReturn(String.valueOf(euGrantUUID));
        when(euGrantRestService.findById(euGrantUUID)).thenReturn(RestResult.restSuccess(new EuGrantResource()));

        EuGrantResource euGrantResource = service.get();

        verify(cookieUtil, times(1)).getCookieValue(request, EU_GRANT_ID);
        verify(euGrantRestService, times(1)).findById(euGrantUUID);
    }

    @Test
    public void getEuGrantResourceWhenCookieIsNotPresent() throws Exception {

        when(euGrantHttpServlet.request()).thenReturn(request);

        when(cookieUtil.getCookieValue(request, EU_GRANT_ID)).thenReturn("");

        EuGrantResource euGrantResource = service.get();

        verify(cookieUtil, times(1)).getCookieValue(request, EU_GRANT_ID);
        verify(euGrantRestService, never()).findById(any());
    }

    @Test
    public void saveWhenCookieIsPresent() throws Exception {

        UUID euGrantUUID = UUID.fromString("31a05805-c748-492d-a862-c047102516be");

        EuGrantResource euGrantResource = newEuGrantResource().build();

        when(euGrantHttpServlet.request()).thenReturn(request);

        when(cookieUtil.getCookieValue(request, EU_GRANT_ID)).thenReturn(String.valueOf(euGrantUUID));
        when(euGrantRestService.update(euGrantResource)).thenReturn(RestResult.restSuccess());

        service.save(euGrantResource);

        verify(cookieUtil, times(1)).getCookieValue(request, EU_GRANT_ID);
        verify(euGrantRestService, times(1)).update(euGrantResource);
        verify(euGrantRestService, never()).create();
    }

    @Test
    public void saveWhenCookieIsNotPresent() throws Exception {

        UUID euGrantUUID = UUID.fromString("31a05805-c748-492d-a862-c047102516be");

        EuGrantResource euGrantResource = newEuGrantResource().build();
        euGrantResource.setId(euGrantUUID);

        when(euGrantHttpServlet.request()).thenReturn(request);
        when(euGrantHttpServlet.response()).thenReturn(response);

        when(cookieUtil.getCookieValue(request, EU_GRANT_ID)).thenReturn("");
        when(euGrantRestService.create()).thenReturn(RestResult.restSuccess(euGrantResource));
        when(euGrantRestService.update(euGrantResource)).thenReturn(RestResult.restSuccess());

        service.save(euGrantResource);

        verify(cookieUtil, times(1)).getCookieValue(request, EU_GRANT_ID);
        verify(euGrantRestService, times(1)).create();
        verify(euGrantRestService, times(1)).update(euGrantResource);

    }
}
