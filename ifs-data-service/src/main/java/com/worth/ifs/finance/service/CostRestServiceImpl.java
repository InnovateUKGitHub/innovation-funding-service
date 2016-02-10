package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
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
    public void add(Long applicationFinanceId, Long questionId, CostItem costItem) {
        restPut(costRestURL + "/add/" + applicationFinanceId + "/" + questionId, costItem);
    }

    @Override
    public List<CostItem> getCosts(Long applicationFinanceId) {
        return asList(restGet(costRestURL + "/get/"+applicationFinanceId, CostItem[].class));
    }

    @Override
    public void update(CostItem costItem) {
        restPut(costRestURL + "/update/" + costItem.getId(), costItem);
    }

    @Override
    public void delete(Long costId) {
        restDelete(costRestURL + "/delete/"+costId);
    }

    @Override
    public CostItem findById(Long id) {
        return restGet(costRestURL + "/findById/" + id, CostItem.class);
    }
}
