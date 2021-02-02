package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.MilestoneDateValidationUtils.*;

@Service
public class AssessmentPeriodServiceImpl extends BaseTransactionalService implements AssessmentPeriodService {

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Override
    @Transactional
    public ServiceResult<Void> updateAssessmentPeriodMilestones(List<MilestoneResource> milestones) {
        ValidationMessages messages = new ValidationMessages();
        messages.addAll(validateCompetitionIdConsistency(milestones));

        messages.addAll(validateDates(milestones));
        messages.addAll(validateAssessmentPeriodDateOrder(milestones));

        if (messages.hasErrors()) {
            return serviceFailure(messages.getErrors());
        }

        milestoneRepository.saveAll(milestoneMapper.mapToDomain(milestones));
        return serviceSuccess();
    }


    @Override
    @Transactional
    public ServiceResult<List<MilestoneResource>> createAssessmentPeriodMilestones(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        int max = assessmentPeriodRepository.findAllByCompetitionId(competitionId).stream()
                .map(AssessmentPeriod::getIndex)
                .mapToInt(v -> v).max()
                .orElse(1);

        AssessmentPeriod assessmentPeriod = new AssessmentPeriod();
        assessmentPeriod.setCompetition(competition);
        assessmentPeriod.setIndex(max + 1);
        assessmentPeriodRepository.save(assessmentPeriod);

        List<Milestone> assessmentPeriodMilestones = new ArrayList<>();
        Milestone assessorBriefing = new Milestone(MilestoneType.ASSESSOR_BRIEFING, competition);
        assessorBriefing.setAssessmentPeriod(assessmentPeriod);
        Milestone assessorAccepts = new Milestone(MilestoneType.ASSESSOR_ACCEPTS, competition);
        assessorAccepts.setAssessmentPeriod(assessmentPeriod);
        Milestone assessorDeadline = new Milestone(MilestoneType.ASSESSOR_DEADLINE, competition);
        assessorDeadline.setAssessmentPeriod(assessmentPeriod);
        assessmentPeriodMilestones.add(assessorBriefing);
        assessmentPeriodMilestones.add(assessorAccepts);
        assessmentPeriodMilestones.add(assessorDeadline);

        return serviceSuccess(newArrayList(milestoneMapper.mapToResource(milestoneRepository.saveAll(assessmentPeriodMilestones))));
    }

    private ValidationMessages validateAssessmentPeriodDateOrder(List<MilestoneResource> milestoneResources) {
        ValidationMessages vm = new ValidationMessages();

        milestoneResources.stream()
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId))
                .values()
                .forEach(assessmentPeriodMilestones -> {
                    assessmentPeriodMilestones.sort(comparing(MilestoneResource::getType));
                    validatePresetMilestonesSequentialOrder(vm, assessmentPeriodMilestones);
                });

        return vm;
    }

    private ValidationMessages validateDates(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();
        Competition competition = competitionRepository.findById(milestones.get(0).getCompetitionId()).get();

        vm.addAll(validateDateNotNull(milestones));

        if (!competition.isNonIfs()) {
            vm.addAll(validateDateInFuture(milestones));
        }

        return vm;
    }

}

