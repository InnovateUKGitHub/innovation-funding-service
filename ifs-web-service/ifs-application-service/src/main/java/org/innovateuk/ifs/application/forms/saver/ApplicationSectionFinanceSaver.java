package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

/**
 * This Saver will handle save all finance sections that are related to the application.
 */
@Service
public class ApplicationSectionFinanceSaver extends AbstractApplicationSaver {

    private SectionService sectionService;

    public ApplicationSectionFinanceSaver(SectionService sectionService) {
        this.sectionService = sectionService;
    }

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
}