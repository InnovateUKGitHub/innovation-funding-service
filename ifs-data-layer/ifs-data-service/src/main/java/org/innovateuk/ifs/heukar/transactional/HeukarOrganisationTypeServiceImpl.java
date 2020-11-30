package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Service
public class HeukarOrganisationTypeServiceImpl implements HeukarOrganisationTypeService {

    @Autowired
    HeukarOrganisationRepository heukarOrganisationRepository;

    @Autowired
    OrganisationTypeMapper mapper;

    @Override
    public ServiceResult<Set<OrganisationTypeResource>> findByApplicationId(long applicationId) {
        Set<OrganisationType> standardOrgTypes = heukarOrganisationRepository.findAllByApplicationId(applicationId)
                .stream()
                .map(HeukarOrganisationType::getOrganisationType)
                .collect(Collectors.toSet());

        Set<OrganisationTypeResource> organisationTypeResources = newHashSet(mapper.mapToResource(standardOrgTypes));
        return ServiceResult.serviceSuccess(organisationTypeResources);
    }

//    @Override
//    public ServiceResult<HeukarOrganisationType> createHeukarOrgType(long applicationId, long organisationTypeId) {
//
//        return ServiceResult.serviceSuccess(heukarOrganisationRepository.save(heukarOrganisationType));
//    }

}
