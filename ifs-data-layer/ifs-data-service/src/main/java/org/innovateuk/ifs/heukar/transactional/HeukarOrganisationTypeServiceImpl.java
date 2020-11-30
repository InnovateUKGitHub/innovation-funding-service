package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class HeukarOrganisationTypeServiceImpl implements HeukarOrganisationTypeService {

    @Autowired
    HeukarOrganisationRepository heukarOrganisationRepository;

    @Override
    public ServiceResult<Set<HeukarOrganisationType>> findByApplicationId(long applicationId) {
        return ServiceResult.serviceSuccess(heukarOrganisationRepository.findAllByApplicationId(applicationId));
    }

}
