package org.innovateuk.ifs.management.nonifs.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.management.nonifs.form.MilestoneOrEmptyRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm;
import org.springframework.stereotype.Service;

/**
 * Populates a {@link org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm}
 */
@Service
public class NonIfsDetailsFormPopulator {

    public NonIfsDetailsForm populate(CompetitionResource competitionResource) {
        NonIfsDetailsForm form = new NonIfsDetailsForm();
        form.setTitle(competitionResource.getName());
        form.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
        form.setInnovationAreaCategoryId(competitionResource.getInnovationAreas().stream().findAny().orElse(null));
        form.setUrl(competitionResource.getNonIfsUrl());
        form.setApplicantNotifiedDate(new MilestoneOrEmptyRowForm(MilestoneType.NOTIFICATIONS, competitionResource.getFundersPanelEndDate()));
        form.setRegistrationCloseDate(new MilestoneRowForm(MilestoneType.REGISTRATION_DATE, competitionResource.getRegistrationDate()));
        form.setOpenDate(new MilestoneRowForm(MilestoneType.OPEN_DATE, competitionResource.getStartDate()));
        form.setCloseDate(new MilestoneRowForm(MilestoneType.SUBMISSION_DATE, competitionResource.getEndDate()));
        form.setFundingType(competitionResource.getFundingType());
        return form;
    }
}
