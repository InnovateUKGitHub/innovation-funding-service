package com.worth.ifs.invite.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.user.domain.Ethnicity;
import com.worth.ifs.user.resource.EthnicityResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class EthnicityServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private EthnicityService ethnicityService = new EthnicityServiceImpl();

    @Test
    public void findAllActive() throws Exception {
        List<EthnicityResource> ethnicityResources = newEthnicityResource().build(2);

        List<Ethnicity> ethnicities = newEthnicity().build(2);

        when(ethnicityRepositoryMock.findByActiveTrueOrderByPriorityAsc()).thenReturn(ethnicities);
        when(ethnicityMapperMock.mapToResource(same(ethnicities.get(0)))).thenReturn(ethnicityResources.get(0));
        when(ethnicityMapperMock.mapToResource(same(ethnicities.get(1)))).thenReturn(ethnicityResources.get(1));

        List<EthnicityResource> found = ethnicityService.findAllActive().getSuccessObjectOrThrowException();
        assertEquals(ethnicityResources, found);

        InOrder inOrder = inOrder(ethnicityRepositoryMock, ethnicityMapperMock);
        inOrder.verify(ethnicityRepositoryMock, calls(1)).findByActiveTrueOrderByPriorityAsc();
        inOrder.verify(ethnicityMapperMock, calls(2)).mapToResource(isA(Ethnicity.class));
        inOrder.verifyNoMoreInteractions();
    }
}