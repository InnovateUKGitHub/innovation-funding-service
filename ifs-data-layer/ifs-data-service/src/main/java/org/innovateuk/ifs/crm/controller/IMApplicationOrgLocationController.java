package org.innovateuk.ifs.crm.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilIMApplicationLocationInfo;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisationLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/application/v1")
@SecuredBySpring(value = "Controller", description = "IMApplicationOrgLocationController", securedType = IMApplicationOrgLocationController.class)
public class IMApplicationOrgLocationController {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/{applicationId}")
    @ResponseBody
    public Object getApplicationLocationInfo(
            @PathVariable("applicationId") final Long applicationId,
            HttpServletRequest request) throws JsonProcessingException {

        ApplicationResource application = applicationService.getApplicationById(applicationId).getSuccess();

        if (application == null) {
            log.error(String.format("application-org-location error: application not found IFS %d: %s", applicationId));
            return RestResult.restFailure(HttpStatus.BAD_REQUEST);
        } else {
            log.debug(String.format("GET application-org-location : ", applicationId));
            return getApplicationLocationObj(application);
        }
    }

    private Object getApplicationLocationObj(ApplicationResource applicationResource) throws JsonProcessingException {
        Long applicationId = applicationResource.getId();
        SilIMApplicationLocationInfo silIMApplicationLocationInfo = new SilIMApplicationLocationInfo();

        silIMApplicationLocationInfo = setApplicationData(applicationResource, silIMApplicationLocationInfo);

        Set<OrganisationResource> organisations = organisationService.findByApplicationId(applicationId).getSuccess();
        List<SilOrganisationLocation> silOrganisations = new ArrayList<SilOrganisationLocation>();
        for (OrganisationResource org : organisations) {
            SilOrganisationLocation silOrganisationLocation = new SilOrganisationLocation();
            silOrganisationLocation.setOrganisationID(org.getId().intValue());
            silOrganisationLocation.setOrganisationName(org.getName());
            silOrganisationLocation.setCompaniesHouseNo(org.getCompaniesHouseNumber());
            silOrganisationLocation.setInternationalRegistrationNumber(org.getInternationalRegistrationNumber());
            ApplicationFinanceResource applicationFinanceResource =
                    applicationFinanceService.financeDetails(applicationId, org.getId()).getSuccess();
            String orgSize = applicationFinanceResource.getOrganisationSize() == null ? null : applicationFinanceResource.getOrganisationSize().getDescription();
            silOrganisationLocation.setOrganisationSize(orgSize);
            String internationalLoc = applicationFinanceResource.getInternationalLocation() == null ? null : applicationFinanceResource.getInternationalLocation();
            silOrganisationLocation.setInternationalLocation(internationalLoc);
            silOrganisationLocation.setWorkPostcode(applicationFinanceResource.getWorkPostcode());
            silOrganisations.add(silOrganisationLocation);
        }
        silIMApplicationLocationInfo.setOrganisations(silOrganisations);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(silIMApplicationLocationInfo);
    }

    private SilIMApplicationLocationInfo setApplicationData(ApplicationResource applicationResource, SilIMApplicationLocationInfo silIMApplicationLocationInfo) {
        Long applicationId = applicationResource.getId();
        silIMApplicationLocationInfo.setApplicationID(applicationId.intValue());
        silIMApplicationLocationInfo.setApplicationName(applicationResource.getName());
        silIMApplicationLocationInfo.setApplicationStartDate(applicationResource.getStartDate());

        CompetitionResource competitionResource = applicationService.
                getCompetitionByApplicationId(applicationId).getSuccess();
        silIMApplicationLocationInfo.setCompetitionID(competitionResource.getId().toString());
        String fundingDecisionStatus = applicationResource.getFundingDecision() == null ? "" : applicationResource.getFundingDecision().getName();
        silIMApplicationLocationInfo.setFundingDecisionStatus(fundingDecisionStatus);

        silIMApplicationLocationInfo.setDurationInMonths(applicationResource.getDurationInMonths());
        silIMApplicationLocationInfo.setCompletionPercentage(applicationResource.getCompletion());

        ZonedDateTime mangeFundingEmailDate = applicationService.findLatestEmailFundingDateByCompetitionId(competitionResource.getId()).getSuccess();
        silIMApplicationLocationInfo.setManageFundingEmailDate(mangeFundingEmailDate);


        silIMApplicationLocationInfo.setInAssessmentReviewPanel(applicationResource.isInAssessmentReviewPanel());
        String companyAge = applicationResource.getCompanyAge() == null ? "" : applicationResource.getCompanyAge().getName();
        silIMApplicationLocationInfo.setCompanyAge(companyAge);
        String companyPrimaryFocus = applicationResource.getCompanyPrimaryFocus() == null ? "" : applicationResource.getCompanyPrimaryFocus().getName();
        silIMApplicationLocationInfo.setCompanyPrimaryFocus(companyPrimaryFocus);
        return silIMApplicationLocationInfo;
    }

}

