package org.innovateuk.ifs.starters.stubdev.util;

import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

public class ITemplateResolverBuilder {
    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;
    @Autowired
    private ApplicationContext applicationContext;

    public ITemplateResolver buildITemplate(String name, String templateDirectory) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setName(name);
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(stubDevConfigurationProperties.getProjectRootDirectory()
                + templateDirectory);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }

}
