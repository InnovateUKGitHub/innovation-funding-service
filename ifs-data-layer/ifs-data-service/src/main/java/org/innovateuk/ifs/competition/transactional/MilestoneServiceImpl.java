package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.MilestoneType.assessmentPeriodValues;
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

        if (competition.isAlwaysOpen()) {
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
    public ServiceResult<MilestoneResource> create(MilestoneResource milestoneResource) {
        return getCompetition(milestoneResource.getCompetitionId()).andOnSuccess(competition -> {
            AssessmentPeriod assessmentPeriod = null;
            if (assessmentPeriodValues().contains(milestoneResource.getType())) {
                if (competition.isAlwaysOpen()) {
                    if (milestoneResource.getAssessmentPeriodId() != null) {
                        assessmentPeriod = assessmentPeriodRepository.findById(milestoneResource.getAssessmentPeriodId()).orElse(null);
                    } else {
                        return serviceFailure(CommonFailureKeys.ASSESSMENT_PERIOD_MISSING_FROM_MILESTONE);
                    }
                } else {
                    assessmentPeriod = assessmentPeriodRepository.findFirstByCompetitionId(competition.getId())
                            .orElseGet(() -> assessmentPeriodRepository.save(new AssessmentPeriod(competition)));
                }
            }
            Milestone milestone = new Milestone(milestoneResource.getType(), competition, assessmentPeriod);
            return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));

        });
    }


    @Override
    @Transactional
    public ServiceResult<Void> updateCompletionStage(long competitionId, CompetitionCompletionStage completionStage) {

        return getCompetition(competitionId).andOnSuccessReturnVoid(competition -> {
            if (competition.getCompletionStage() != completionStage) {
                competition.setCompletionStage(completionStage);

                List<Milestone> currentMilestones = milestoneRepository.findAllByCompetitionId(competitionId);
                Set<MilestoneType> targetMilestoneTypes = EnumSet.allOf(MilestoneType.class)
                        .stream()
                        .filter(milestoneType -> milestoneTypeShouldBeCreatedAtCompletionStageChange(milestoneType, competition))
                        .filter(milestoneType -> milestoneType.getPriority() <= completionStage.getLastMilestone().getPriority())
                        .collect(Collectors.toSet());

                if (currentMilestones.size() > 1) {
                    List<Milestone> milestonesToDelete = currentMilestones.stream()
                            .filter(milestone -> milestone.getType().getPriority() > completionStage.getLastMilestone().getPriority())
                            .collect(Collectors.toList());

                    milestoneRepository.deleteAll(milestonesToDelete);

                    List<MilestoneType> currentMilestoneTypes = currentMilestones.stream()
                            .map(Milestone::getType)
                            .collect(toList());

                    targetMilestoneTypes.stream()
                            .filter(milestoneType -> !currentMilestoneTypes.contains(milestoneType))
                            .forEach(type -> create(new MilestoneResource(type, competition.getId())
                    ));
                } else {
                    targetMilestoneTypes.stream()
                            .filter(milestoneType -> !milestoneType.equals(MilestoneType.OPEN_DATE))
                            .forEach(type -> create(new MilestoneResource(type, competition.getId()))
                    );
                }
            }
        });
    }

    private boolean milestoneTypeShouldBeCreatedAtCompletionStageChange(MilestoneType milestoneType, Competition competition) {
        if (milestoneType.isOnlyNonIfs()) {
            return false;
        }
        if (!milestoneType.isPresetDate()) {
            return false;
        }
        if (competition.isAlwaysOpen()) {
            return !assessmentPeriodValues().contains(milestoneType);
        }
        return true;
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
