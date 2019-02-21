package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public class GrantProcessServiceImplTest extends BaseServiceUnitTest<GrantProcessServiceImpl> {

    @Mock
    private GrantProcessRepository grantProcessRepository;

    @Override
    protected GrantProcessServiceImpl supplyServiceUnderTest() {
        return new GrantProcessServiceImpl();
    }


    @Test
    public void findReadyToSend() {
        GrantProcess grantProcessOne = new GrantProcess(1);
        GrantProcess grantProcessTwo = new GrantProcess(2);
        List<GrantProcess> readyToSend = Arrays.asList(grantProcessOne, grantProcessTwo);
        when(grantProcessRepository.findByPendingIsTrue()).thenReturn(readyToSend);

        assertThat(service.findReadyToSend(), is(readyToSend));
    }
}