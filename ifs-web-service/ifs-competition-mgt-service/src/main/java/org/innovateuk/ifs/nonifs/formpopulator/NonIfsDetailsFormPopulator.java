package org.innovateuk.ifs.nonifs.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Populates a {@link org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm}
 */
@Service
public class NonIfsDetailsFormPopulator {

    public NonIfsDetailsForm populate(CompetitionResource competitionResource) {
        NonIfsDetailsForm form = new NonIfsDetailsForm();
        form.setTitle(competitionResource.getName());
        form.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
        form.setInnovationAreaCategoryId(competitionResource.getInnovationAreas().stream().findAny().orElse(null));
        form.setUrl(competitionResource.getNonIfsUrl());
        form.setApplicantNotifiedDate(createEditableMilestoneRowForm(MilestoneType.RELEASE_FEEDBACK, competitionResource.getReleaseFeedbackDate()));
        form.setOpenDate(createEditableMilestoneRowForm(MilestoneType.OPEN_DATE, competitionResource.getStartDate()));
        form.setCloseDate(createEditableMilestoneRowForm(MilestoneType.SUBMISSION_DATE, competitionResource.getEndDate()));
        return form;
    }

    private MilestoneRowForm createEditableMilestoneRowForm(MilestoneType milestoneType, LocalDateTime date) {
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(milestoneType, date);
        milestoneRowForm.setEditable(true);
        return milestoneRowForm;

    }
}
