package org.innovateuk.ifs.project.financereviewer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.SimpleUserResource;

import java.util.List;

public interface FinanceReviewerRestService {

    RestResult<List<SimpleUserResource>> findFinanceUsers();

    RestResult<Void> assignFinanceReviewerToProject(long userId, long projectId);

    RestResult<SimpleUserResource> findFinanceReviewerForProject(long projectId);
}
