package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.costItemListType;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
 */
@Service
public class ProjectFinanceRowRestServiceImpl extends BaseRestService implements ProjectFinanceRowRestService {

    private String costRestURL = "/cost/project";

    @Override
    public RestResult<ValidationMessages> add(Long projectFinanceId, Long questionId, FinanceRowItem costItem) {
        return postWithRestResult(costRestURL + "/add/" + projectFinanceId + "/" + questionId, costItem, ValidationMessages.class);
    }

    @Override
    public RestResult<ValidationMessages> update(FinanceRowItem costItem) {
        return putWithRestResult(costRestURL + "/update/" + costItem.getId(), costItem, ValidationMessages.class);
    }

    @Override
    public RestResult<Void> delete(Long projectId, Long organisationId, Long costId) {
        return deleteWithRestResult(costRestURL + "/" + projectId + "/organisation/" + organisationId + "/delete/" + costId);
    }

    @Override
    public RestResult<FinanceRowItem> addWithoutPersisting(Long projectFinanceId, Long questionId) {
        return postWithRestResult(costRestURL + "/add-without-persisting/" + projectFinanceId + "/" + questionId, FinanceRowItem.class);
    }

    @Override
    public RestResult<List<FinanceRowItem>> getCosts(Long projectFinanceId) {
        return getWithRestResult(costRestURL + "/get/" + projectFinanceId, costItemListType());
    }

    @Override
    public RestResult<FinanceRowItem> findById(Long id) {
        return getWithRestResult(costRestURL + "/" + id, FinanceRowItem.class);
    }
}
