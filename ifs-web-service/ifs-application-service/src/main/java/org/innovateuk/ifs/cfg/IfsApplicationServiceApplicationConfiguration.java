package org.innovateuk.ifs.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.client.RestTemplate;

import static org.innovateuk.ifs.virtualassistant.VirtualAssistantAuthRestClient.AZURE_CHAT_BOT_REST_TEMPLATE_QUALIFIER;

@Configuration
public class IfsApplicationServiceApplicationConfiguration {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean(AZURE_CHAT_BOT_REST_TEMPLATE_QUALIFIER)
    public RestTemplate azureChatBotRestTemplate() {
        return restTemplateBuilder.build(RestTemplate.class);
    }

    @Bean
    public static ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

}
