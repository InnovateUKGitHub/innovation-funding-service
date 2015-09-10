package com.worth.ifs.service;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ApplicationFinanceService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.applicationfinance}")
    String applicationFinanceRestURL;

    public ApplicationFinance getApplicationFinance(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null){
            return null;
        }
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(dataRestServiceURL + applicationFinanceRestURL + "/findByApplicationOrganisation/" + applicationId + "/" + organisationId, ApplicationFinance.class);
    }

    public List<ApplicationFinance> getApplicationFinances(Long applicationId) {
        if(applicationId == null) {
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApplicationFinance[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + applicationFinanceRestURL + "/findByApplication/" + applicationId, ApplicationFinance[].class);
        ApplicationFinance[] applicationFinances =  responseEntity.getBody();
        return Arrays.asList(applicationFinances);
    }

    public ApplicationFinance addApplicationFinanceForOrganisation(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null) {
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + applicationFinanceRestURL + "/add/" + applicationId + "/" + organisationId;

        ResponseEntity<ApplicationFinance> responseEntity = restTemplate.exchange(url, HttpMethod.POST, null, ApplicationFinance.class);
        return responseEntity.getBody();
    }
}
