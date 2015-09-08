package com.worth.ifs.service;

import com.worth.ifs.domain.ApplicationFinance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
}
