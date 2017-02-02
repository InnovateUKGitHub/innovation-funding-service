package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.PostRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;

public class ProjectFinanceQueriesServiceImpl implements ProjectFinanceQueriesService {
    @Autowired
    QueryRepository queryRepository;

    public final ServiceResult<Query> query(Long projectFinanceId) {
        return find(queryRepository.findByClassPkAndClassName(projectFinanceId, ProjectFinance.class.getName()),
                    notFoundError(Query.class, projectFinanceId));
    }
    public final ServiceResult<Void> createQuery(Query query) {
        queryRepository.save(query);
        return serviceSuccess();
    }

    public final ServiceResult<Void> addPost(Post post, Long projectFinanceId) {
        return query(projectFinanceId).andOnSuccessReturn(query -> {
            query.addPost((post));
            return queryRepository.save(query);
        }).andOnSuccessReturnVoid();
    }
}
