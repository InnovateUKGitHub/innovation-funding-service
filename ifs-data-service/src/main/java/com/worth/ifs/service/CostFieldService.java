package com.worth.ifs.service;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CostFieldService extends BaseServiceProvider{
    @Value("${ifs.data.service.rest.costfield}")
    String costFieldRestURL;

    public List<CostField> getCostFields() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CostField[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + costFieldRestURL + "/findAll/", CostField[].class);
        CostField[] costFields = responseEntity.getBody();
        return Arrays.asList(costFields);
    }
}
