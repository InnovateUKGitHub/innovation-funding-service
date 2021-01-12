package org.innovateuk.ifs.notifications.service;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {WhiteBlackDomainFilter.class},
        properties = {
                "ifs.emailNotification.blacklist=example.com,another.com",
                "ifs.emailNotification.whitelist=*"
        })
public class WhiteBlackDomainFilterComponentTest {

    @Autowired
    private WhiteBlackDomainFilter whiteBlackDomainFilter;

    @Test
    public void componentTestWhiteBlackDomainFilter() {
        // Uses class level injected values for properties - blacklist and WILDCARD whitelist
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@example.com"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@another.com"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@subdomain.another.com"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@ok.domain.com"), equalTo(true));

        // Ensure white lists entries pass unless they occur in a black list
        whiteBlackDomainFilter.setWhitelist(ImmutableList.of("example.com", "foo.com"));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@example.com"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@foo.com"), equalTo(true));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@subdomain.foo.com"), equalTo(true));

        // Bad input should fail
        assertThat(whiteBlackDomainFilter.passesFilterCheck(null), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("foo%example.com"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("fffff@"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("@"), equalTo(false));
        assertThat(whiteBlackDomainFilter.passesFilterCheck("fffff@ddddd@ddddd"), equalTo(false));

        // Empty white lists should fail
        whiteBlackDomainFilter.setWhitelist(ImmutableList.of());
        assertThat(whiteBlackDomainFilter.passesFilterCheck("test@foo.com"), equalTo(false));

    }

}