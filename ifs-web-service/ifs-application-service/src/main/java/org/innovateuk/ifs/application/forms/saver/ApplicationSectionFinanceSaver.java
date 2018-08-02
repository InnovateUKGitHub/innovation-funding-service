package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

/**
 * This Saver will handle save all finance sections that are related to the application.
 */
@Service
public class ApplicationSectionFinanceSaver extends AbstractApplicationSaver {

    @Autowired
    private SectionService sectionService;

    public void handleMarkAcademicFinancesAsNotRequired(long organisationType, SectionResource selectedSection, long applicationId, long competitionId, long processRoleId) {
        if (SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType())
                && OrganisationTypeEnum.RESEARCH.getId() == organisationType) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }

    public void handleStateAid(Map<String, String[]> params, ApplicationResource application, ApplicationForm form, SectionResource selectedSection) {
        if (isMarkSectionAsCompleteRequest(params)) {
            application.setStateAidAgreed(form.isStateAidAgreed());
        } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
            application.setStateAidAgreed(Boolean.FALSE);
        }
    }

    public void handleRequestFundingRequests(Map<String, String[]> params, long applicationId, long competitionId, long processRoleId) {
        if (isNotRequestingFundingRequest(params)) {
            setRequestingFunding(NOT_REQUESTING_FUNDING, applicationId, competitionId, processRoleId);
        } else {
            setRequestingFunding(REQUESTING_FUNDING, applicationId, competitionId, processRoleId);
        }
    }

    private void setRequestingFunding(String requestingFunding, long applicationId, long competitionId, long processRoleId) {
        SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
        SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
        if (REQUESTING_FUNDING.equals(requestingFunding)) {
            sectionService.markAsInComplete(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsInComplete(fundingSection.getId(), applicationId, processRoleId);
        } else if (NOT_REQUESTING_FUNDING.equals(requestingFunding)) {
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }
}