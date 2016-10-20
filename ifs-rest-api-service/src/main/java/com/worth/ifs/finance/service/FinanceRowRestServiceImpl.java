package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.costItemListType;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on {@link FinanceRow}.
 * This class connects to the {@link com.worth.ifs.finance.controller.FinanceRowController}
 * through a REST call.
 */
@Service
public class FinanceRowRestServiceImpl extends BaseRestService implements FinanceRowRestService {

    private String costRestURL = "/cost";

    @Override
    public RestResult<ValidationMessages> add(Long applicationFinanceId, Long questionId, FinanceRowItem costItem) {
        return postWithRestResult(costRestURL + "/add/" + applicationFinanceId + "/" + questionId, costItem, ValidationMessages.class);
    }
    
    @Override
    public RestResult<FinanceRowItem> addWithoutPersisting(Long applicationFinanceId, Long questionId) {
        return postWithRestResult(costRestURL + "/add-without-persisting/" + applicationFinanceId + "/" + questionId, FinanceRowItem.class);
    }

    @Override
    public RestResult<List<FinanceRowItem>> getCosts(Long applicationFinanceId) {
        return getWithRestResult(costRestURL + "/get/" + applicationFinanceId, costItemListType());
    }

    @Override
    public RestResult<ValidationMessages> update(FinanceRowItem costItem) {
        return putWithRestResult(costRestURL + "/update/" + costItem.getId(), costItem, ValidationMessages.class);
    }

    @Override
    public RestResult<Void> delete(Long costId) {
        return deleteWithRestResult(costRestURL + "/delete/" + costId);
    }

    @Override
    public RestResult<FinanceRowItem> findById(Long id) {
        return getWithRestResult(costRestURL + "/" + id, FinanceRowItem.class);
    }
}
