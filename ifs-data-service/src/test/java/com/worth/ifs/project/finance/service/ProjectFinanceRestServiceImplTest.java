package com.worth.ifs.project.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
    public void markSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;
        Boolean complete = true;

        setupPostWithRestResultExpectations(projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/complete/" + complete,
                OK);

        RestResult<Void> result = service.markSpendProfile(projectId, organisationId,  complete);

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
        assertEquals(ApprovalType.APPROVED, result.getSuccessObject());
    }
}
