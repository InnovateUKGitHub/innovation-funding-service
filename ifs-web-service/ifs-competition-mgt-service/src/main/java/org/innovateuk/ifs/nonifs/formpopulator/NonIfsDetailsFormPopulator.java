package org.innovateuk.ifs.nonifs.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.stereotype.Service;

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
        form.setApplicantNotifiedDate(new MilestoneRowForm(MilestoneType.RELEASE_FEEDBACK, competitionResource.getReleaseFeedbackDate()));
        form.setOpenDate(new MilestoneRowForm(MilestoneType.OPEN_DATE, competitionResource.getStartDate()));
        form.setCloseDate(new MilestoneRowForm(MilestoneType.SUBMISSION_DATE, competitionResource.getEndDate()));
        return form;
    }
}
