package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.Cost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * CostRestServiceImpl is a utility for CRUD operations on {@link Cost}.
 * This class connects to the {@link com.worth.ifs.finance.controller.CostController}
 * through a REST call.
 */
@Service
public class CostRestServiceImpl extends BaseRestService implements CostRestService {
    private final Log log = LogFactory.getLog(getClass());

    @Value("${ifs.data.service.rest.cost}")
    String costRestURL;

    @Override
    public void add(Long applicationFinanceId, Long questionId) {
        restPut(costRestURL + "/add/" + applicationFinanceId + "/" + questionId);
    }

    @Override
    public List<Cost> getCosts(Long applicationFinanceId) {
        return asList(restGet(costRestURL + "/get/"+applicationFinanceId, Cost[].class));
    }

    @Override
    public void update(Cost cost) {
        restPut(costRestURL + "/update/" + cost.getId(), cost);
    }

    @Override
    public void delete(Long costId) {
        restDelete(costRestURL + "/delete/"+costId);
    }

    @Override
    public Cost findById(Long id) {
        return restGet(costRestURL + "/findById/" + id, Cost.class);
    }
}
