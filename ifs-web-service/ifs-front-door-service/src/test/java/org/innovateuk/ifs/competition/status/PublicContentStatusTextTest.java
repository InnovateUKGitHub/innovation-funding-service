package org.innovateuk.ifs.competition.status;

import org.junit.Assert;
import org.junit.Test;

public class PublicContentStatusTextTest {

    PublicContentStatusText publicContentStatusText;

    @Test
    public void getPredicate() throws Exception {
    }

    @Test
    public void getHeader() throws Exception {
        String result = publicContentStatusText.CLOSING_SOON.getHeader();
        Assert.assertEquals( "Closing soon", result);
    }

    @Test
    public void getOpenTense() throws Exception {
        String result = publicContentStatusText.CLOSING_SOON.getOpenTense();
        Assert.assertEquals( "Opened", result);
    }
}