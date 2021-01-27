package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Milestones
 */
@Service
public class MilestoneServiceImpl extends BaseTransactionalService implements MilestoneService {

    private static final List<MilestoneType> PUBLIC_MILESTONES =
            asList(MilestoneType.OPEN_DATE, MilestoneType.REGISTRATION_DATE, MilestoneType.SUBMISSION_DATE, MilestoneType.NOTIFICATIONS);

    private static final List<MilestoneType> HORIZON_PUBLIC_MILESTONES =
            asList(MilestoneType.OPEN_DATE, MilestoneType.REGISTRATION_DATE);

    private static final List<MilestoneType> ALWAYS_OPEN_PUBLIC_MILESTONES = asList(MilestoneType.OPEN_DATE);

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Override
    public ServiceResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(Long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id)).andOnSuccessReturn(competition ->
                (List<MilestoneResource>)
                        milestoneMapper.mapToResource(milestoneRepository
                                .findByCompetitionIdAndTypeIn(id, competition.isH2020() ? HORIZON_PUBLIC_MILESTONES : PUBLIC_MILESTONES))
        );
    }

    @Override
    public ServiceResult<Boolean> allPublicDatesComplete(Long competitionId) {


        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId)).andOnSuccessReturn(competition -> {

            boolean isNonIfs = competition.isNonIfs();

            List<MilestoneType> milestonesRequired;

            if (isNonIfs) {
                milestonesRequired= PUBLIC_MILESTONES.stream()
                        .filter(milestoneType -> filterNonIfsOutOnIFSComp(milestoneType, true))
                        .collect(toList());
            } else {
                milestonesRequired = ifsAllPublicDatesComplete(competition);
            }

            List<Milestone> milestones = milestoneRepository
                    .findByCompetitionIdAndTypeIn(competitionId, milestonesRequired);

            return hasRequiredMilestones(milestones, milestonesRequired);
        });
    }

    private List<MilestoneType> ifsAllPublicDatesComplete(Competition competition) {
        List<MilestoneType> milestonesRequired;

        boolean isAlwaysOpen = BooleanUtils.isTrue(competition.getAlwaysOpen());

        if (isAlwaysOpen) {
            milestonesRequired = ALWAYS_OPEN_PUBLIC_MILESTONES.stream()
                    .filter(milestoneType -> milestoneType.getPriority() <= competition.getCompletionStage().getLastMilestone().getPriority())
                    .collect(toList());
        } else {
            milestonesRequired = PUBLIC_MILESTONES.stream()
                    .filter(milestoneType -> milestoneType.getPriority() <= competition.getCompletionStage().getLastMilestone().getPriority())
                    .filter(milestoneType -> filterNonIfsOutOnIFSComp(milestoneType, false))
                    .collect(toList());
        }

        return milestonesRequired;
    }

    private Boolean hasRequiredMilestones(List<Milestone> milestones, List<MilestoneType> milestonesRequired) {
        List<MilestoneType> milestoneTypes = milestones
                .stream()
                .map(milestone -> milestone.getType()).collect(toList());

        return milestoneTypes.containsAll(milestonesRequired)
                && milestones
                .stream()
                .noneMatch(milestone -> milestone.getDate() == null);
    }

    private boolean filterNonIfsOutOnIFSComp(MilestoneType milestoneType, boolean isNonIfs) {
        if (isNonIfs) {
            return !milestoneType.equals(MilestoneType.NOTIFICATIONS);
        } else {
            return !milestoneType.isOnlyNonIfs();
        }
    }

    @Override
    public ServiceResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long id) {
        return serviceSuccess((List<MilestoneResource>) milestoneMapper.mapToResource(milestoneRepository.findAllByCompetitionId(id)));
    }

    @Override
    public ServiceResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long id) {
        return find(milestoneRepository.findByTypeAndCompetitionId(type, id), notFoundError(MilestoneResource.class, type, id))
                .andOnSuccess(milestone -> serviceSuccess(milestoneMapper.mapToResource(milestone)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones) {
        ValidationMessages messages = validate(milestones);

        if (messages.hasErrors()) {
            return serviceFailure(messages.getErrors());
        }

        milestoneRepository.saveAll(milestoneMapper.mapToDomain(milestones));
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateMilestone(MilestoneResource milestoneResource) {
        milestoneRepository.save(milestoneMapper.mapToDomain(milestoneResource));
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateAssessmentPeriodMilestones(List<MilestoneResource> milestones) {
        ValidationMessages messages = new ValidationMessages();
        messages.addAll(validateCompetitionIdConsistency(milestones));

//        messages.addAll(validateDates(milestones));
        messages.addAll(validateAssessmentPeriodDateOrder(milestones));

        if (messages.hasErrors()) {
            return serviceFailure(messages.getErrors());
        }

        milestoneRepository.saveAll(milestoneMapper.mapToDomain(milestones));
        return serviceSuccess();
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

    private void validatePresetMilestonesSequentialOrder(ValidationMessages vm, List<MilestoneResource> milestones) {
        for (int i = 1; i < milestones.size(); i++) {
            MilestoneResource previous = milestones.get(i - 1);
            MilestoneResource current = milestones.get(i);

            if (current.getDate() != null && previous.getDate() != null && previous.getDate().isAfter(current.getDate())) {
                Error error = new Error("error.milestone.nonsequential", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        }
    }

    @Override
    @Transactional
    public ServiceResult<MilestoneResource> create(MilestoneType type, Long id) {
        Competition competition = competitionRepository.findById(id).orElse(null);

        Milestone milestone = new Milestone(type, competition);
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));
    }

    @Override
    @Transactional
    public ServiceResult<List<MilestoneResource>> createAssessmentPeriodMilestones(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        int max = assessmentPeriodRepository.findAllByCompetitionId(competitionId).stream()
                .map(AssessmentPeriod::getIndex).mapToInt(v -> v).max().orElse(1);

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

    @Override
    @Transactional
    public ServiceResult<Void> updateCompletionStage(long competitionId, CompetitionCompletionStage completionStage) {

        return getCompetition(competitionId).andOnSuccessReturnVoid(competition -> {
            if (competition.getCompletionStage() != completionStage) {
                competition.setCompletionStage(completionStage);

                List<Milestone> currentMilestones = milestoneRepository.findAllByCompetitionId(competitionId);
                List<Milestone> newMilestones = new ArrayList<>();

                if (currentMilestones.size() > 1) {
                    List<Milestone> milestonesToDelete = currentMilestones.stream()
                            .filter(milestone -> milestone.getType().getPriority() > completionStage.getLastMilestone().getPriority())
                            .collect(Collectors.toList());

                    milestoneRepository.deleteAll(milestonesToDelete);

                    List<MilestoneType> currentMilestoneTypes = currentMilestones.stream()
                            .map(milestone -> milestone.getType())
                            .collect(toList());

                    Stream.of(MilestoneType.presetValues()).filter(milestoneType -> !milestoneType.isOnlyNonIfs())
                            .filter(milestoneType -> !currentMilestoneTypes.contains(milestoneType)).forEach(type ->
                            newMilestones.add(new Milestone(type, competition))
                    );
                    milestoneRepository.saveAll(newMilestones);
                } else {
                    Stream.of(MilestoneType.presetValues()).filter(milestoneType -> !milestoneType.isOnlyNonIfs())
                            .filter(milestoneType -> milestoneType.getPriority() <= completionStage.getLastMilestone().getPriority())
                            .filter(milestoneType -> !milestoneType.equals(MilestoneType.OPEN_DATE)).forEach(type ->
                            newMilestones.add(new Milestone(type, competition))
                    );
                    milestoneRepository.saveAll(newMilestones);
                }
            }
        });
    }

    private ValidationMessages validate(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        vm.addAll(validateCompetitionIdConsistency(milestones));
        vm.addAll(validateDates(milestones));
        vm.addAll(validateDateOrder(milestones));

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

    private ValidationMessages validateDateInFuture(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.forEach(m -> {
            if (m.getDate() != null && m.getDate().isBefore(ZonedDateTime.now())) {
                Error error = new Error("error.milestone.pastdate", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        });

        return vm;
    }

    private ValidationMessages validateDateNotNull(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.forEach(m -> {
            if (m.getDate() == null) {
                Error error = new Error("error.milestone.nulldate", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        });

        return vm;
    }

    private ValidationMessages validateDateOrder(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.sort(comparing(MilestoneResource::getType));
        // preset milestones must be in the correct order
        List<MilestoneResource> presetMilestones = simpleFilter(milestones, milestoneResource -> milestoneResource.getType().isPresetDate());

        validatePresetMilestonesSequentialOrder(vm, presetMilestones);

        return vm;
    }

    private ValidationMessages validateCompetitionIdConsistency(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();
        Long firstMilestoneCompetitionId = milestones.get(0).getCompetitionId();

        boolean allCompetitionIdsMatch = milestones.stream().allMatch(milestone -> milestone.getCompetitionId().equals(firstMilestoneCompetitionId));

        if (!allCompetitionIdsMatch) {
            Error error = new Error("error.title.status.400", HttpStatus.BAD_REQUEST);
            vm.addError(error);
        }

        return vm;
    }
}
