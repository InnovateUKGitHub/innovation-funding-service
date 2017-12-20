package org.innovateuk.ifs.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@Component
public class ConnectionCountFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(ConnectionCountFilter.class);

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

        LOG.debug("incoming connection used = " + count + "/" + max + " healthy = "+ healthy);

        return healthy;
    }
}
