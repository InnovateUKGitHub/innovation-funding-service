package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.costItemListType;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
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
