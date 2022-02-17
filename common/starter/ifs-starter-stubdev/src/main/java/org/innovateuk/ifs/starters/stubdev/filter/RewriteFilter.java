package org.innovateuk.ifs.starters.stubdev.filter;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * Work around the web tier wanting to all be on port 8080 for local non-container dev.
 *
 * Use a servlet filter to rewrite urls
 */
public class RewriteFilter implements Filter {

    ImmutableMap<String, String> replacements = ImmutableMap.of(
        "https://localhost:8080/assessment/", "http://localhost:8081/assessment/",
        "https://localhost:8080/management/", "http://localhost:8082/management/",
        "https://localhost:8080/competition/", "http://localhost:8083/competition/",
        "https://localhost:8080/project-setup-management/", "http://localhost:8084/project-setup-management/",
        "https://localhost:8080/project-setup/", "http://localhost:8085/project-setup/",
        "https://localhost:8080/survey/", "http://localhost:8086/survey/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Stopwatch timer = Stopwatch.createStarted();
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);
        if (wrapper.getContentType() != null && wrapper.getContentType().contains("text/html")) {
            String alteredContent = wrapper.toString();
            for(Map.Entry<String, String> entry : replacements.entrySet()) {
                alteredContent = alteredContent.replaceAll(entry.getKey(), entry.getValue());
            }
            alteredContent = alteredContent.replaceAll("Innovation Funding Service", "STUB IFS " + timer.stop());
            response.setContentLength(alteredContent.length());
            PrintWriter responseWriter = response.getWriter();
            responseWriter.write(alteredContent);
        }
    }

}

