package org.innovateuk.ifs.organisation.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
        Set<ProcessRole> applicantRoles = new HashSet<>(simpleFilter(roles, ProcessRole::isLeadApplicantOrCollaborator));
        List<Organisation> organisations = simpleMap(applicantRoles, role -> organisationRepository.findOne(role.getOrganisationId()));
        List<OrganisationResource> organisationResources = new ArrayList<>(simpleMap(organisations, organisationMapper::mapToResource));
        organisationResources.sort(Comparator.comparing(OrganisationResource::getId));
        return serviceSuccess(new LinkedHashSet<>(organisationResources));
    }

    @Override
    public ServiceResult<OrganisationResource> findById(final Long organisationId) {
        Organisation org = organisationRepository.findOne(organisationId);
        return find(org, notFoundError(Organisation.class, organisationId)).andOnSuccessReturn(o -> {
            return organisationMapper.mapToResource(o);
        });
    }

    @Override
    public ServiceResult<OrganisationResource> getPrimaryForUser(final Long userId) {
        List<Organisation> organisations = organisationRepository.findByUsersId(userId);
        if (organisations.isEmpty()) {
            return serviceFailure(CommonErrors.notFoundError(Organisation.class));
        }
        return serviceSuccess(organisationMapper.mapToResource(organisations.get(0)));
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> create(final OrganisationResource organisationToCreate) {
        return update(organisationToCreate);
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> update(final OrganisationResource organisationResource) {
        Organisation organisation = organisationMapper.mapToDomain(organisationResource);

        if (organisation.getOrganisationType() == null) {
            organisation.setOrganisationType(organisationTypeRepository.findOne(OrganisationTypeEnum.BUSINESS.getId()));
        }

        Organisation savedOrganisation = organisationRepository.save(organisation);
        return serviceSuccess(organisationMapper.mapToResource(savedOrganisation));
    }

    @Override
    @Transactional
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
    @Transactional
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
