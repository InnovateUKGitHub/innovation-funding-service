package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_ALREADY_ACCEPTED;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_HASH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the InviteServiceImpl class.
 */
@RunWith(MockitoJUnitRunner.class)
public class InviteServiceImplTest {
    private static String HASH = "Hash";
    private static ApplicationInviteResource SENT_RESOURCE = ApplicationInviteResourceBuilder
            .newApplicationInviteResource().withHash(HASH).
                    withStatus(InviteStatus.SENT).build();
    private static ApplicationInviteResource UNSENT_RESOURCE = ApplicationInviteResourceBuilder
            .newApplicationInviteResource().withHash(HASH).
                    withStatus(InviteStatus.OPENED).build();

    @InjectMocks
    private InviteServiceImpl inviteService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Test
    public void testGetInviteWithSentInvite() {
        when(cookieUtil.getCookieValue(request, INVITE_HASH)).thenReturn(HASH);
        when(inviteRestService.getInviteByHash(HASH)).thenReturn(restSuccess(SENT_RESOURCE));

        ApplicationInviteResource result = inviteService.getInviteByRequest(request, response);

        assertThat(result, equalTo(SENT_RESOURCE));
        verify(cookieUtil).getCookieValue(request, INVITE_HASH);
        verifyNoMoreInteractions(cookieUtil);
        verifyNoMoreInteractions(cookieFlashMessageFilter);
    }

    @Test
    public void testGetInviteWithUnsentInivte() {
        when(cookieUtil.getCookieValue(request, INVITE_HASH)).thenReturn(HASH);
        when(inviteRestService.getInviteByHash(HASH)).thenReturn(restSuccess(UNSENT_RESOURCE));

        ApplicationInviteResource result = inviteService.getInviteByRequest(request, response);

        assertThat(result, equalTo(UNSENT_RESOURCE));
        verify(cookieUtil).removeCookie(response, INVITE_HASH);
        verify(cookieFlashMessageFilter).setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
    }

    @Test(expected = RuntimeException.class)
    public void testGetInviteWithInvalidInvite() {
        when(cookieUtil.getCookieValue(request, INVITE_HASH)).thenReturn(HASH);
        when(inviteRestService.getInviteByHash(HASH)).thenReturn(restFailure(RestFailure.error("fail", HttpStatus.I_AM_A_TEAPOT)));

        inviteService.getInviteByRequest(request, response);

    }
}
