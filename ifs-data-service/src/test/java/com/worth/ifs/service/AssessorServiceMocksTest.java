package com.worth.ifs.service;

import com.worth.ifs.BaseServiceMocksTest;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.junit.Test;

import java.util.Optional;

import static com.worth.ifs.service.AssessorServiceImpl.*;
import static com.worth.ifs.service.AssessorServiceImpl.Failures.*;
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
        assertTrue(serviceResult.getLeft().is(Failures.RESPONSE_NOT_FOUND.name()));
    }

}
