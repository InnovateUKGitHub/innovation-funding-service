package org.innovateuk.ifs.organisation.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.*;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.AcademicRepository;
import org.innovateuk.ifs.organisation.repository.ExecutiveOfficerRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.SicCodeRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_COMPANY_NAME_TOO_LONG;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.resource.OrganisationResource.normalOrgComparator;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
@Service
public class OrganisationServiceImpl extends BaseTransactionalService implements OrganisationService {

    private static final Log log = LogFactory.getLog(OrganisationServiceImpl.class);
    private static final Integer MAX_CHARACTER_DB_LENGTH = 255;

    @Autowired
    private AcademicRepository academicRepository;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private SicCodeRepository sicCodeRepository;

    @Autowired
    private ExecutiveOfficerRepository executiveOfficerRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public ServiceResult<Set<OrganisationResource>> findByApplicationId(final long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Long leadOrganisationId = roles.stream()
                .filter(role -> role.getRole().equals(ProcessRoleType.LEADAPPLICANT))
                .map(role -> role.getOrganisationId())
                .findAny().orElse(null);

        final Comparator<OrganisationResource> comparator;

        if (leadOrganisationId != null) {
            Comparator<OrganisationResource> leadComparator = Comparator.comparing(organisationResource -> leadOrganisationId.equals(organisationResource.getId()), Comparator.reverseOrder());
            comparator = leadComparator.thenComparing(normalOrgComparator);
        } else {
            comparator = normalOrgComparator;
        }

        Set<ProcessRole> applicantRoles = new HashSet<>(simpleFilter(roles, ProcessRole::isLeadApplicantOrCollaborator));
        List<Organisation> organisations = simpleMap(applicantRoles, role -> organisationRepository.findById(role.getOrganisationId()).orElse(null));
        List<OrganisationResource> organisationResources = new ArrayList<>(simpleMap(organisations, organisationMapper::mapToResource));
        organisationResources.sort(comparator);
        return serviceSuccess(new LinkedHashSet<>(organisationResources));
    }

    @Override
    public ServiceResult<OrganisationResource> findById(final long organisationId) {
        return find(organisationRepository.findById(organisationId), notFoundError(Organisation.class, organisationId))
                .andOnSuccess(organisation -> serviceSuccess(organisationMapper.mapToResource(organisation)));
    }

    @Override
    public ServiceResult<OrganisationResource> getByUserAndApplicationId(long userId, long applicationId) {
        Organisation org = organisationRepository.findByProcessRolesUserIdAndProcessRolesApplicationId(userId, applicationId);
        return find(org, notFoundError(Organisation.class, userId, applicationId)).andOnSuccessReturn(o ->
                organisationMapper.mapToResource(o)
        );
    }

    @Override
    public ServiceResult<OrganisationResource> getByUserAndProjectId(long userId, long projectId) {
        Organisation org = organisationRepository.findByUserAndProjectId(userId, projectId);
        return find(org, notFoundError(Organisation.class, userId, projectId)).andOnSuccessReturn(o ->
                organisationMapper.mapToResource(o)
        );
    }

    @Override
    public ServiceResult<List<OrganisationResource>> getAllByUserId(long userId) {
        return serviceSuccess(simpleMap(organisationRepository.findDistinctByProcessRolesUserId(userId),
                organisationMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<OrganisationResource>> getOrganisations(long userId, boolean international) {
        return serviceSuccess(simpleMap(
                organisationRepository.findDistinctByProcessRolesUserIdAndInternational(userId, international), organisationMapper::mapToResource
        ));
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> create(final OrganisationResource organisationToCreate) {
        return update(organisationToCreate);
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> update(final OrganisationResource organisationResource) {
        return serviceSuccess(organisationMapper.mapToResource(createNewOrganisation(organisationResource)));
    }

    private Organisation createNewOrganisation(OrganisationResource organisationResource) {
        Organisation organisation = organisationMapper.mapToDomain(organisationResource);
        Organisation savedOrganisation = organisationRepository.save(organisation);

        return savedOrganisation;
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> syncCompaniesHouseDetails(OrganisationResource organisationResource) {
        return find(organisationRepository.findById(organisationResource.getId()), notFoundError(Organisation.class, organisationResource.getId()))
                .andOnSuccess(organisation -> serviceSuccess(organisationMapper.mapToResource(syncOrganisation(organisation.getId(), organisationResource))));
    }

    private Organisation syncOrganisation(Long organisationId, OrganisationResource organisationToSync) {
        Organisation organisation = organisationMapper.mapToDomain(organisationToSync);
        setOrganisationIdForRelatedEntities(organisation);

        deleteExistingCompaniesHouseDetails(organisationId, organisation);

        Organisation savedOrganisation = organisationRepository.save(organisation);

        return savedOrganisation;
    }

    private void deleteExistingCompaniesHouseDetails(Long organisationId, Organisation organisation) {
        List<SicCode> removedSicCodes = sicCodeRepository.findByOrganisationId(organisationId).stream()
                .filter(sicCode -> !organisation.getSicCodes().contains(sicCode))
                .collect(Collectors.toList());
        sicCodeRepository.deleteAll(removedSicCodes);

        List<ExecutiveOfficer> removedExecutiveOfficers = executiveOfficerRepository.findByOrganisationId(organisationId).stream()
                .filter(executiveOfficer -> !organisation.getExecutiveOfficers().contains(executiveOfficer))
                .collect(Collectors.toList());
        executiveOfficerRepository.deleteAll(removedExecutiveOfficers);

        AddressType registeredAddressType = new AddressType();
        registeredAddressType.setId(OrganisationAddressType.REGISTERED.getId());
        registeredAddressType.setName(OrganisationAddressType.REGISTERED.name());
        List<OrganisationAddress> removedOrganisationAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(organisationId, registeredAddressType).stream()
                .filter(organisationAddress -> !organisation.getAddresses().contains(organisationAddress))
                .collect(Collectors.toList());
        removedOrganisationAddresses.stream().forEach(organisationAddress -> {
            addressRepository.delete(organisationAddress.getAddress());
            organisationAddressRepository.delete(organisationAddress);
        });
    }

    private void setOrganisationIdForRelatedEntities(Organisation mappedOrganisation) {
        mappedOrganisation.getAddresses().forEach(address -> address.setOrganisation(mappedOrganisation));
        mappedOrganisation.getSicCodes().forEach(sicCode -> sicCode.setOrganisation(mappedOrganisation));
        mappedOrganisation.getExecutiveOfficers().forEach(director -> director.setOrganisation(mappedOrganisation));
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource>
        updateOrganisationNameAndRegistration(final long organisationId, final String organisationName, final String registrationNumber) {
        return getOrganisation(organisationId)
            .andOnSuccess(org -> {
                if (organisationName.length() <= MAX_CHARACTER_DB_LENGTH) {
                    org.setName(decodeOrganisationName(organisationName));
                    org.setCompaniesHouseNumber(registrationNumber);
                    return serviceSuccess(organisationMapper.mapToResource(org));
                }
                return serviceFailure(BANK_DETAILS_COMPANY_NAME_TOO_LONG);
            });
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> addAddress(final long organisationId, final OrganisationAddressType organisationAddressType, AddressResource addressResource) {
        return find(organisation(organisationId)).andOnSuccessReturn(organisation -> {
            Address address = addressMapper.mapToDomain(addressResource);
            AddressType addressType = addressTypeRepository.findById(organisationAddressType.getId()).orElse(null);
            organisation.addAddress(address, addressType);
            Organisation updatedOrganisation = organisationRepository.save(organisation);
            return organisationMapper.mapToResource(updatedOrganisation);
        });
    }


    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchAcademic(final String organisationName, int maxItems) {
        List<OrganisationSearchResult> organisations;
        organisations = academicRepository.findByNameContainingIgnoreCase(organisationName, PageRequest.of(0, 10))
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
    public ServiceResult<OrganisationSearchResult> getSearchOrganisation(final long searchOrganisationId) {
        Optional<Academic> academic = academicRepository.findById(searchOrganisationId);

        ServiceResult<OrganisationSearchResult> organisationResults;
        if (!academic.isPresent()) {
            organisationResults = serviceFailure(notFoundError(Academic.class, searchOrganisationId));
        } else {
            organisationResults = serviceSuccess(new OrganisationSearchResult(academic.get().getId().toString(), academic.get().getName()));
        }
        return organisationResults;
    }

    private String decodeOrganisationName(String encodedName) {
        String organisationNameDecoded;
        try {
            organisationNameDecoded = UriUtils.decode(encodedName, "UTF-8");
        } catch (Exception e) {
            log.error("Unable to decode company name " + encodedName + ". Saving original encoded value.", e);
            organisationNameDecoded = encodedName;
        }
        return organisationNameDecoded;
    }

    private List<OrganisationResource> organisationsToResources(List<Organisation> organisations) {
        return simpleMap(organisations, organisation -> organisationMapper.mapToResource(organisation));
    }

    public ServiceResult<List<OrganisationResource>> findOrganisationsByName(String name) {
        return find(organisationRepository.findByNameOrderById(name), notFoundError(Organisation.class, name))
                .andOnSuccessReturn(organisationMapper::mapToResources);
    }

    @Override
    @Transactional
    public ServiceResult<OrganisationResource> updateOrganisationName(Long organisationId, String organisationName) {
        return getOrganisation(organisationId)
            .andOnSuccess(org -> {
                if (organisationName.length() <= MAX_CHARACTER_DB_LENGTH) {
                    org.setName(decodeOrganisationName(organisationName));
                    organisationRepository.save(org);
                    return serviceSuccess(organisationMapper.mapToResource(org));
                }
                return serviceFailure(BANK_DETAILS_COMPANY_NAME_TOO_LONG);
            });
    }

    public ServiceResult<List<OrganisationResource>> findOrganisationsByCompaniesHouseId(String companiesHouseNumber) {
        return find(organisationRepository.findByCompaniesHouseNumberOrderById(companiesHouseNumber), notFoundError(Organisation.class, companiesHouseNumber))
                .andOnSuccessReturn(organisationMapper::mapToResources);
    }
}
