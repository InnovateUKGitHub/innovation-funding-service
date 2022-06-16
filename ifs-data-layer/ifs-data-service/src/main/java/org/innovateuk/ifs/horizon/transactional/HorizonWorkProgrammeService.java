package org.innovateuk.ifs.horizon.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HorizonWorkProgrammeService {

    @NotSecured(value = "Anyone can see an HorizonWorkProgramme", mustBeSecuredByOtherServices = false)
    ServiceResult<HorizonWorkProgrammeResource> findById(Long workProgrammeId);

    @NotSecured(value = "Anyone can see an HorizonWorkProgramme", mustBeSecuredByOtherServices = false)
    ServiceResult<List<HorizonWorkProgrammeResource>> findRootWorkProgrammes();

    @NotSecured(value = "Anyone can see an HorizonWorkProgramme", mustBeSecuredByOtherServices = false)
    ServiceResult<List<HorizonWorkProgrammeResource>> findChildrenWorkProgrammes(Long workProgrammeId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> updateWorkProgrammesForApplication(List<HorizonWorkProgrammeResource> programmes, Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(Long applicationId);
}
