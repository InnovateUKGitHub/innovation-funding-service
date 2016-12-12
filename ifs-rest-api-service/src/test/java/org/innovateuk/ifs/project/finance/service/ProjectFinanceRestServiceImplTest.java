package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class ProjectFinanceRestServiceImplTest extends BaseRestServiceUnitTest<ProjectFinanceRestServiceImpl> {

    private static final String projectFinanceRestURL = "/project";

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

    @Test
    public void saveSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile",
                table,
                OK);

        RestResult<Void> result = service.saveSpendProfile(projectId, organisationId,  table);

        assertTrue(result.isSuccess());

    }

    @Test
    public void markSpendProfileComplete() {

        Long projectId = 1L;
        Long organisationId = 1L;

        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/complete",
                OK);

        RestResult<Void> result = service.markSpendProfileComplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markSpendProfileIncomplete() {

        Long projectId = 1L;
        Long organisationId = 1L;

        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/incomplete",
                OK);

        RestResult<Void> result = service.markSpendProfileIncomplete(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testCompleteSpendProfilesReview() {

        Long projectId = 1L;

        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/complete-spend-profiles-review/",
                OK);

        RestResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileCSV() {

        Long projectId = 1L;
        Long organisationId = 1L;

        setupGetWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-csv", SpendProfileCSVResource.class, null);

        RestResult<SpendProfileCSVResource> result = service.getSpendProfileCSV(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void acceptOrRejectSpendProfile() {
        Long projectId = 1L;
        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/spend-profile/approval/" + ApprovalType.APPROVED,
                OK);

        RestResult<Void> result = service.acceptOrRejectSpendProfile(projectId, ApprovalType.APPROVED);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectId() {
        Long projectId = 1L;

        setupGetWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/spend-profile/approval",
                ApprovalType.class,
                ApprovalType.APPROVED,
                OK);

        RestResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        Assert.assertEquals(ApprovalType.APPROVED, result.getSuccessObject());
    }
}
