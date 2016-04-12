package com.worth.ifs.finance.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.builder.CostFieldResourceBuilder;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.security.CostFieldPermissionsRules;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static com.worth.ifs.finance.service.CostServiceSecurityTest.TestCostService.ARRAY_SIZE_FOR_POST_FILTER_TESTS;

/**
 * Testing how the secured methods in CostService interact with Spring Security
 */
public class CostServiceSecurityTest extends BaseServiceSecurityTest<CostService> {

    private CostFieldPermissionsRules costFieldPermissionsRules;

    @Before
    public void lookupPermissionRules() {
        costFieldPermissionsRules = getMockPermissionRulesBean(CostFieldPermissionsRules.class);
    }

    @Test
    public void testFindAllCostFields() {
        service.findAllCostFields();
        verify(costFieldPermissionsRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).loggedInUsersCanReadCostFieldReferenceData(isA(CostFieldResource.class), isA(UserResource.class));
        verifyNoMoreInteractions(costFieldPermissionsRules);

    }

    @Override
    protected Class<TestCostService> getServiceClass() {
        return TestCostService.class;
    }

    public static class TestCostService implements CostService {


        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<CostField> getCostFieldById(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<CostFieldResource>> findAllCostFields() {
            return ServiceResult.serviceSuccess(CostFieldResourceBuilder.newCostFieldResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<CostItem> addCost(Long applicationFinanceId, Long questionId, CostItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateCost(Long id, CostItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteCost(Long costId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Double> getResearchParticipationPercentage(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationFinanceResource> addCost(Long applicationId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationFinanceResource> updateCost(Long applicationFinanceId, ApplicationFinanceResource applicationFinance) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId) {
            return null;
        }
    }
}
