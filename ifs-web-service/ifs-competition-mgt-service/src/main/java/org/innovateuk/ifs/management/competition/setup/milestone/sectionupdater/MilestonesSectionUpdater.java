package org.innovateuk.ifs.management.competition.setup.milestone.sectionupdater;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    private static Log LOG = LogFactory.getLog(MilestonesSectionUpdater.class);

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;
        LinkedMap<String, GenericMilestoneRowForm> milestoneEntries = milestonesForm.getMilestoneEntries();

        List<Error> errors = returnErrorsFoundOnSave(milestoneEntries, competition);
        if (!errors.isEmpty()) {
            competitionSetupMilestoneService.sortMilestones(milestonesForm);
            return serviceFailure(errors);
        }

        return serviceSuccess();
    }

    private List<Error> returnErrorsFoundOnSave(LinkedMap<String, GenericMilestoneRowForm> milestoneEntries, CompetitionResource competition) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competition.getId()).getSuccess();
        Map<String, GenericMilestoneRowForm> filteredMilestoneEntries = milestoneEntries;

        //If competition is already set up only allow to save of future milestones.
        if (TRUE.equals(competition.getSetupComplete())) {
            List<MilestoneType> futureTypes = milestones.stream()
                    .filter(milestoneResource -> milestoneResource.getDate() == null || ZonedDateTime.now().isBefore(milestoneResource.getDate()))
                    .map(MilestoneResource::getType)
                    .collect(Collectors.toList());

            filteredMilestoneEntries = CollectionFunctions.simpleFilter(milestoneEntries, (name, form) -> futureTypes.contains(form.getMilestoneType()));
        }

        List<Error> errors = competitionSetupMilestoneService.validateMilestoneDates(filteredMilestoneEntries);
        if (!errors.isEmpty()) {
            return errors;
        }

        ServiceResult<Void> result = competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, filteredMilestoneEntries, competition.getId());
        if (result.isFailure()) {
            return result.getErrors();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return MilestonesForm.class.equals(clazz);
    }

}
