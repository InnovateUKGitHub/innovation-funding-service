package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
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

    //TODO: IFS-673 - this function is calling the data layer 3 times, could be done in one call
    public void handleMarkAcademicFinancesAsNotRequired(Long organisationType, SectionResource selectedSection, Long applicationId, Long competitionId, Long processRoleId) {
        if (SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType())
                && OrganisationTypeEnum.RESEARCH.getId().equals(organisationType)) {
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

    public void handleRequestFundingRequests(Map<String, String[]> params, Long applicationId, Long competitionId, Long processRoleId) {
        if (isNotRequestingFundingRequest(params)) {
            setRequestingFunding(NOT_REQUESTING_FUNDING, applicationId, competitionId, processRoleId);
        } else {
            setRequestingFunding(REQUESTING_FUNDING, applicationId, competitionId, processRoleId);
        }
    }

    private void setRequestingFunding(String requestingFunding, Long applicationId, Long competitionId, Long processRoleId) {
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
