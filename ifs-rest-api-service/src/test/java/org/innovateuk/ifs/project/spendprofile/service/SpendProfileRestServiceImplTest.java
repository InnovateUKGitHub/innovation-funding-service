package org.innovateuk.ifs.project.spendprofile.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class SpendProfileRestServiceImplTest extends BaseRestServiceUnitTest<SpendProfileRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected SpendProfileRestServiceImpl registerRestServiceUnderTest() {
        return new SpendProfileRestServiceImpl();
    }

    @Test
    public void testGenerateSpendProfile() {

        setupPostWithRestResultExpectations("/project/123/spend-profile/generate", Void.class, null, null, CREATED);
        service.generateSpendProfile(123L);
        setupPostWithRestResultVerifications("/project/123/spend-profile/generate", Void.class, null);
    }

    @Test
    public void saveSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 2L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile",
                table,
                OK);

        RestResult<Void> result = service.saveSpendProfile(projectId, organisationId, table);
        setupPostWithRestResultVerifications("/project/1/partner-organisation/2/spend-profile", Void.class, table);


        assertTrue(result.isSuccess());

    }

    @Test
    public void markSpendProfileComplete() {

        Long projectId = 1L;
        Long organisationId = 2L;

        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/complete",
                OK);

        RestResult<Void> result = service.markSpendProfileComplete(projectId, organisationId);
        setupPostWithRestResultVerifications("/project/1/partner-organisation/2/spend-profile/complete", Void.class, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markSpendProfileIncomplete() {

        Long projectId = 1L;
        Long organisationId = 2L;

        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/incomplete",
                OK);

        RestResult<Void> result = service.markSpendProfileIncomplete(projectId, organisationId);
        setupPostWithRestResultVerifications("/project/1/partner-organisation/2/spend-profile/incomplete", Void.class, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testCompleteSpendProfilesReview() {

        Long projectId = 1L;

        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/complete-spend-profiles-review/",
                OK);

        RestResult<Void> result = service.completeSpendProfilesReview(projectId);

        setupPostWithRestResultVerifications("/project/1/complete-spend-profiles-review/", Void.class, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileCSV() {

        Long projectId = 1L;
        Long organisationId = 1L;

        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile-csv", SpendProfileCSVResource.class, null);

        RestResult<SpendProfileCSVResource> result = service.getSpendProfileCSV(projectId, organisationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void acceptOrRejectSpendProfile() {
        Long projectId = 1L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/spend-profile/approval/" + ApprovalType.APPROVED,
                OK);

        RestResult<Void> result = service.acceptOrRejectSpendProfile(projectId, ApprovalType.APPROVED);
        setupPostWithRestResultVerifications("/project/1/spend-profile/approval/APPROVED", Void.class, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectId() {
        Long projectId = 1L;

        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/spend-profile/approval",
                ApprovalType.class,
                ApprovalType.APPROVED,
                OK);

        RestResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.APPROVED, result.getSuccessObject());
    }
}
