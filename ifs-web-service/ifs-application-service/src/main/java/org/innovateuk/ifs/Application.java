package org.innovateuk.ifs;

import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = { "classpath:/application.properties", "classpath:/applicationservice.properties" })
@SpringBootApplication(exclude=org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class Application extends SpringBootServletInitializer {

    private static final Log LOG = LogFactory.getLog(Application.class);

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
            ajpConnector.setAttribute("acceptCount", maxConnections+100);
            ajpConnector.setAttribute("maxConnections", maxConnections);
            ajpConnector.setAttribute("maxThreads", 100);
            ajpConnector.setAttribute("minSpareThreads", 20);
            ajpConnector.setScheme("ajp");
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        }

        return tomcat;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        LOG.info("IFS Application builder configure method");
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        LOG.info("IFS boot Application main method");
        SpringApplication.run(Application.class, args);
    }
}
