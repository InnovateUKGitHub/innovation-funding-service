package com.worth.ifs.service;

import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.resource.ApplicationFinanceResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationFinanceService extends BaseServiceProvider {
    @Value("${ifs.data.service.rest.applicationfinance}")
    String applicationFinanceRestURL;

    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long organisationId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(dataRestServiceURL + applicationFinanceRestURL + "/findByApplicationOrganisation/" + applicationId + "/" + organisationId, ApplicationFinanceResource.class);
    }
}
