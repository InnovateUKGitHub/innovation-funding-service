package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectFinanceQueriesServiceImpl extends ThreadService<Query, ProjectFinance> {

    @Autowired
    public ProjectFinanceQueriesServiceImpl(QueryRepository queryRepository) {
        super(queryRepository, ProjectFinance.class);
    }
}