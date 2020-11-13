package org.innovateuk.ifs.virtualassistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class VirtualAssistantRestClient {

    private final Logger LOG = LoggerFactory.getLogger(VirtualAssistantRestClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ifs.virtualAssistant.tokenExchangeUrl:undefined}")
    private String tokenExchangeUrl;

    @Value("${ifs.virtualAssistant.botId:undefined}")
    private String botId;

    @Value("${ifs.virtualAssistant.botSecret}")
    private String botSecret;

    public VirtualAssistantModel obtainVirtualAssistantDetails() {
        LOG.debug(botId);
        LOG.debug(tokenExchangeUrl);
        try {
            HttpEntity<String> request = new HttpEntity<>(authHeader());
            ResponseEntity<String> response = restTemplate.exchange(tokenExchangeUrl, HttpMethod.GET, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOG.error("Status " + response.getStatusCode());
                return new VirtualAssistantModel(response.getStatusCode().toString());
            }
            return new VirtualAssistantModel(botId, response.getBody());
        } catch (HttpClientErrorException ex) {
            LOG.error("Failed to obtain virtual assistant token", ex);
            return new VirtualAssistantModel(ex.getMessage());
        }
    }

    private HttpHeaders authHeader(){
        return new HttpHeaders() {{
            set( "Authorization", "BotConnector " + botSecret);
        }};
    }

}
