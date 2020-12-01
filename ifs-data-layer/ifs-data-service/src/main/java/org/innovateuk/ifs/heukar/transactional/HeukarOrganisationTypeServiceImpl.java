package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class HeukarOrganisationTypeServiceImpl implements HeukarOrganisationTypeService {

    @Autowired
    HeukarOrganisationRepository heukarOrganisationRepository;

    @Autowired
    OrganisationTypeMapper mapper;

    @Override
    public ServiceResult<List<OrganisationTypeResource>> findByApplicationId(long applicationId) {
        List<OrganisationTypeResource> standardOrgTypes = heukarOrganisationRepository.findAllByApplicationId(applicationId)
                .stream()
                .map(HeukarOrganisationType::getOrganisationType)
                .map(mapper::mapToResource)
                .collect(Collectors.toList());

        return ServiceResult.serviceSuccess(standardOrgTypes);
    }

    @Override
    public ServiceResult<HeukarOrganisationType> addNewOrgTypeToApplication(long applicationId, long organisationTypeId) {
        HeukarOrganisationType heukarOrganisationType = new HeukarOrganisationType();
        heukarOrganisationType.setOrganisationType(mapper.mapIdToDomain(organisationTypeId));
        heukarOrganisationType.setApplicationId(applicationId);
        return ServiceResult.serviceSuccess(heukarOrganisationRepository.save(heukarOrganisationType));
    }

}
