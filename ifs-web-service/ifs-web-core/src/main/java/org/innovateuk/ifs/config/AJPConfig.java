package org.innovateuk.ifs.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AJPConfig {
    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

    @Value("${ifs.web.rest.connections.max.total}")
    int maxConnections;

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
            ajpConnector.setAttribute("connectionTimeout", 30000);
            ajpConnector.setAttribute("acceptCount", maxConnections);
            ajpConnector.setAttribute("maxConnections", maxConnections);
            ajpConnector.setAttribute("maxThreads", maxConnections);
            ajpConnector.setAttribute("minSpareThreads", 20);
            ajpConnector.setScheme("ajp");
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        }

        return tomcat;
    }
}
