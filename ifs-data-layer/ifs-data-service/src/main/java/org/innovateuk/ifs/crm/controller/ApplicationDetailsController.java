package org.innovateuk.ifs.crm.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompanyAge;
import org.innovateuk.ifs.application.resource.CompanyPrimaryFocus;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilApplicationStatus;
import org.innovateuk.ifs.sil.crm.resource.SilIMApplicationLocationInfo;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisationLocation;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/application-details")
@SecuredBySpring(value = "Controller", description = "ApplicationDetailsController", securedType = ApplicationDetailsController.class)
public class ApplicationDetailsController {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @Autowired
    private UsersRolesService usersRolesService;

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/v1/{applicationId}")
    @ResponseBody
    public RestResult<SilIMApplicationLocationInfo> getApplicationLocationInfo(
            @PathVariable("applicationId") final Long applicationId,
            HttpServletRequest request) throws JsonProcessingException {

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if (user == null) {
            log.error("application-details : user not found or inactive in the system ");
            return RestResult.restFailure(HttpStatus.UNAUTHORIZED);
        } else {
            ApplicationResource application = applicationService.getApplicationById(applicationId).getSuccess();

            if (application == null) {
                log.error(String.format("application-details : application %d, not found in the system ", applicationId));
                return RestResult.restFailure(HttpStatus.BAD_REQUEST);
            } else {
                log.debug(String.format("GET application-details : ", applicationId));
                return getAssociatedApplicationData(user, applicationId, application);
            }
        }
    }

    private RestResult<SilIMApplicationLocationInfo> getAssociatedApplicationData(UserResource user, Long applicationId, ApplicationResource applicationResource) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).handleSuccessOrFailure(
                failure -> {
                    log.error(String.format("application-details error: process role not found using user %d, application %d", user.getId(), applicationId));
                    return RestResult.restFailure(failure.getErrors(), HttpStatus.FORBIDDEN);
                },
                processRole -> getApplicationLocationObj(applicationResource));
    }


    private RestResult<SilIMApplicationLocationInfo> getApplicationLocationObj(ApplicationResource applicationResource) throws JsonProcessingException {
        Long applicationId = applicationResource.getId();
        SilIMApplicationLocationInfo silIMApplicationLocationInfo = new SilIMApplicationLocationInfo();

        silIMApplicationLocationInfo = setApplicationData(applicationResource, silIMApplicationLocationInfo);

        Set<OrganisationResource> organisations = organisationService.findByApplicationId(applicationId).getSuccess();
        List<SilOrganisationLocation> silOrganisations = setOrganisationData(applicationId, organisations);
        silIMApplicationLocationInfo.setOrganisations(silOrganisations);

        return  RestResult.restSuccess(silIMApplicationLocationInfo);
    }

    private List<SilOrganisationLocation> setOrganisationData (Long applicationId, Set<OrganisationResource> organisations) {
        List<SilOrganisationLocation> silOrganisations = new ArrayList<SilOrganisationLocation>();
        for (OrganisationResource org : organisations) {
            SilOrganisationLocation silOrganisationLocation = new SilOrganisationLocation();
            silOrganisationLocation.setOrganisationID(org.getId().intValue());
            silOrganisationLocation.setOrganisationName(org.getName());
            silOrganisationLocation.setCompaniesHouseNo(org.getCompaniesHouseNumber());
            silOrganisationLocation.setInternationalRegistrationNumber(org.getInternationalRegistrationNumber());
            ApplicationFinanceResource applicationFinanceResource =
                    applicationFinanceService.financeDetails(applicationId, org.getId()).getSuccess();

            String orgSize = Optional.ofNullable(applicationFinanceResource.getOrganisationSize()).map(OrganisationSize::getDescription).orElse(null);
            silOrganisationLocation.setOrganisationSize(orgSize);
            String internationalLoc = Optional.ofNullable(applicationFinanceResource.getInternationalLocation()).orElse(null);
            silOrganisationLocation.setInternationalLocation(internationalLoc);
            silOrganisationLocation.setWorkPostcode(applicationFinanceResource.getWorkPostcode());
            silOrganisations.add(silOrganisationLocation);
        }
        return silOrganisations;
    }

    private SilIMApplicationLocationInfo setApplicationData(ApplicationResource applicationResource, SilIMApplicationLocationInfo silIMApplicationLocationInfo) {
        Long applicationId = applicationResource.getId();
        silIMApplicationLocationInfo.setApplicationID(applicationId.intValue());
        silIMApplicationLocationInfo.setApplicationName(applicationResource.getName());
        LocalDate appStartDate = Optional.ofNullable(applicationResource.getStartDate()).orElse(null);
        silIMApplicationLocationInfo.setApplicationStartDate(appStartDate != null ? appStartDate.atStartOfDay(ZoneId.systemDefault()) : null);

        CompetitionResource competitionResource = applicationService.
                getCompetitionByApplicationId(applicationId).getSuccess();
        silIMApplicationLocationInfo.setCompetitionID(competitionResource.getId().toString());
        String fundingDecisionStatus = Optional.ofNullable(applicationResource.getDecision()).map(Decision::getName) .orElse(null);
        silIMApplicationLocationInfo.setFundingDecisionStatus(fundingDecisionStatus);

        silIMApplicationLocationInfo.setDurationInMonths(applicationResource.getDurationInMonths());
        silIMApplicationLocationInfo.setCompletionPercentage(applicationResource.getCompletion());

        ZonedDateTime mangeFundingEmailDate = applicationService.findLatestEmailFundingDateByCompetitionId(competitionResource.getId()).getSuccess();
        silIMApplicationLocationInfo.setManageFundingEmailDate(mangeFundingEmailDate);

        silIMApplicationLocationInfo.setInAssessmentReviewPanel(applicationResource.isInAssessmentReviewPanel());
        String companyAge = Optional.ofNullable(applicationResource.getCompanyAge()).map(CompanyAge::getName) .orElse(null);
        silIMApplicationLocationInfo.setCompanyAge(companyAge);
        String companyPrimaryFocus = Optional.ofNullable(applicationResource.getCompanyPrimaryFocus()).map(CompanyPrimaryFocus::getName) .orElse(null);
        silIMApplicationLocationInfo.setCompanyPrimaryFocus(companyPrimaryFocus);
        return silIMApplicationLocationInfo;
    }
}

