package org.innovateuk.ifs.horizon.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.mapper.HorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme.workProgrammes;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

public class HorizonWorkProgrammeServiceImplTest extends BaseServiceUnitTest<HorizonWorkProgrammeService> {

    @Mock
    private HorizonWorkProgrammeRepository repository;

    @Mock
    private HorizonWorkProgrammeMapper horizonWorkProgrammeMapper;

    @Override
    protected HorizonWorkProgrammeService supplyServiceUnderTest() {
        return new HorizonWorkProgrammeServiceImpl();
    }

    @Test
    public void updateLocationsForApplication() {
        long applicationId = 1L;

        List<HorizonWorkProgramme> programmes = new ArrayList<>();
        programmes.addAll(workProgrammes);

        service.updateWorkProgrammesForApplication(programmes, applicationId);

        verify(repository).deleteAllByApplicationId(applicationId);
        verify(repository).saveAll(anyList());
    }

    @Test
    public void findSelectedForApplication(){

        long applicationId = 1L;

        ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> result = service.findSelectedForApplication(applicationId);

        assertTrue(result.isSuccess());
        verify(repository).findByApplicationId(applicationId);
    }

}