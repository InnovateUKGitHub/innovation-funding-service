package org.innovateuk.ifs.project.financereviewer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.simpleUserListType;

@Service
public class FinanceReviewerRestServiceImpl extends BaseRestService implements FinanceReviewerRestService {

    private static final String FINANCE_REVIEWER_REST_URL = "/finance-reviewer";

    @Override
    public RestResult<List<SimpleUserResource>> findFinanceUsers() {
        return getWithRestResult(format("%s/%s", FINANCE_REVIEWER_REST_URL, "find-all"), simpleUserListType());
    }

    @Override
    public RestResult<Void> assignFinanceReviewerToProject(long userId, long projectId) {
        return postWithRestResult(format("%s/%d/%s/%d", FINANCE_REVIEWER_REST_URL, userId, "assign", projectId));
    }

    @Override
    public RestResult<SimpleUserResource> findFinanceReviewerForProject(long projectId) {
        return getWithRestResult(String.format("%s?projectId=%d", FINANCE_REVIEWER_REST_URL, projectId), SimpleUserResource.class);
    }
}
