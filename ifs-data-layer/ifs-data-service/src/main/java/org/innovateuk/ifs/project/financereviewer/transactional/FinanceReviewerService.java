package org.innovateuk.ifs.project.financereviewer.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.SimpleUserResource;

import java.util.List;

public interface FinanceReviewerService {

    ServiceResult<Long> assignFinanceReviewer(long projectId, long financeReviewerUserId);

    ServiceResult<List<SimpleUserResource>> findFinanceUsers();

    ServiceResult<SimpleUserResource> getFinanceReviewerForProject(long projectId);
}
