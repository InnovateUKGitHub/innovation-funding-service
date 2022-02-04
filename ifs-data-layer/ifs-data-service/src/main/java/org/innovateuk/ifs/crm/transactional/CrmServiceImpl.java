package org.innovateuk.ifs.crm.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.ApplicationSummarisationService;
import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
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
import org.innovateuk.ifs.util.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_INCORRECT_TYPE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;

@Slf4j
@Service
public class CrmServiceImpl implements CrmService {

    @Autowired
    private BaseUserService userService;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionService competitionService;


    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationAddressService organisationAddressService;

    @Autowired
    private SilCrmEndpoint silCrmEndpoint;

    @Autowired
    private ApplicationSummarisationService applicationSummarisationService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Value("${ifs.new.organisation.search.enabled:false}")
    private Boolean newOrganisationSearchEnabled;

    @Value("${sil.rest.crmApplications.eligibilityStatusChangeSource}")
    private String eligibilityStatusChangeSource;

    @Value("${ifs.loan.partb.enabled}")
    private boolean isLoanPartBEnabled;

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

    @Override
    public ServiceResult<Void> syncCrmApplicationState(ApplicationResource application) {

        CompetitionResource competition = competitionService.getCompetitionById(application.getCompetition()).getSuccess();

        if (!competition.isLoan() || !isEligibleLoanState(application)) {
            return serviceFailure(GENERAL_INCORRECT_TYPE);
        } else {
            if (isLoanPartBEnabled) {
                SilLoanApplication loanApplication = setLoanApplication(application);
                log.info(format("Updating CRM application for appId:%s state:%s, payload:%s", loanApplication.getApplicationID(), application.getApplicationState(), loanApplication));
                return silCrmEndpoint.updateLoanApplicationState(loanApplication);
            } else {
                return serviceSuccess();
            }
        }
    }

    private boolean isEligibleLoanState(ApplicationResource application) {
        ApplicationState applicationState = application.getApplicationState();
        return ApplicationState.SUBMITTED.equals(applicationState) ||
                ApplicationState.INELIGIBLE.equals(applicationState) ||
                ApplicationState.INELIGIBLE_INFORMED.equals(applicationState);
    }

    private void syncMonitoringOfficer(UserResource user) {

        if (user.hasRole(MONITORING_OFFICER)) {
            SilContact silContact = monitoringOfficerToSilContact(user);
            getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
        }

    }

    private FailingOrSucceedingResult<Void, ServiceFailure> getSilContactEmailAndOrganisationNameAndUpdateContact(SilContact silContact) {
        stripAttributesNotNeeded(silContact, () -> !FundingType.LOAN.getDisplayName().equals(silContact.getExperienceType()));
        log.info(format("Updating CRM contact %s and organisation %s %nPayload is:%s ",
                silContact.getEmail(), silContact.getOrganisation().getName(), silContact));
        return silCrmEndpoint.updateContact(silContact);
    }

    @Override
    public ServiceResult<Void> syncCrmCompetitionAssessment(Long competitionId) {
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        SilLoanAssessment silLoanAssessment = new SilLoanAssessment();
        silLoanAssessment.setCompetitionID(competition.getId());

        List<SilLoanAssessmentRow> silLoanAssessmentRows = new ArrayList<>();

        if (!competition.isLoan()) {
            return serviceFailure(CommonFailureKeys.GENERAL_INCORRECT_TYPE);
        } else {
            return applicationService.getApplicationsByCompetitionIdAndState(competitionId,
                    ApplicationState.submittedStates).handleSuccessOrFailure(failure -> {
                return serviceFailure(CommonFailureKeys.GENERAL_INCORRECT_TYPE);
            }, applications -> {
                applications.stream().filter(app->app.getAssessments().size()>0).forEach(application -> {
                    silLoanAssessmentRows.add(setSilAssessmentRow(application));
                });
                silLoanAssessment.setApplications(silLoanAssessmentRows);
                if (isLoanPartBEnabled) {
                    log.info(format("Updating CRM application for compId:%s,  payload:%s", competition.getId(), silLoanAssessment));
                    return silCrmEndpoint.updateLoanAssessment(silLoanAssessment);
                } else {
                    return serviceSuccess();
                }

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

    private SilLoanApplication setLoanApplication(ApplicationResource application) {
        ApplicationState applicationState = application.getApplicationState();
        SilLoanApplication loanApplication = new SilLoanApplication();
        loanApplication.setApplicationID(application.getId().intValue());

        switch (applicationState) {
            case SUBMITTED:
                if (ApplicationEvent.REINSTATE_INELIGIBLE.getType().equals(application.getEvent())) {
                    markIneligible(application, loanApplication, Boolean.FALSE);
                } else {
                    CompetitionResource competition = competitionService.getCompetitionById(application.getCompetition()).getSuccess();
                    markSubmitted(application, competition, loanApplication);
                }
                break;
            case INELIGIBLE:
            case INELIGIBLE_INFORMED:
                markIneligible(application, loanApplication, Boolean.TRUE);
                break;
        }

        return loanApplication;
    }
    private SilLoanAssessmentRow setSilAssessmentRow(Application application) {
        SilLoanAssessmentRow row = new SilLoanAssessmentRow();
        Long appId = application.getId();
        row.setApplicationID(appId.intValue());
        ApplicationAssessmentAggregateResource res = assessorFormInputResponseService.getApplicationAggregateScores(appId).toGetResponse().getSuccess();

        List<Assessment> assessments
                = application.getAssessments()
                .stream().filter(assessment -> assessment.getProcessState() == AssessmentState.SUBMITTED)
                .collect(Collectors.toList());

        row.setScoreAverage(res.getAveragePercentage());
        row.setScoreSpread(getScoreSpread(appId));
        row.setAssessorNumber(assessments.size());
        row.setAssessorNotInScope(res.getTotalScope() - res.getInScope());
        row.setAssessorRecommended(getRecommendedOrNot(appId, () -> applicationAssessmentResource -> applicationAssessmentResource.getRecommended()));
        row.setAssessorNotRecommended(getRecommendedOrNot(appId, () -> applicationAssessmentResource -> !applicationAssessmentResource.getRecommended()));

        return row;
    }

    private long getRecommendedOrNot(Long appId, Supplier<Predicate<ApplicationAssessmentResource>> supplier) {
        return applicationAssessmentService.getApplicationAssessmentResource(appId).getSuccess().stream().filter(supplier.get()).count();
    }

    private int getScoreSpread(Long appId) {
        List<ApplicationAssessmentResource> assessments = applicationAssessmentService.getApplicationAssessmentResource(appId).getSuccess();
        return assessments.stream().max(Comparator.comparing(ApplicationAssessmentResource::getTotalScoreGiven)).get().getTotalScoreGiven() -
                assessments.stream().min(Comparator.comparing(ApplicationAssessmentResource::getTotalScoreGiven)).get().getTotalScoreGiven();

    }

    private void markSubmitted(ApplicationResource application, CompetitionResource competition, SilLoanApplication loanApplication) {
        loanApplication.setApplicationName(application.getName());
        loanApplication.setApplicationLocation(applicationSummarisationService.getProjectLocation(application.getId()).getSuccess());
        loanApplication.setApplicationSubmissionDate(application.getSubmittedDate());
        loanApplication.setCompetitionCode(competition.getCode());
        loanApplication.setCompetitionName(competition.getName());
        loanApplication.setProjectDuration(application.getDurationInMonths().intValue());
        loanApplication.setProjectTotalCost(applicationSummarisationService.getProjectTotalFunding(application.getId()).getSuccess().doubleValue());
        loanApplication.setProjectOtherFunding(applicationSummarisationService.getProjectOtherFunding(application.getId()).getSuccess().doubleValue());

    }

    private void markIneligible(ApplicationResource application, SilLoanApplication loanApplication, Boolean ineligibleFlag) {
        loanApplication.setMarkedIneligible(ineligibleFlag);
        loanApplication.setEligibilityStatusChangeDate(Optional.ofNullable(application.getLastStateChangeDate()).orElse(TimeMachine.now()));
        loanApplication.setEligibilityStatusChangeSource(eligibilityStatusChangeSource);

    }
}