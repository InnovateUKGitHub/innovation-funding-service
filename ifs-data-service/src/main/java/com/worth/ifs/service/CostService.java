package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Cost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CostService extends BaseServiceProvider {

    @Value("${ifs.data.service.rest.cost}")
    String costRestURL;

    public void add(Long applicationFinanceId, Long questionId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + costRestURL + "/add/"+applicationFinanceId + "/" + questionId, applicationFinanceId, questionId);
    }

    public List<Cost> getCosts(Long applicationFinanceId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Cost[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + costRestURL + "/get/"+applicationFinanceId, Cost[].class);
        Cost[] costs = responseEntity.getBody();
        return Arrays.asList(costs);
    }

    public void update(Cost cost) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + costRestURL + "/update/" +cost.getId();
        restTemplate.put(url, cost.getId());
    }
}
