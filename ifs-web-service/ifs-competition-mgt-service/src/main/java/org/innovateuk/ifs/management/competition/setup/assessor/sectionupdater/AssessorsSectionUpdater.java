package org.innovateuk.ifs.management.competition.setup.assessor.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Competition setup section saver for the assessor section.
 */
@Service
public class AssessorsSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private AssessorCountOptionsRestService assessorCountOptionsRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.ASSESSORS;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition,
                                                CompetitionSetupForm competitionSetupForm) {

        AssessorsForm assessorsForm = (AssessorsForm) competitionSetupForm;

        if (!sectionToSave().preventEdit(competition)) {

            List<AssessorCountOptionResource> assessorCountOptions = assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()).getSuccess();
            if (assessorCountOptions.stream().anyMatch(assessorOption -> assessorsForm.getAssessorCount().equals(assessorOption.getOptionValue()))) {

                // Update existing config resource to below
                CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();

                // pass resource as arg in below two methods
                setFieldsDisallowedFromChangeAfterSetupAndLive(competition, assessorsForm, competitionAssessmentConfigResource);
                setFieldsAllowedFromChangeAfterSetupAndLive(competition, assessorsForm, competitionAssessmentConfigResource);

// call restservice to update the config -- remove lin 66
//                return competitionAssessmentConfigRestService.update(competitionAssessmentConfigResource).toServiceResult();
//                return competitionSetupRestService.update(competition).toServiceResult();
                return competitionAssessmentConfigRestService.update(competition.getId(), competitionAssessmentConfigResource).toServiceResult();
            } else {
                return serviceFailure(fieldError("assessorCount",
                        assessorsForm.getAssessorCount(),
                        "competition.setup.invalid.assessor.count", (Object) null));
            }
        } else {
            return serviceFailure(singletonList(new Error("COMPETITION_NOT_EDITABLE", HttpStatus.INTERNAL_SERVER_ERROR)));
        }
    }

    // Update void to new resource
    private CompetitionAssessmentConfigResource setFieldsDisallowedFromChangeAfterSetupAndLive(CompetitionResource competition,
                                                                AssessorsForm assessorsForm,
                                                                CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
        if (!competition.isSetupAndLive()) {

            competitionAssessmentConfigResource.setAssessorPay(assessorsForm.getAssessorPay());
        }

        // return new resource
        return new CompetitionAssessmentConfigResource();
    }

    // Update void to new resource
    private CompetitionAssessmentConfigResource setFieldsAllowedFromChangeAfterSetupAndLive(CompetitionResource competition,
                                                             AssessorsForm assessorsForm,
                                                             CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {

        competitionAssessmentConfigResource.setAssessorCount(assessorsForm.getAssessorCount());
        competitionAssessmentConfigResource.setHasAssessmentPanel(assessorsForm.getHasAssessmentPanel());
        competitionAssessmentConfigResource.setHasInterviewStage(assessorsForm.getHasInterviewStage());
        competitionAssessmentConfigResource.setAssessorFinanceView(assessorsForm.getAssessorFinanceView());
        competitionAssessmentConfigResource.setAverageAssessorScore(assessorsForm.getAverageAssessorScore());
        competition.setCompetitionAssessmentConfig(competitionAssessmentConfigResource);

        return competitionAssessmentConfigResource;
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return AssessorsForm.class.equals(clazz);
    }

}
