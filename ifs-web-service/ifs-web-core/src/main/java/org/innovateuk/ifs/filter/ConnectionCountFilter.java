package org.innovateuk.ifs.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

/**
 * This class is used for keeping track of the incoming connections.
 */

@Service
public class ConnectionCountFilter extends GenericFilterBean {
    private static final Log LOG = LogFactory.getLog(ConnectionCountFilter.class);

    private AtomicInteger count = new AtomicInteger(0);
    @Value("${ifs.web.rest.connections.max.total}")
    private int max;


    @Override public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
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
