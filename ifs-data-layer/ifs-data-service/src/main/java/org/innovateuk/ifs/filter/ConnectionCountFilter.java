package org.innovateuk.ifs.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used for keeping track of incoming connections to the data layer.  With increased use of
 * parallelisation, the data layer potentially needs to handle a deal more incoming connections than the web
 * layer accepts.
 */
@Slf4j
@Component
public class ConnectionCountFilter extends OncePerRequestFilter {

    private AtomicInteger count = new AtomicInteger(0);

    @Value("${server.tomcat.max-connections}")
    private int max;

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            count.incrementAndGet();
            chain.doFilter(request, response);
        } finally {
            count.decrementAndGet();
        }
    }

    public boolean canAcceptConnection(){
        boolean healthy = max > count.intValue();

        log.trace("incoming connection used = " + count + "/" + max + " healthy = "+ healthy);

        return healthy;
    }
}
