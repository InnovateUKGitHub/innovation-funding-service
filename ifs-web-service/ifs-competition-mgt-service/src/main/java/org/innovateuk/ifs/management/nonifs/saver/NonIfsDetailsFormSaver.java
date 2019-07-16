package org.innovateuk.ifs.management.nonifs.saver;

import org.hibernate.validator.internal.util.CollectionHelper;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Service to save {@link org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm}
 */
@Service
public class NonIfsDetailsFormSaver {

    private static List<MilestoneType> PUBLIC_MILESTONE_TYPES = asList(MilestoneType.OPEN_DATE, MilestoneType.SUBMISSION_DATE, MilestoneType.REGISTRATION_DATE, MilestoneType.NOTIFICATIONS);

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ServiceResult<Void> save(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        if(!competitionResource.isNonIfs()) {
            return serviceFailure(CommonFailureKeys.ONLY_NON_IFS_COMPETITION_VALID);
        }

        Map<String, GenericMilestoneRowForm> mappedMilestones = createMilestoneMap(form);
        mapFormFields(form, competitionResource);
        return competitionSetupRestService.update(competitionResource).toServiceResult().andOnSuccess(() -> {
            List<MilestoneResource> milestones = getPublicMilestones(competitionResource);
            return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, mappedMilestones, competitionResource.getId());
        });
    }

    private List<MilestoneResource> getPublicMilestones(CompetitionResource competitionResource) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId()).getSuccess();
        if (milestones.isEmpty()) {
            createPublicMilestones(competitionResource.getId());
            milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId()).getSuccess();
        }
        return milestones;
    }

    private void createPublicMilestones(Long competitionId) {
        PUBLIC_MILESTONE_TYPES.forEach(type -> milestoneRestService.create(type, competitionId).getSuccess());
    }

    private void mapFormFields(NonIfsDetailsForm form, CompetitionResource competitionResource) {
        competitionResource.setNonIfsUrl(form.getUrl());
        competitionResource.setName(form.getTitle());
        competitionResource.setInnovationSector(form.getInnovationSectorCategoryId());
        competitionResource.setInnovationAreas(CollectionHelper.asSet(form.getInnovationAreaCategoryId()));
        competitionResource.setFundingType(form.getFundingType());
    }

    private Map<String, GenericMilestoneRowForm> createMilestoneMap(NonIfsDetailsForm form) {
        Map<String, GenericMilestoneRowForm> milestones = new HashMap<>();
        milestones.put(MilestoneType.OPEN_DATE.name(), form.getOpenDate());
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), form.getCloseDate());
        milestones.put(MilestoneType.REGISTRATION_DATE.name(), form.getRegistrationCloseDate());
        milestones.put(MilestoneType.NOTIFICATIONS.name(), form.getApplicantNotifiedDate());
        return milestones;
    }
}
