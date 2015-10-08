package com.worth.ifs.service;

import com.worth.ifs.BaseServiceMocksTest;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.junit.Test;

import static com.worth.ifs.application.domain.ResponseBuilder.newResponse;
import static com.worth.ifs.service.AssessorServiceImpl.Failures.*;
import static com.worth.ifs.user.domain.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.domain.RoleBuilder.newRole;
import static com.worth.ifs.user.domain.UserRoleType.*;
import static java.util.Optional.empty;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by dwatson on 07/10/15.
 */
public class AssessorServiceMocksTest extends BaseServiceMocksTest<AssessorService> {

    @Override
    protected AssessorService supplyServiceUnderTest() {
        return new AssessorServiceImpl();
    }

    @Test
    public void test_responseNotFound() {

        long responseId = 1L;
        when(responseRepositoryMock.findOne(responseId)).thenReturn(null);
        Either<ServiceFailure, ServiceSuccess> serviceResult = service.updateAssessorFeedback(responseId, 2L, empty(), empty());
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(RESPONSE_NOT_FOUND));
    }

    @Test
    public void test_processRoleNotFound() {

        long responseId = 1L;
        long processRoleId = 2L;

        when(responseRepositoryMock.findOne(responseId)).thenReturn(newResponse().build());
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(null);

        Either<ServiceFailure, ServiceSuccess> serviceResult = service.updateAssessorFeedback(responseId, processRoleId, empty(), empty());
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(PROCESS_ROLE_NOT_FOUND));
    }

    @Test
    public void test_processRoleNotCorrectType() {

        long responseId = 1L;
        long processRoleId = 2L;

        ProcessRole incorrectTypeProcessRole = newProcessRole().
                withRole(newRole().withType(COLLABORATOR)).
                build();

        when(responseRepositoryMock.findOne(responseId)).thenReturn(newResponse().build());
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(incorrectTypeProcessRole);

        Either<ServiceFailure, ServiceSuccess> serviceResult = service.updateAssessorFeedback(responseId, processRoleId, empty(), empty());
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(PROCESS_ROLE_INCORRECT_TYPE));
    }

}
