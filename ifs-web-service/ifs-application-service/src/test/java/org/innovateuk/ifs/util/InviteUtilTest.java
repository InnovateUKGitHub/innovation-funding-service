package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.error.exception.InvalidURLException;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.util.InviteUtil.INVITE_ALREADY_ACCEPTED;
import static org.innovateuk.ifs.util.InviteUtil.INVITE_HASH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Tests for the InviteUtil class.
 */
@RunWith(MockitoJUnitRunner.class)
public class InviteUtilTest {

    @Mock
    private HttpServletResponse response;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Test
    public void testHandleAcceptedInvite() {
        String redirectUrl = InviteUtil.handleAcceptedInvite(cookieFlashMessageFilter, response, cookieUtil);

        verify(cookieUtil).removeCookie(response, INVITE_HASH);
        verify(cookieFlashMessageFilter).setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        assertThat(redirectUrl, equalTo("redirect:/login"));
    }

    @Test(expected = InvalidURLException.class)
    public void testHandleInvalidInvite() {
        InviteUtil.handleInvalidInvite(response, cookieUtil);

        verify(cookieUtil).removeCookie(response, INVITE_HASH);
    }


}
