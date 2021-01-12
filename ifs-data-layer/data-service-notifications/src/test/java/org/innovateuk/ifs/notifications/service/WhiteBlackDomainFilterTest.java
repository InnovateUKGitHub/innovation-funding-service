package org.innovateuk.ifs.notifications.service;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.innovateuk.ifs.notifications.service.WhiteBlackDomainFilter.passesFilterCheck;

public class WhiteBlackDomainFilterTest {

    private List EMPTY = ImmutableList.of();

    @Test
    public void testBadInput() {
        assertFail(EMPTY, EMPTY, null);
        assertFail(EMPTY, EMPTY, "foo%example.com");
        assertFail(EMPTY, EMPTY, "fffff@");
        assertFail(EMPTY, EMPTY, "@");
        assertFail(EMPTY, EMPTY, "fffff@ddddd@ddddd");
    }

    @Test
    public void testBlacklist() {
        assertFail(EMPTY, ImmutableList.of("example.com"),"foo@example.com");
        assertPass(EMPTY, ImmutableList.of("example.com"),"foo@example.org");
        assertFail(EMPTY, ImmutableList.of("example.com"),"foo@subdomain.example.com");
    }

    @Test
    public void testWhiteList() {
        assertPass(EMPTY, EMPTY,"foo@ok.com");
        assertPass(ImmutableList.of("ok.com"), EMPTY,"foo@ok.com");
        assertFail(ImmutableList.of("ok.com"), EMPTY,"foo@ok.org");
        assertPass(ImmutableList.of("ok.com"), EMPTY,"foo@subdomain.ok.com");
    }

    private void assertPass(List white, List black, String email) {
        assertPassOrFail(white, black, email, true);
    }

    private void assertFail(List white, List black, String email) {
        assertPassOrFail(white, black, email, false);
    }

    private void assertPassOrFail(List white, List black, String email, boolean passOrFail) {
        assertThat(passesFilterCheck(white, black, email), equalTo(passOrFail));
    }

}