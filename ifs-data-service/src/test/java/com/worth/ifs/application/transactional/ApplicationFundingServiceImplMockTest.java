package com.worth.ifs.application.transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationBuilder;
import com.worth.ifs.application.builder.ApplicationStatusBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.util.MapFunctions;

public class ApplicationFundingServiceImplMockTest extends BaseServiceUnitTest<ApplicationFundingService> {

    @Override
    protected ApplicationFundingService supplyServiceUnderTest() {
        return new ApplicationFundingServiceImpl();
    }

    private ApplicationStatus approvedStatus;
    private ApplicationStatus rejectedStatus;

    @Before
    public void setup() {
    	approvedStatus = ApplicationStatusBuilder.newApplicationStatus().build();
    	rejectedStatus = ApplicationStatusBuilder.newApplicationStatus().build();

    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.APPROVED.getId())).thenReturn(approvedStatus);
    	when(applicationStatusRepositoryMock.findOne(ApplicationStatusConstants.REJECTED.getId())).thenReturn(rejectedStatus);
    }

    @Test
    public void testFailIfNotAllApplicationsRepresentedInDecision() {
    	Application application1 = ApplicationBuilder.newApplication().withId(1L).build();
    	Application application2 = ApplicationBuilder.newApplication().withId(2L).build();
    	when(applicationRepositoryMock.findByCompetitionId(123L)).thenReturn(Arrays.asList(application1, application2));
    	
    	Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isFailure());
    	assertEquals("not all applications represented in funding decision", result.getFailure().getErrors().get(0).getErrorMessage());
    	verify(applicationRepositoryMock).findByCompetitionId(123L);
    	verifyNoMoreInteractions(applicationRepositoryMock);
    }
    
    @Test
    public void testSuccessAllApplicationsRepresented() {
    	Application application1 = ApplicationBuilder.newApplication().withId(1L).build();
    	Application application2 = ApplicationBuilder.newApplication().withId(2L).build();
    	when(applicationRepositoryMock.findByCompetitionId(123L)).thenReturn(Arrays.asList(application1, application2));
    	
    	Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.NOT_FUNDED);
    	
    	ServiceResult<Void> result = service.makeFundingDecision(123L, decision);
    	
    	assertTrue(result.isSuccess());
    	verify(applicationRepositoryMock).findByCompetitionId(123L);
    	verify(applicationRepositoryMock).save(application1);
    	verify(applicationRepositoryMock).save(application2);
    	assertEquals(approvedStatus, application1.getApplicationStatus());
    	assertEquals(rejectedStatus, application2.getApplicationStatus());
    }

}
