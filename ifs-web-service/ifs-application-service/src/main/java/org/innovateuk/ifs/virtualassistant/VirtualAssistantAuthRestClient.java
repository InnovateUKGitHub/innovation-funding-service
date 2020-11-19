package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VirtualAssistantAuthRestClient {

    private final Logger LOG = LoggerFactory.getLogger(VirtualAssistantAuthRestClient.class);
    private static final String AUTH_BOT_CONNECTOR_HEADER = "BotConnector ";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ifs.web.virtualAssistant.tokenExchangeUrl}")
    private String tokenExchangeUrl;

    @Value("${ifs.web.virtualAssistant.botId}")
    private String botId;

    @Value("${ifs.web.virtualAssistant.botSecret}")
    private String botSecret;

    public VirtualAssistantModel obtainVirtualAssistantAuthDetails() {
        LOG.debug(botId);
        LOG.debug(tokenExchangeUrl);
        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenExchangeUrl,
                    HttpMethod.GET, new HttpEntity<>(authHeader()), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOG.error("Status " + response.getStatusCode());
                return new VirtualAssistantModel(response.getStatusCode().toString());
            }
            return new VirtualAssistantModel(botId, JsonUtil.getObjectFromJson(response.getBody(), String.class));
        } catch (Exception ex) {
            LOG.error("Failed to obtain virtual assistant token", ex);
            return new VirtualAssistantModel(ex.getMessage());
        }
    }

    protected HttpHeaders authHeader(){
        return new HttpHeaders() {{
            set(HttpHeaders.AUTHORIZATION, AUTH_BOT_CONNECTOR_HEADER + botSecret);
        }};
    }

}
