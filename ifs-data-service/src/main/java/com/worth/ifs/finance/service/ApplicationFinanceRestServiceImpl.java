package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * ApplicationFinanceRestServiceImpl is a utility for CRUD operations on {@link ApplicationFinance}.
 * This class connects to the {@link com.worth.ifs.finance.controller.ApplicationFinanceController}
 * through a REST call.
 */
@Service
public class ApplicationFinanceRestServiceImpl extends BaseRestService implements ApplicationFinanceRestService {
    @Value("${ifs.data.service.rest.applicationfinance}")
    String applicationFinanceRestURL;

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null){
            return null;
        }
        return restGet(applicationFinanceRestURL + "/findByApplicationOrganisation/" + applicationId + "/" + organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationFinances(Long applicationId) {
        if(applicationId == null) {
            return null;
        }

        return asList(restGet(applicationFinanceRestURL + "/findByApplication/" + applicationId, ApplicationFinanceResource[].class));
    }

    @Override
    public ApplicationFinanceResource addApplicationFinanceForOrganisation(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null) {
            return null;
        }
        return restPost(applicationFinanceRestURL + "/add/" + applicationId + "/" + organisationId, null, ApplicationFinanceResource.class);
    }

    @Override
    public ApplicationFinanceResource update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance){
        return restPost(applicationFinanceRestURL + "/update/"+ applicationFinanceId, applicationFinance, ApplicationFinanceResource.class);
    }

    @Override
    public ApplicationFinanceResource getById(Long applicationFinanceId){
        return restGet(applicationFinanceRestURL + "/getById/" + applicationFinanceId, ApplicationFinanceResource.class);
    }

    @Override
    public ApplicationFinanceResource getFinanceDetails(Long applicationId, Long organisationId) {
        return restGet(applicationFinanceRestURL + "/financeDetails/" + applicationId + "/"+organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public List<ApplicationFinanceResource> getFinanceTotals(Long applicationId) {
        return asList(restGet(applicationFinanceRestURL + "/financeTotals/" + applicationId, ApplicationFinanceResource[].class));
    }
}
