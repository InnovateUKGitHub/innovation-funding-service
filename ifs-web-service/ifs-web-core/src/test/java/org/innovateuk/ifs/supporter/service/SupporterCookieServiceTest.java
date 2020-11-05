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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.supporter.service.SupporterCookieService.SUPPORTER_PREVIOUS_RESPONSE;
import static org.innovateuk.ifs.supporter.builder.SupporterAssignmentResourceBuilder.newSupporterAssignmentResource;
import static org.junit.Assert.*;
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
    public void saveToSupporterPreviousResponseCookie() {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource().build();

        service.saveToSupporterPreviousResponseCookie(supporterAssignmentResource, response);

        verify(cookieUtil, times(1)).saveToCookie(response, SUPPORTER_PREVIOUS_RESPONSE,
                JsonUtil.getSerializedObject(supporterAssignmentResource));
    }

    @Test
    public void getSupporterPreviousResponseCookie() {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource().build();

        when(cookieUtil.getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE)).thenReturn(JsonUtil.getSerializedObject(supporterAssignmentResource));

        Optional<SupporterAssignmentResource> result = service.getSupporterPreviousResponseCookie(request);

        assertTrue(result.isPresent());
        assertEquals(supporterAssignmentResource, result.get());
        verify(cookieUtil, times(1)).getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE);
    }

    @Test
    public void saveToSupporterPreviousResponseCookieForMaximumWordCount() {
        long wordCount = 250;

        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withComments(Stream.generate(() -> "ABCDEFGHIJKLMNOPQ ").limit(wordCount).collect(Collectors.joining()))
                .build();

        when(cookieUtil.getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE)).thenReturn(JsonUtil.getSerializedObject(supporterAssignmentResource));

        service.saveToSupporterPreviousResponseCookie(supporterAssignmentResource, response);
        Optional<SupporterAssignmentResource> result = service.getSupporterPreviousResponseCookie(request);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getComments());
        assertEquals(wordCount, result.get().getComments().split(" ").length);

        verify(cookieUtil, times(1)).saveToCookie(response, SUPPORTER_PREVIOUS_RESPONSE,
                JsonUtil.getSerializedObject(supporterAssignmentResource));
        verify(cookieUtil, times(1)).getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE);
    }
}
