package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class CostService extends BaseServiceProvider {

    @Value("${ifs.data.service.rest.cost}")
    String costRestURL;

    public void addAnother(Long costCategoryId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + costRestURL + "/addAnother/"+costCategoryId, costCategoryId);
    }
}
