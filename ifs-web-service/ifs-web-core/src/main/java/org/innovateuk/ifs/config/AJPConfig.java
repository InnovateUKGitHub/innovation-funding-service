package org.innovateuk.ifs.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
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
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if(tomcatAjpEnabled) {

                Connector ajpConnector = new Connector("AJP/1.3");
                ajpConnector.setPort(ajpPort);
                ajpConnector.setSecure(false);
                ajpConnector.setAllowTrace(false);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setTomcatAuthentication(false);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setConnectionTimeout(connectionTimeout);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setAcceptCount(acceptCount);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setMaxConnections(maxConnections);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setMaxThreads(maxThreads);
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setMinSpareThreads(20);
                ajpConnector.setScheme("ajp");
                ajpConnector.setProperty("address","0.0.0.0");
                ajpConnector.setProperty("allowedRequestAttributesPattern",".*");
                ((AbstractAjpProtocol)ajpConnector.getProtocolHandler()).setSecretRequired(false);

                server.addAdditionalTomcatConnectors(ajpConnector);
            }
        };
    }

}
