package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;

import java.util.List;

public interface HorizonWorkProgrammeRestService {

    RestResult<HorizonWorkProgrammeResource> findWorkProgramme(Long workProgrammeId);

    RestResult<List<HorizonWorkProgrammeResource>> findRootWorkProgrammes();

    RestResult<List<HorizonWorkProgrammeResource>> findChildrenWorkProgrammes(Long workProgrammeId);

    RestResult<List<HorizonWorkProgrammeResource>> findWorkProgrammesByCompetition(Long competitionId);

    RestResult<Void> updateWorkProgrammeForApplication(List<HorizonWorkProgrammeResource> selectedProgrammes, Long applicationId);

    RestResult<List<ApplicationHorizonWorkProgrammeResource>> findSelected(Long applicationId);
}
