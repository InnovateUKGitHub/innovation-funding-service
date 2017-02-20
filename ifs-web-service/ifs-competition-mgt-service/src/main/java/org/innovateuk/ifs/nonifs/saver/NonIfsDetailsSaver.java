package org.innovateuk.ifs.nonifs.saver;

import org.hibernate.validator.internal.util.CollectionHelper;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service to save {@link org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm}
 */
@Service
public class NonIfsDetailsSaver {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;
    @Autowired
    private MilestoneService milestoneService;

    public ServiceResult<Void> save(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        if(!competitionResource.isNonIfs()) {
            return serviceFailure(CommonFailureKeys.ONLY_NON_IFS_COMPETITION_VALID);
        }

        Map<String, MilestoneRowForm> mappedMilestones = createMilestoneMap(form);
        mapFormFields(form, competitionResource);

        List<Error> errors = competitionSetupMilestoneService.validateMilestoneDates(mappedMilestones);
        if (!errors.isEmpty()) {
            return serviceFailure(errors);
        } else {
            return competitionService.update(competitionResource).andOnSuccess(() -> {
                List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competitionResource.getId());
                List<Error> updateErrors = competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, mappedMilestones, competitionResource.getId());
                if (updateErrors.isEmpty()) {
                    return serviceSuccess();
                } else {
                    return serviceFailure(updateErrors);
                }
            });
        }
    }

    private void mapFormFields(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        competitionResource.setNonIfsUrl(form.getUrl());
        competitionResource.setName(form.getTitle());
        competitionResource.setInnovationSector(form.getInnovationSector());
        competitionResource.setInnovationAreas(CollectionHelper.asSet(form.getInnovationArea()));
    }

    private Map<String, MilestoneRowForm> createMilestoneMap(NonIfsDetailsForm form) {
        Map<String, MilestoneRowForm> milestones = new HashMap<>();
        putDateIfFuture(milestones, MilestoneType.OPEN_DATE, form.getOpenDate());
        putDateIfFuture(milestones, MilestoneType.SUBMISSION_DATE, form.getCloseDate());
        putDateIfFuture(milestones, MilestoneType.RELEASE_FEEDBACK, form.getApplicantNotifiedDate());
        return milestones;
    }

    private void putDateIfFuture(Map<String, MilestoneRowForm> milestones, MilestoneType type, MilestoneRowForm row) {
        if(row.getMilestoneAsDateTime().isAfter(LocalDateTime.now())) {
            milestones.put(type.name(), row);
        }
    }
}
