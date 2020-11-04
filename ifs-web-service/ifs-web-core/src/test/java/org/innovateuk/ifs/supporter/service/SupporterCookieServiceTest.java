package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.innovateuk.ifs.supporter.service.SupporterCookieService.SUPPORTER_PREVIOUS_RESPONSE;
import static org.innovateuk.ifs.supporter.builder.SupporterAssignmentResourceBuilder.newSupporterAssignmentResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SupporterCookieServiceTest  extends BaseServiceUnitTest<SupporterCookieService> {

    @Mock
    private EncryptedCookieService cookieUtil;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @Override
    protected SupporterCookieService supplyServiceUnderTest() {
        return new SupporterCookieService();
    }

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        super.setup();
    }

    @Test
    public void saveToSupporterPreviousResponseCookie() throws Exception {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource().build();

        service.saveToSupporterPreviousResponseCookie(supporterAssignmentResource, response);

        verify(cookieUtil, times(1)).saveToCookie(response, SUPPORTER_PREVIOUS_RESPONSE, JsonUtil.getSerializedObject(supporterAssignmentResource));
    }

    @Test
    public void getSupporterPreviousResponseCookie() throws Exception {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource().build();

        when(cookieUtil.getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE)).thenReturn(JsonUtil.getSerializedObject(supporterAssignmentResource));

        Optional<SupporterAssignmentResource> result = service.getSupporterPreviousResponseCookie(request);

        assertTrue(result.isPresent());
        assertEquals(supporterAssignmentResource, result.get());
        verify(cookieUtil, times(1)).getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE);
    }

}
