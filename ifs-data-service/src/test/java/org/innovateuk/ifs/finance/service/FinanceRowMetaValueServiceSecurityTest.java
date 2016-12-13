package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.innovateuk.ifs.finance.security.ApplicationFinanceRowPermissionRules;
import org.innovateuk.ifs.finance.transactional.FinanceRowMetaValueService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueResourceBuilder.newFinanceRowMetaValueResource;
import static org.mockito.Matchers.isA;

/**
 * Testing how the secured methods in {@link FinanceRowMetaValueService} interact with Spring Security
 */
public class FinanceRowMetaValueServiceSecurityTest extends BaseServiceSecurityTest<FinanceRowMetaValueService> {


    private ApplicationFinanceRowPermissionRules costPermissionsRules;


    @Before
    public void lookupPermissionRules() {
        costPermissionsRules = getMockPermissionRulesBean(ApplicationFinanceRowPermissionRules.class);
    }


    @Test
    public void testFindApplicationFinanceByApplicationIdAndOrganisation() {
        final Long financeRowMetaValueId = 123L;
        assertAccessDenied(
                () -> classUnderTest.findOne(financeRowMetaValueId),
                () -> costPermissionsRules.consortiumCanReadACostValueForTheirApplicationAndOrganisation(isA(FinanceRowMetaValueResource.class), isA(UserResource.class))
        );
    }


    @Override
    protected Class<TestFinanceRowMetaValueService> getClassUnderTest() {
        return TestFinanceRowMetaValueService.class;
    }

    public static class TestFinanceRowMetaValueService implements FinanceRowMetaValueService {

        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<FinanceRowMetaValueResource> findOne(Long id) {
            return serviceSuccess(newFinanceRowMetaValueResource().build());
        }
    }
}

