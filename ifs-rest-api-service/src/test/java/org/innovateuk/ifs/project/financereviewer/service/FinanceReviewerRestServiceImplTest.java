package org.innovateuk.ifs.project.financereviewer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.simpleUserListType;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class FinanceReviewerRestServiceImplTest extends BaseRestServiceUnitTest<FinanceReviewerRestServiceImpl> {

    @Test
    public void findFinanceUsers() {
        List<SimpleUserResource> expected = newSimpleUserResource().build(1);
        setupGetWithRestResultExpectations("/finance-reviewer/find-all", simpleUserListType(), expected, OK);

        RestResult<List<SimpleUserResource>> result = service.findFinanceUsers();

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Test
    public void assignFinanceReviewerToProject() {
        long userId = 1L;
        long projectId = 2L;
        setupPostWithRestResultExpectations(String.format("/finance-reviewer/%d/assign/%d", userId, projectId), OK);

        RestResult<Void> result = service.assignFinanceReviewerToProject(userId, projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void findFinanceReviewerForProject() {
        long projectId = 2L;
        SimpleUserResource expected = newSimpleUserResource().build();
        setupGetWithRestResultExpectations("/finance-reviewer?projectId=" + projectId, SimpleUserResource.class, expected, OK);

        RestResult<SimpleUserResource> result = service.findFinanceReviewerForProject(projectId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), expected);
    }

    @Override
    protected FinanceReviewerRestServiceImpl registerRestServiceUnderTest() {
        return new FinanceReviewerRestServiceImpl();
    }
}
