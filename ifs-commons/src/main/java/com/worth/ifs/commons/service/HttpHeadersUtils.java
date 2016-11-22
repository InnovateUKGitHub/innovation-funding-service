package com.worth.ifs.commons.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;


public final class HttpHeadersUtils {
	
	private HttpHeadersUtils(){}
	
    public static HttpHeaders getJSONHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

    public static HttpHeaders getCSVHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        // Prevent caching
        httpHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        httpHeaders.setPragma("no-cache");
        httpHeaders.setExpires(0);
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        httpHeaders.add("Content-Transfer-Encoding", "binary");
        return httpHeaders;
    }
}
