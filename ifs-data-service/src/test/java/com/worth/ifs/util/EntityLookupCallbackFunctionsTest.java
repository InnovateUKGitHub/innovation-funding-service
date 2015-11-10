package com.worth.ifs.util;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.function.Function;

import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.EntityLookupCallbackFunctions.withProcessRoleReturnJsonResponse;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 *
 */
public class EntityLookupCallbackFunctionsTest extends BaseUnitTestMocksTest {

    private Role role = newRole().withType(ASSESSOR).build();
    private long userId = 123L;
    private long applicationId = 456L;
    private ProcessRole returnedProcessRole = newProcessRole().withId(789L).build();

    private Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>> doWithProcessRoleFn =
            processRole -> Either.right(JsonStatusResponse.ok("Success with " + processRole.getId()));

    @Test
    public void test_withProcessRoleReturnJsonResponse() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(asList(returnedProcessRole));

        Either<JsonStatusResponse, JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, new MockHttpServletResponse(), serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertEquals("Success with 789", response.getRight().getMessage());
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList());

        Either<JsonStatusResponse, JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, new MockHttpServletResponse(), serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertEquals("No role of type ASSESSOR set up on Application 456", response.getLeft().getMessage());
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noProcessRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(asList());

        Either<JsonStatusResponse, JsonStatusResponse> response = withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                applicationId, new MockHttpServletResponse(), serviceLocator,
                processRole -> doWithProcessRoleFn.apply(processRole));

        assertEquals("No process role of type ASSESSOR set up on Application 456", response.getLeft().getMessage());
    }
}
