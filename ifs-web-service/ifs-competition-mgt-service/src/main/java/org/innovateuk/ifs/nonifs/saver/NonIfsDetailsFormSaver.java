package org.innovateuk.ifs.nonifs.saver;

import org.hibernate.validator.internal.util.CollectionHelper;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Service to save {@link org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm}
 */
@Service
public class NonIfsDetailsFormSaver {

    private static List<MilestoneType> PUBLIC_MILESTONE_TYPES = asList(MilestoneType.OPEN_DATE, MilestoneType.SUBMISSION_DATE, MilestoneType.RELEASE_FEEDBACK);

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ServiceResult<Void> save(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        if(!competitionResource.isNonIfs()) {
            return serviceFailure(CommonFailureKeys.ONLY_NON_IFS_COMPETITION_VALID);
        }

        Map<String, MilestoneRowForm> mappedMilestones = createMilestoneMap(form);
        mapFormFields(form, competitionResource);
        return competitionService.update(competitionResource).andOnSuccess(() -> {
            List<MilestoneResource> milestones = getPublicMilestones(competitionResource);
            return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, mappedMilestones, competitionResource.getId());
        });
    }

    private List<MilestoneResource> getPublicMilestones(CompetitionResource competitionResource) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId()).getSuccessObjectOrThrowException();
        if (milestones.isEmpty()) {
            createPublicMilestones(competitionResource.getId());
            milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId()).getSuccessObjectOrThrowException();
        }
        return milestones;
    }

    private void createPublicMilestones(Long competitionId) {
        PUBLIC_MILESTONE_TYPES.forEach(type -> milestoneRestService.create(type, competitionId).getSuccessObjectOrThrowException());
    }

    private void mapFormFields(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        competitionResource.setNonIfsUrl(form.getUrl());
        competitionResource.setName(form.getTitle());
        competitionResource.setInnovationSector(form.getInnovationSectorCategoryId());
        competitionResource.setInnovationAreas(CollectionHelper.asSet(form.getInnovationAreaCategoryId()));
    }

    private Map<String, MilestoneRowForm> createMilestoneMap(NonIfsDetailsForm form) {
        Map<String, MilestoneRowForm> milestones = new HashMap<>();
        milestones.put(MilestoneType.OPEN_DATE.name(), form.getOpenDate());
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), form.getCloseDate());
        milestones.put(MilestoneType.RELEASE_FEEDBACK.name(), form.getApplicantNotifiedDate());
        return milestones;
    }
}
