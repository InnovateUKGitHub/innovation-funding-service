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
 * This might be better a thymeleaf post processor TBF
 */

@Slf4j
public class RewriteFilter implements Filter {

    ImmutableMap<String, String> replacements = ImmutableMap.of(
//        "Innovation Funding Service", "STUB Innovation Funding Service Service",
        "https://localhost:8080/assessment/", "http://localhost:8082/assessment/",
            "https://localhost:8080/management/", "http://localhost:8083/management/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);

        if (wrapper.getContentType() != null &&
                wrapper.getContentType().contains("text/html")) {
            Stopwatch timer = Stopwatch.createStarted();
            String originalContent = wrapper.toString();
            String alteredContent = originalContent;
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

