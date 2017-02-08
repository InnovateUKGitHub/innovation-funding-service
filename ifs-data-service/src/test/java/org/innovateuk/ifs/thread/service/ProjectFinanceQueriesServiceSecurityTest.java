package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesService;
import org.innovateuk.ifs.threads.security.QueryPermissionRules;
import org.junit.Before;

public class ProjectFinanceQueriesServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceQueriesService> {

    private QueryPermissionRules queryRules;

    @Override
    protected Class<? extends ProjectFinanceQueriesService> getClassUnderTest() {
        return ProjectFinanceQueriesService.class;
    }

    @Before
    public void lookupPermissionRules() {
        queryRules = getMockPermissionRulesBean(QueryPermissionRules.class);
    }


}