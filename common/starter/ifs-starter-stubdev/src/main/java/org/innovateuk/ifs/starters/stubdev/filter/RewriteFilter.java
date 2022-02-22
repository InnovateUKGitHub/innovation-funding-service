package org.innovateuk.ifs.starters.stubdev.filter;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.starters.stubdev.cfg.RewriteRule;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.innovateuk.ifs.starters.stubdev.security.StubUidSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * Heavy work around for the web tier wanting to all be on port 8080 for local non-container dev.
 *
 * Use a servlet filter to rewrite urls based on context. Url mappings are in the yml config.
 */
public class RewriteFilter implements Filter {

    @Autowired
    private StubUidSupplier stubUidSupplier;

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Stopwatch timer = Stopwatch.createStarted();
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);
        if (wrapper.getContentType() != null && wrapper.getContentType().contains(MimeTypeUtils.TEXT_HTML_VALUE)) {
            String alteredContent = wrapper.toString();
            for(RewriteRule rewriteRule : stubDevConfigurationProperties.getRewriteRules()) {
                alteredContent = alteredContent.replaceAll(rewriteRule.getExisting(), rewriteRule.getRewrite());
            }
            alteredContent = alteredContent.replaceAll("Innovation Funding Service",
                    "Stub IFS  as " + stubUidSupplier.getUid(null) + " in " + timer.stop());
            response.setContentLength(alteredContent.length());
            PrintWriter responseWriter = response.getWriter();
            responseWriter.write(alteredContent);
        }
    }

}

