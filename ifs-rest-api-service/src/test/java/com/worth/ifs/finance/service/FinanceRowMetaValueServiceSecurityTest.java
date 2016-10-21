package com.worth.ifs.finance.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import com.worth.ifs.finance.security.FinanceRowPermissionRules;
import com.worth.ifs.finance.transactional.FinanceRowMetaValueService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.finance.builder.FinanceRowMetaValueResourceBuilder.newFinanceRowMetaValue;
import static org.mockito.Matchers.isA;

/**
 * Testing how the secured methods in {@link FinanceRowMetaValueService} interact with Spring Security
 */
public class FinanceRowMetaValueServiceSecurityTest extends BaseServiceSecurityTest<FinanceRowMetaValueService> {


    private FinanceRowPermissionRules costPermissionsRules;


    @Before
    public void lookupPermissionRules() {
        costPermissionsRules = getMockPermissionRulesBean(FinanceRowPermissionRules.class);
    }


    @Test
    public void testFindApplicationFinanceByApplicationIdAndOrganisation() {
        final FinanceRowMetaValueId financeRowMetaValueId = new FinanceRowMetaValueId();
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
        public ServiceResult<FinanceRowMetaValueResource> findOne(FinanceRowMetaValueId id) {
            return ServiceResult.serviceSuccess(FinanceRowMetaValueResourceBuilder.newFinanceRowMetaValue().build());
        }
    }
}

