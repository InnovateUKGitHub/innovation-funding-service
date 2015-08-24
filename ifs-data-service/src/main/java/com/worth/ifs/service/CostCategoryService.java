package com.worth.ifs.service;

import com.worth.ifs.domain.CostCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CostCategoryService extends BaseServiceProvider {

    @Value("${ifs.data.service.rest.costcategory}")
    String costCategoryRestURL;

    public List<CostCategory> getCostCategoriesByApplicationFinance(Long applicationFinanceId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CostCategory[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + costCategoryRestURL + "/findByApplicationFinance/" + applicationFinanceId, CostCategory[].class);
        CostCategory[] costCategories = responseEntity.getBody();
        return Arrays.asList(costCategories);
    }

}
