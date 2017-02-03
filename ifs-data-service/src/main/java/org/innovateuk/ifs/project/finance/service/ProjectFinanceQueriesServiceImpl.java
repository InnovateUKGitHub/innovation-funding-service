package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectFinanceQueriesServiceImpl extends MappingThreadService<Query, QueryResource, QueryMapper, ProjectFinance>
        implements ProjectFinanceQueriesService {

    @Autowired
    public ProjectFinanceQueriesServiceImpl(QueryRepository queryRepository, QueryMapper queryMapper, PostMapper postMapper) {
        super(queryRepository, queryMapper, postMapper, ProjectFinance.class);
    }
}