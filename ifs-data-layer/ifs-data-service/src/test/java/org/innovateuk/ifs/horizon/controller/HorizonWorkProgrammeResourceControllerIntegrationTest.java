package org.innovateuk.ifs.horizon.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.horizon.mapper.ApplicationHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizonWorkProgrammeResourceControllerIntegrationTest extends BaseControllerIntegrationTest<HorizonWorkProgrammeController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @Autowired
    private ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    @Autowired
    private ApplicationHorizonWorkProgrammeMapper applicationHorizonWorkProgrammeMapper;

    @Autowired
    @Override
    protected void setControllerUnderTest(HorizonWorkProgrammeController controller) {
        this.controller = controller;
    }

    @Test
    public void updateWorkProgrammeForApplication() {

        loginSteveSmith();

        long applicationId = 1L;
        List<Long> programmes = Arrays.asList(new Long[] {1L, 15L});

        ServiceResult<Void> result = horizonWorkProgrammeService.updateWorkProgrammesForApplication(programmes, applicationId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void findSelectedForApplication() {

        loginSteveSmith();

        long applicationId = 1L;
        List<Long> programmes = Arrays.asList(new Long[] {1L, 15L});

        horizonWorkProgrammeService.updateWorkProgrammesForApplication(programmes, applicationId);
        List<ApplicationHorizonWorkProgrammeResource> updated = controller.findSelectedForApplication(applicationId).getSuccess();

        assertEquals(2, updated.size());
        assertEquals((Long) programmes.get(0), (Long) updated.get(0).getWorkProgramme().getId());
        assertEquals((Long) programmes.get(1), (Long) updated.get(1).getWorkProgramme().getId());
    }
}