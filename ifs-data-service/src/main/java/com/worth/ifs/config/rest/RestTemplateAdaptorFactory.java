package com.worth.ifs.config.rest;

import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.service.HttpHeadersUtils.getJSONHeaders;

@Component
public class RestTemplateAdaptorFactory {

    @Value("${idp.rest.key}")
    private String shibbolethKey = null;

    @Value("${ifs.data.company-house.key}")
    private String companyhouseKey = null;

    @Bean(autowire = Autowire.BY_TYPE)
    @Qualifier("shibboleth_adaptor")
    public AbstractRestTemplateAdaptor shibbolethAdaptor(){
        return new AbstractRestTemplateAdaptor(){
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = getJSONHeaders();
                headers.add("api-key", shibbolethKey);
                return headers;
            }
        };
    }


    @Bean(autowire = Autowire.BY_TYPE)
    @Qualifier("companyhouse_adaptor")
    public AbstractRestTemplateAdaptor companyHouseAdaptor(){
        return new AbstractRestTemplateAdaptor(){
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = getJSONHeaders();
                String auth = companyhouseKey + ":";
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                headers.add("Authorization", authHeader);
                return headers;
            }
        };
    }

    @Bean(autowire = Autowire.BY_TYPE)
    @Qualifier("sil_adaptor")
    public AbstractRestTemplateAdaptor silAdaptor(){
        return new AbstractRestTemplateAdaptor() {
            @Override
            public HttpHeaders getHeaders() {
                return getJSONHeaders();
            }
        };
    }
}
