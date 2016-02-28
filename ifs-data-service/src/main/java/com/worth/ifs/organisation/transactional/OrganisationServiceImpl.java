package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.mapper.OrganisationMapper;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.OrganisationTypeRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toCollection;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
@Service
public class OrganisationServiceImpl extends BaseTransactionalService implements OrganisationService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ServiceResult<Set<Organisation>> findByApplicationId(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<Organisation> organisations = roles.stream().map(role -> organisationRepository.findByProcessRoles(role)).collect(toCollection(LinkedHashSet::new));
        return serviceSuccess(organisations);
    }

    @Override
    public ServiceResult<Organisation> findById(final Long organisationId) {
        return find(organisationRepository.findOne(organisationId), notFoundError(Organisation.class, organisationId));
    }

    @Override
    public ServiceResult<OrganisationResource> create(final Organisation organisation) {

        if (organisation.getOrganisationType() == null) {
            organisation.setOrganisationType(organisationTypeRepository.findOne(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()));
        }
        Organisation savedOrganisation = organisationRepository.save(organisation);
        return serviceSuccess(organisationMapper.mapToResource(savedOrganisation));
    }

    // TODO DW - INFUND-1555 - lot of duplication between create() and saveResource()
    @Override
    public ServiceResult<OrganisationResource> saveResource(final OrganisationResource organisationResource) {

        Organisation organisation = organisationMapper.mapToDomain(organisationResource);

        if (organisation.getOrganisationType() == null) {
            organisation.setOrganisationType(organisationTypeRepository.findOne(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()));
        }
        Organisation savedOrganisation = organisationRepository.save(organisation);
        return serviceSuccess(organisationMapper.mapToResource(savedOrganisation));
    }

    @Override
    public ServiceResult<OrganisationResource> addAddress(final Long organisationId, final AddressType addressType, AddressResource addressResource) {

        return find(organisation(organisationId)).andOnSuccess(organisation -> {
	    Address address = addressMapper.mapToDomain(addressResource);
            organisation.addAddress(address, addressType);
            Organisation updatedOrganisation = organisationRepository.save(organisation);

            return serviceSuccess(organisationMapper.mapToResource(updatedOrganisation));
        });
    }



    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchAcademic(final String organisationName){
        ArrayList<OrganisationSearchResult> organisations = new ArrayList<>();

        organisations.add(new OrganisationSearchResult("1", "Aberystwyth University"));
        organisations.add(new OrganisationSearchResult("2", "American University of Beirut"));
        organisations.add(new OrganisationSearchResult("3", "Catholic University of Louvain"));

        ServiceResult organisationResults = serviceSuccess(organisations);
        return organisationResults;
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getSearchOrganisation(final Long searchOrganisationId){
        ArrayList<OrganisationSearchResult> organisations = new ArrayList<>();

        ServiceResult organisationResults = serviceSuccess(new OrganisationSearchResult("1", "Aberystwyth University"));
        return organisationResults;
    }


}
