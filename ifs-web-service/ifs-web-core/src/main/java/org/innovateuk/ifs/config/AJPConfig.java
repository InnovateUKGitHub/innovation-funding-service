package org.innovateuk.ifs.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * this class is for configuring the AJP connector on the webservices
 */

@Configuration
public class AJPConfig {
    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    @Value("${ifs.web.ajp.connections.timeout}")
    int connectionTimeout;

    @Value("${ifs.web.ajp.connections.max.total}")
    int maxConnections;

    @Value("${ifs.web.ajp.connections.accept.count}")
    int acceptCount;

    @Value("${ifs.web.ajp.connections.max.threads}")
    int maxThreads;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        if (tomcatAjpEnabled)
        {
            Connector ajpConnector = new Connector("AJP/1.3");
            ajpConnector.setProtocol("AJP/1.3");
            ajpConnector.setPort(ajpPort);
            ajpConnector.setSecure(false);
            ajpConnector.setAllowTrace(false);
            ajpConnector.setAttribute("tomcatAuthentication", false);
            ajpConnector.setAttribute("connectionTimeout", connectionTimeout);
            ajpConnector.setAttribute("acceptCount", acceptCount);
            ajpConnector.setAttribute("maxConnections", maxConnections);
            ajpConnector.setAttribute("maxThreads", maxThreads);
            ajpConnector.setAttribute("minSpareThreads", 20);
            ajpConnector.setScheme("ajp");
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        }

        return tomcat;
    }
}
