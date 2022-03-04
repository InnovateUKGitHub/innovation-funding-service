package org.innovateuk.ifs.starters.stubdev.filter;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.cfg.RewriteRule;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.innovateuk.ifs.starters.stubdev.security.StubUidSupplier;
import org.innovateuk.ifs.starters.stubdev.security.StubUserSwitchController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(IfsProfileConstants.STUBDEV)
@SpringBootTest(classes = {RewriteFilter.class, StubUidSupplier.class, StubUserSwitchController.class})
@EnableConfigurationProperties(StubDevConfigurationProperties.class)
class RewriteFilterTest {

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @Autowired
    private StubUidSupplier stubUidSupplier;

    @Autowired
    private RewriteFilter rewriteFilter;

    @Autowired
    private StubUserSwitchController stubUserSwitchController;

    @Test
    void testRewriteFilter() throws ServletException, IOException {
        String uuid = UUID.randomUUID().toString();
        stubDevConfigurationProperties.setDefaultUuid(uuid);
        stubUidSupplier.init();
        stubUserSwitchController.setUser(uuid);
        ServletResponse servletResponse = new MockHttpServletResponse();
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) servletResponse);
        rewriteFilter.doFilter(new MockHttpServletRequest(), wrapper, new FakeResponseFilterChain());
        String rewrittenContent = wrapper.toString();
        for (RewriteRule rewriteRule : stubDevConfigurationProperties.getRewriteRules()) {
            assertThat(rewrittenContent.contains(rewriteRule.getRewrite()), equalTo(true));
            assertThat(rewrittenContent.contains(rewriteRule.getExisting()), equalTo(false));
        }
        assertThat(rewrittenContent.contains("Stub IFS"), equalTo(true));
        assertThat(rewrittenContent.contains(uuid), equalTo(true));
        assertThat(rewrittenContent.contains("Innovation Funding Service"), equalTo(false));
    }

    class FakeResponseFilterChain extends MockFilterChain {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            super.doFilter(request, response);
            getResponse().setContentType(MimeTypeUtils.TEXT_HTML_VALUE);
            response.getWriter().write("<html><h1>Innovation Funding Service<h2>");
            for (RewriteRule rewriteRule : stubDevConfigurationProperties.getRewriteRules()) {
                response.getWriter().write("<a href=\"" + rewriteRule.getExisting() + "\">Linky</a>");
            }
        }
    }

}