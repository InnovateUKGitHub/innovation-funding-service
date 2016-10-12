package com.worth.ifs.notifications.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
/**
 *
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfiguration {

    @Bean
    public static Configuration freemarkerConfiguration() throws IOException, URISyntaxException {

        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.22) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setDirectoryForTemplateLoading(new File(Thread.currentThread().getContextClassLoader().getResource("ftltemplates").toURI()));

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        cfg.setLocale(java.util.Locale.UK);
        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return cfg;
    }
}
