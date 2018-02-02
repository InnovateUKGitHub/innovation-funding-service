package org.innovateuk.ifs.finance.security;

import org.junit.Test;

public class FinanceTotalsRulesTest {

    @Override
    protected Class<TestProjectFinanceRowService> getClassUnderTest() {
        return TestProjectFinanceRowService.class;
    }

    public static class TestProjectFinanceRowService implements Finance {

        @Override
        public ServiceResult<List<? extends FinanceRow>> getCosts(Long projectFinanceId, String costTypeName, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> getCostItem(Long costItemId) {
            return null;
        }

        @Override
        public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, String costTypeName, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> addCost(@P("projectFinanceId") Long projectFinanceId, Long questionId, FinanceRowItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> updateCost(@P("costId") Long costId, FinanceRowItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> addCostWithoutPersisting(@P("projectFinanceId") Long projectFinanceId, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteCost(@P("projectId") Long projectId, @P("organisationId") Long organisationId, @P("costId") Long costId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectFinanceResource> updateCost(@P("projectFinanceId") Long projectFinanceId, ProjectFinanceResource applicationFinance) {
            return null;
        }

        @Override
        public ServiceResult<ProjectFinanceResource> financeChecksDetails(Long projectId, Long organisationId) {
            return serviceSuccess(newProjectFinanceResource().build());
        }

        @Override
        public ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(Long projectId) {
            return null;
        }

        @Override
        public FinanceRowHandler getCostHandler(FinanceRowItem costItemId) {
            return null;
        }
    }
}