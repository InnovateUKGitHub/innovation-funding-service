package org.innovateuk.ifs.horizon.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.mapper.ApplicationHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.innovateuk.ifs.horizon.builder.HorizonWorkProgrammeBuilder.newHorizonWorkProgramme;

public class HorizonWorkProgrammeResourceServiceImplTest extends BaseServiceUnitTest<HorizonWorkProgrammeService> {

    @Mock
    private ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    @Mock
    private HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;

    @Mock
    private ApplicationHorizonWorkProgrammeMapper applicationHorizonWorkProgrammeMapper;

    @Override
    protected HorizonWorkProgrammeService supplyServiceUnderTest() {
        return new HorizonWorkProgrammeServiceImpl();
    }

    @Test
    public void updateWorkProgrammeForApplication() {
        long applicationId = 1L;

        when(horizonWorkProgrammeRepository.findById(1L)).thenReturn(Optional.of(newHorizonWorkProgramme().withId(1L).build()));
        when(horizonWorkProgrammeRepository.findById(15L)).thenReturn(Optional.of(newHorizonWorkProgramme().withId(15L).build()));
        List<Long> programmes = Arrays.asList(new Long[] {1L, 15L});

        service.updateWorkProgrammesForApplication(programmes, applicationId);

        verify(applicationHorizonWorkProgrammeRepository).deleteAllByApplicationId(applicationId);
        verify(applicationHorizonWorkProgrammeRepository).saveAll(anyList());
    }

    @Test
    public void findSelectedForApplication(){

        long applicationId = 1L;

        ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> result = service.findSelectedForApplication(applicationId);

        assertTrue(result.isSuccess());
        verify(applicationHorizonWorkProgrammeRepository).findByApplicationId(applicationId);
    }

}