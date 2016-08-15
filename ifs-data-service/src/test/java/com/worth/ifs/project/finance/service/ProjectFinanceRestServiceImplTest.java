package com.worth.ifs.project.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import org.junit.Test;

import static org.springframework.http.HttpStatus.CREATED;

public class ProjectFinanceRestServiceImplTest extends BaseRestServiceUnitTest<ProjectFinanceRestServiceImpl> {

    @Override
    protected ProjectFinanceRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectFinanceRestServiceImpl();
    }

    @Test
    public void test() {

        setupPostWithRestResultExpectations("/project/123/spend-profile/generate", Void.class, null, null, CREATED);
        service.generateSpendProfile(123L);
        setupPostWithRestResultVerifications("/project/123/spend-profile/generate", Void.class, null);
    }
}
