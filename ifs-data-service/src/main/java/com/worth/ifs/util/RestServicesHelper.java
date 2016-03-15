package com.worth.ifs.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

public class RestServicesHelper {
    public RestTemplate hateoasRestTemplate(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.findAndRegisterModules();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);
        return new RestTemplate(singletonList(converter));
    }

    public <T> T getResources(String url, ParameterizedTypeReference<T> responseType){
        ResponseEntity<T> userResourceEntities = hateoasRestTemplate().exchange(url, HttpMethod.GET, null, responseType);
        T userResources =userResourceEntities.getBody();
        return userResources;
    }
}