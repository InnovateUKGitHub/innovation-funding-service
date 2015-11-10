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
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 *
 */
public class IfsWrapperFunctionsTest extends BaseUnitTestMocksTest {

    private Role role = newRole().withType(ASSESSOR).build();
    private long userId = 123L;
    private long applicationId = 456L;
    private ProcessRole returnedProcessRole = newProcessRole().withId(789L).build();

    private Function<Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>>, JsonStatusResponse> function =
            IfsWrapperFunctions.withProcessRoleReturnJsonResponse(userId, ASSESSOR,
                    applicationId, new MockHttpServletResponse(), serviceLocator);

    private Function<ProcessRole, Either<JsonStatusResponse, JsonStatusResponse>> doWithProcessRoleFn =
            processRole -> Either.right(JsonStatusResponse.ok("Success with " + processRole.getId()));

    @Test
    public void test_withProcessRoleReturnJsonResponse() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(asList(returnedProcessRole));

        JsonStatusResponse response = function.apply(doWithProcessRoleFn);
        assertEquals("Success with 789", response.getMessage());
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList());

        JsonStatusResponse response = function.apply(doWithProcessRoleFn);
        assertEquals("No role of type ASSESSOR set up on Application 456", response.getMessage());
    }

    @Test
    public void test_withProcessRoleReturnJsonResponse_noProcessRole() {

        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(asList(role));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, role, applicationId)).thenReturn(asList());

        JsonStatusResponse response = function.apply(doWithProcessRoleFn);
        assertEquals("No process role of type ASSESSOR set up on Application 456", response.getMessage());
    }
}
