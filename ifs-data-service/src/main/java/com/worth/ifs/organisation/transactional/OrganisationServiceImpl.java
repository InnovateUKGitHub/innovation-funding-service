package com.worth.ifs.organisation.transactional;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.Academic;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.organisation.repository.AcademicRepository;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.OrganisationTypeRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMapSet;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toCollection;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
@Service
public class OrganisationServiceImpl extends BaseTransactionalService implements OrganisationService {

    private static final Log log = LogFactory.getLog(OrganisationServiceImpl.class);

    @Autowired
    private AcademicRepository academicRepository;
    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;
    @Autowired
    private OrganisationMapper organisationMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ServiceResult<Set<OrganisationResource>> findByApplicationId(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<Organisation> organisations = roles.stream().map(role -> organisationRepository.findByProcessRoles(role)).collect(toCollection(LinkedHashSet::new));
        return serviceSuccess(simpleMapSet(organisations, organisationMapper::mapToResource));
    }

    @Override
    public ServiceResult<OrganisationResource> findById(final Long organisationId) {
        return find(organisationRepository.findOne(organisationId), notFoundError(Organisation.class, organisationId)).andOnSuccessReturn(organisationMapper::mapToResource);
    }

    @Override
    public ServiceResult<OrganisationResource> create(final OrganisationResource organisationToCreate) {
        return update(organisationToCreate);
    }

    @Override
    public ServiceResult<OrganisationResource> update(final OrganisationResource organisationResource) {
        Organisation organisation = organisationMapper.mapToDomain(organisationResource);

        if (organisation.getOrganisationType() == null) {
            organisation.setOrganisationType(organisationTypeRepository.findOne(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()));
        }

        Organisation savedOrganisation = organisationRepository.save(organisation);
        return serviceSuccess(organisationMapper.mapToResource(savedOrganisation));
    }

    @Override
    public ServiceResult<OrganisationResource> updateOrganisationNameAndRegistration(final Long organisationId, final String organisationName, final String registrationNumber) {
        return find(organisation(organisationId)).andOnSuccess(organisation -> {
            String organisationNameDecoded;
            try {
                organisationNameDecoded = UriUtils.decode(organisationName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to decode company name " + organisationName + " . Saving original encoded value.", e);
                organisationNameDecoded = organisationName;
            }
            organisation.setName(organisationNameDecoded);
            organisation.setCompanyHouseNumber(registrationNumber);
            return serviceSuccess(organisationMapper.mapToResource(organisation));
        });
    }

    @Override
    public ServiceResult<OrganisationResource> addAddress(final Long organisationId, final OrganisationAddressType organisationAddressType, AddressResource addressResource) {
        return find(organisation(organisationId)).andOnSuccessReturn(organisation -> {
            Address address = addressMapper.mapToDomain(addressResource);
            AddressType addressType = addressTypeRepository.findOne((long)organisationAddressType.getOrdinal());
            organisation.addAddress(address, addressType);
            Organisation updatedOrganisation = organisationRepository.save(organisation);
            return organisationMapper.mapToResource(updatedOrganisation);
        });
    }


    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchAcademic(final String organisationName, int maxItems) {
        List<OrganisationSearchResult> organisations;
        organisations = academicRepository.findByNameContainingIgnoreCase(organisationName, new PageRequest(0, 10))
                .stream()
                .map(a -> new OrganisationSearchResult(a.getId().toString(), a.getName()))
                .collect(Collectors.toList());

        ServiceResult<List<OrganisationSearchResult>> organisationResults;
        if (organisations.isEmpty()) {
            organisationResults = serviceFailure(notFoundError(Academic.class, organisationName));
        } else {
            organisationResults = serviceSuccess(organisations);
        }
        return organisationResults;
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getSearchOrganisation(final Long searchOrganisationId) {
        Academic academic = academicRepository.findById(searchOrganisationId);

        ServiceResult<OrganisationSearchResult> organisationResults;
        if (academic == null) {
            organisationResults = serviceFailure(notFoundError(Academic.class, searchOrganisationId));
        } else {
            organisationResults = serviceSuccess(new OrganisationSearchResult(academic.getId().toString(), academic.getName()));
        }
        return organisationResults;
    }
}
