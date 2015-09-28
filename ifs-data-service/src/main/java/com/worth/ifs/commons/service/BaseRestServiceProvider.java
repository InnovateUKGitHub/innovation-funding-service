package com.worth.ifs.commons.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * BaseRestServiceProvider provides a base for all Service classes.
 */

public class BaseRestServiceProvider {

    @Value("${ifs.data.service.rest.baseURL}")
    protected String dataRestServiceURL;

    /**
     * restGet is a generic method that performs a RESTful GET request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected  <T> T restGet(String path, Class c) {
        ResponseEntity<T> responseEntity = new RestTemplate().getForEntity(dataRestServiceURL + path , c);
        return responseEntity.getBody();
    }
    /**
     * restPost is a generic method that performs a RESTful POST request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected  <T> T restPost(String message, String path, Class c) {
        HttpEntity<String> entity = new HttpEntity<>(message, getJSONHeaders());
        ResponseEntity<T> response = new RestTemplate().exchange(dataRestServiceURL + path, HttpMethod.POST, entity, c);
        return response.getBody();
    }

    public static HttpHeaders getJSONHeaders() {
        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}

