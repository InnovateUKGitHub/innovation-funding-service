package org.innovateuk.ifs.crm.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder;
import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentServiceImpl;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessmentServiceImpl;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.sil.crm.resource.*;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;

@Service
public class CrmServiceImpl implements CrmService {

    private static final Log LOG = LogFactory.getLog(CrmServiceImpl.class);
    @Autowired
    private BaseUserService userService;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationAddressService organisationAddressService;

    @Autowired
    private SilCrmEndpoint silCrmEndpoint;

    @Value("${ifs.new.organisation.search.enabled:false}")
    private Boolean newOrganisationSearchEnabled;

    @Override
    public ServiceResult<Void> syncCrmContact(long userId) {
        return userService.getUserById(userId).andOnSuccess(user -> {
            syncExternalUser(user, null, null);
            syncMonitoringOfficer(user);

            return serviceSuccess();
        });
    }


    @Override
    public ServiceResult<Void> syncCrmContact(long userId, long projectId) {
        return userService.getUserById(userId).andOnSuccess(user -> {

            syncExternalUser(user, projectId);
            syncMonitoringOfficer(user);

            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> syncCrmContact(long userId, long competitionId, Long applicationId) {
        FundingType fundingType = competitionService.getCompetitionById(competitionId).getSuccess().getFundingType();

        return userService.getUserById(userId).andOnSuccess(user -> {
            syncExternalUser(user, fundingType.getDisplayName(), applicationId);
            return serviceSuccess();
        });
    }

    private void syncExternalUser(UserResource user, String fundingType, Long applicationId) {
        if (!user.isInternalUser()) {
            organisationService.getAllByUserId(user.getId()).andOnSuccessReturn(organisations -> {
                ServiceResult<Void> result = serviceSuccess();
                for (OrganisationResource organisation : organisations) {
                    result = result.andOnSuccess(() -> {
                        SilContact silContact = externalUserToSilContact(user, organisation, fundingType, applicationId);
                        getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
                    });
                }
                return serviceSuccess();
            });
        }
    }

    private void syncExternalUser(UserResource user, long projectId) {
        if (!user.isInternalUser()) {
            organisationService.getByUserAndProjectId(user.getId(), projectId).andOnSuccessReturn(organisation -> {
                SilContact silContact = externalUserToSilContact(user, organisation, null, null);
                getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
                return serviceSuccess();
            });
        }
    }

    private void syncMonitoringOfficer(UserResource user) {

        if (user.hasRole(MONITORING_OFFICER)) {
            SilContact silContact = monitoringOfficerToSilContact(user);
            getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
        }

    }

    private FailingOrSucceedingResult<Void, ServiceFailure> getSilContactEmailAndOrganisationNameAndUpdateContact(SilContact silContact) {
        stripAttributesNotNeeded(silContact, () -> !FundingType.LOAN.getDisplayName().equals(silContact.getExperienceType()));
        LOG.info(format("Updating CRM contact %s and organisation %s %nPayload is:%s ",
                silContact.getEmail(), silContact.getOrganisation().getName(), silContact));
        return silCrmEndpoint.updateContact(silContact);
    }

    @Override
    public ServiceResult<Void> syncCrmCompetitionAssessment(Long competitionId) {
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();

        if (!competition.isLoan()) {
            return serviceFailure(CommonFailureKeys.GENERAL_INCORRECT_TYPE);
        } else {
            return applicationService.getApplicationsByCompetitionIdAndState(competitionId,
                    ApplicationState.submittedStates).handleSuccessOrFailure(failure -> {
                return serviceFailure(CommonFailureKeys.GENERAL_INCORRECT_TYPE);
            }, applications -> {
                applications.forEach(application -> {
                    setSilAssessmentRow(application);
                });
                //applications.forEach(application -> application.getAssessments().stream().map(assessment -> assessment.))

                // TODO-10692 put all AssessmentSummaryRows into AssessmentSummary and return it
                return serviceSuccess();
            });
        }
    }

    private void stripAttributesNotNeeded(SilContact silContact, BooleanSupplier supplier) {

        if (supplier.getAsBoolean()) {
            silContact.setExperienceType(null);
            silContact.setIfsAppID(null);
        }
    }

    private SilContact externalUserToSilContact(UserResource user, OrganisationResource organisation, String fundingType, Long applicationId) {

        SilContact silContact = setSilContactDetails(user, fundingType, applicationId);
        SilOrganisation silOrganisation = setSilOrganisation(organisation.getName(), organisation.getCompaniesHouseNumber(),
                String.valueOf(organisation.getId()));

        if (newOrganisationSearchEnabled) {
            silOrganisation.setRegisteredAddress(getRegisteredAddress(organisation));
        }

        silContact.setOrganisation(silOrganisation);
        return silContact;
    }

    private SilOrganisation setSilOrganisation(String name, String companiesHouseNumber, String s) {
        SilOrganisation silOrganisation = new SilOrganisation();
        silOrganisation.setName(name);
        silOrganisation.setRegistrationNumber(companiesHouseNumber);
        silOrganisation.setSrcSysOrgId(s);
        return silOrganisation;
    }

    private SilAddress getRegisteredAddress(OrganisationResource organisation) {
        AddressType addressType = new AddressType();
        addressType.setId(OrganisationAddressType.REGISTERED.getId());
        addressType.setName(OrganisationAddressType.REGISTERED.name());

        return organisationAddressService.findByOrganisationIdAndAddressType(organisation.getId(), addressType)
                .andOnSuccessReturn(addresses -> addresses.stream()
                        .findFirst()
                        .map(address -> organisationAddressToSilAddress(address))
                        .orElse(null))
                .getSuccess();
    }

    private SilAddress organisationAddressToSilAddress(OrganisationAddressResource organisationAddress) {
        SilAddress silAddress = new SilAddress();
        AddressResource address = organisationAddress.getAddress();

        if (address != null) {
            String[] street = new String[2];
            street[0] = address.getAddressLine2() == null ? "" : address.getAddressLine2();
            street[1] = (address.getAddressLine3() != null
                    && address.getAddressLine3().trim().length() > 0) ? format(", %s", address.getAddressLine3()) : "";

            silAddress.setBuildingName(address.getAddressLine1() == null ? "" : address.getAddressLine1());
            silAddress.setStreet(String.join("", street));
            silAddress.setLocality(address.getCounty() == null ? "" : address.getCounty());
            silAddress.setTown(address.getTown() == null ? "" : address.getTown());
            silAddress.setPostcode(address.getPostcode() == null ? "" : address.getPostcode());
            silAddress.setCountry(address.getCountry() == null ? "" : address.getCountry());
        }

        return silAddress;
    }

    private SilContact setSilContactDetails(UserResource user, String fundingType, Long applicationId) {

        SilContact silContact = new SilContact();
        silContact.setEmail(user.getEmail());
        silContact.setFirstName(user.getFirstName());
        silContact.setLastName(user.getLastName());
        silContact.setTitle(Optional.ofNullable(user.getTitle()).map(Title::getDisplayName).orElse(null));
        silContact.setSrcSysContactId(String.valueOf(user.getId()));
        silContact.setExperienceType(fundingType);
        silContact.setIfsAppID(String.valueOf(applicationId));
        silContact.setIfsUuid(user.getUid());
        return silContact;
    }

    private SilContact monitoringOfficerToSilContact(UserResource user) {

        SilContact silContact = setSilContactDetails(user, null, null);
        SilOrganisation moSilOrganisation = setSilOrganisation("IFS MO Company", "", "IFSMO01");
        silContact.setOrganisation(moSilOrganisation);

        return silContact;
    }

    // TODO-10692 create a AssessmentSummaryRow for each application
    private SilLoanAssessmentRow setSilAssessmentRow(Application application) {
        SilLoanAssessmentRow row = new SilLoanAssessmentRow();
        row.setApplicationID(application.getId().intValue());

        int assessmentScoreAverage = 0;
        int assessmentMaxScore = 0;
        int assessmentMinScore = 0;
        int assessmentRecommendCount = 0;

        application.getAssessments()
                .stream().filter(assessment -> assessment.getProcessState() == AssessmentState.SUBMITTED)
                .forEach(assessment -> {
                    List<ApplicationAssessmentResource> aa = new ApplicationAssessmentServiceImpl().getApplicationAssessmentResource(application.getId()).getSuccess();
                    //assessmentScoreAverage = aa.get(0).getOverallScore();
                    //assessmentMaxScore = aa.get(0).getMaxScoreGiven();
                    //assessmentMinScore = aa.get(0).getMinScoreGiven();
                    //assessmentRecommendCount += assessment.getFundingDecision().isFundingConfirmation() ? 1 : 0;
        });
        return row;
    }
}
