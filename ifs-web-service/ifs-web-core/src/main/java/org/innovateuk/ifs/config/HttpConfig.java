package org.innovateuk.ifs.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {

    @Value("${ifs.web.rest.connections.max.total}")
    int connectionsMaxTotal;

    @Value("${ifs.web.rest.connections.max.per.route}")
    int connectionsMaxPerRoute;

    @Value("${ifs.web.rest.connections.timeout.millis}")
    int connectionsTimeoutMillis;

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public RestTemplate restTemplate() {
        return configureRestTemplate(new RestTemplate(httpRequestFactory()));
    }

    private RestTemplate configureRestTemplate(RestTemplate restTemplate) {
        restTemplate.getMessageConverters().stream().filter(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName())).forEach(m -> {
            ((MappingJackson2HttpMessageConverter) m).getObjectMapper().registerModule(new JacksonZoneDateDeserializerModule());
        });
        return restTemplate;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(connectionsMaxTotal);
        connectionManager.setDefaultMaxPerRoute(connectionsMaxPerRoute);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionsTimeoutMillis).build();
        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return defaultHttpClient;
    }

    @Bean
    public AsyncClientHttpRequestFactory asyncHttpRequestFactory() {
        return new HttpComponentsAsyncClientHttpRequestFactory(
                asyncHttpClient());
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate(asyncHttpRequestFactory(), restTemplate());
    }

    @Bean
    public CloseableHttpAsyncClient asyncHttpClient() {
        try {
            PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
            connectionManager.setMaxTotal(connectionsTimeoutMillis);
            connectionManager
                    .setDefaultMaxPerRoute(connectionsMaxPerRoute);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(connectionsTimeoutMillis)
                    .build();
            CloseableHttpAsyncClient httpclient = HttpAsyncClientBuilder
                    .create().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(config).build();
            return httpclient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
