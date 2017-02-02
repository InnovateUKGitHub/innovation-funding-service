package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;

public interface ProjectFinanceQueriesService {
    ServiceResult<Query> query(Long projectFinanceId);

    ServiceResult<Void> createQuery(Query query);

    ServiceResult<Void> addPost(Post post, Long projectFinanceId);
}
