package com.worth.ifs.util;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import org.junit.Test;

import java.util.function.Function;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.PROCESS_ROLE_NOT_FOUND;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.ROLE_NOT_FOUND;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.EntityLookupCallbacks.withProcessRoleReturnJsonResponse;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 *
 */
public class EntityLookupCallbacksTest extends BaseUnitTestMocksTest {

    private Role role = newRole().withType(ASSESSOR).build();
    private long userId = 123L;
    private long applicationId = 456L;
    private ProcessRole returnedProcessRole = newProcessRole().withId(789L).build();

    private Function<ProcessRole, ServiceResult<JsonStatusResponse>> doWithProcessRoleFn =
            processRole -> success(JsonStatusResponse.ok("Success with " + processRole.getId()));

    @Test
    public void test_withProcessRoleReturnJsonResponse() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(singletonList(returnedProcessRole));

        ServiceResult<JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertEquals("Success with 789", response.getRight().getMessage());
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(emptyList());

        ServiceResult<JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertTrue(response.getLeft().is(ROLE_NOT_FOUND));
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noProcessRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(emptyList());

        ServiceResult<JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertTrue(response.getLeft().is(PROCESS_ROLE_NOT_FOUND));
    }
}
