package org.innovateuk.ifs.competition.status;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PublicContentStatusTextTest {

    PublicContentStatusText publicContentStatusText;

    @Test
    public void getHeader() throws Exception {
        assertEquals("Closing soon", publicContentStatusText.CLOSING_SOON.getHeader());
    }

    @Test
    public void getOpenTense() throws Exception {
        assertEquals("Opened", publicContentStatusText.CLOSING_SOON.getOpenTense());
    }
}