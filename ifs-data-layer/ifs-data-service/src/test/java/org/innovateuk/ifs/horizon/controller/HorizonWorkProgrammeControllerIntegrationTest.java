package org.innovateuk.ifs.horizon.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.horizon.mapper.HorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizonWorkProgrammeControllerIntegrationTest extends BaseControllerIntegrationTest<HorizonWorkProgrammeController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @Autowired
    private HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;

    @Autowired
    private HorizonWorkProgrammeMapper horizonWorkProgrammeMapper;

    @Autowired
    @Override
    protected void setControllerUnderTest(HorizonWorkProgrammeController controller) {
        this.controller = controller;
    }

    @Test
    public void updateWorkProgrammeForApplication() {

        loginSteveSmith();

        long applicationId = 1L;

        List<HorizonWorkProgramme> programmes = new ArrayList<>();
        programmes.add(HorizonWorkProgramme.CL2);
        programmes.add(HorizonWorkProgramme.HORIZON_CL2_2021_DEMOCRACY_01);

        ServiceResult<Void> result = horizonWorkProgrammeService.updateWorkProgrammesForApplication(programmes, applicationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void findSelectedForApplication() {

        loginSteveSmith();

        long applicationId = 1L;

        List<HorizonWorkProgramme> programmes = new ArrayList<>();
        programmes.add(HorizonWorkProgramme.CL2);
        programmes.add(HorizonWorkProgramme.HORIZON_CL2_2021_DEMOCRACY_01);

        horizonWorkProgrammeService.updateWorkProgrammesForApplication(programmes, applicationId);

        List<ApplicationHorizonWorkProgrammeResource> updated = controller.findSelectedForApplication(applicationId).getSuccess();
        assertEquals(2, updated.size());
        assertEquals("CL2", updated.get(0).getWorkProgramme());
        assertEquals("HORIZON_CL2_2021_DEMOCRACY_01", updated.get(1).getWorkProgramme());
    }
}