package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class QuestionSetupServiceImpl extends BaseTransactionalService implements QuestionSetupService {

    @Autowired
    private SetupStatusService setupStatusService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Transactional
    @Override
    public ServiceResult<SetupStatusResource> markQuestionInSetupAsComplete(Long questionId, Long competitionId, CompetitionSetupSection parentSection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, questionId, parentSection);
        setupStatus.setCompleted(Boolean.TRUE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Transactional
    @Override
    public ServiceResult<SetupStatusResource> markQuestionInSetupAsIncomplete(Long questionId, Long competitionId, CompetitionSetupSection parentSection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, questionId, parentSection);
        setupStatus.setCompleted(Boolean.FALSE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    public ServiceResult<Map<Long, Boolean>> getQuestionStatuses(Long competitionId, CompetitionSetupSection parentSection) {
        SetupStatusResource parentSectionStatus = setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId)
                .getSuccessObjectOrThrowException();
        List<SetupStatusResource> setupStatuses = getSetupStatusByTargetAndParentId(competitionId, parentSectionStatus);

        return ServiceResult.serviceSuccess(setupStatuses
                .stream()
                .filter(setupStatusResource -> setupStatusResource.getClassName().equals(Question.class.getName()))
                .collect(toMap(SetupStatusResource::getClassPk, SetupStatusResource::getCompleted)));
    }

    private List<SetupStatusResource> getSetupStatusByTargetAndParentId(Long competitionId, SetupStatusResource parentSectionStatus) {
        return setupStatusService
                    .findByTargetClassNameAndTargetIdAndParentId(Competition.class.getName(), competitionId, parentSectionStatus.getId())
                    .getSuccessObjectOrThrowException();
    }

    private SetupStatusResource findOrCreateSetupStatusResource(Long competitionId, Long questionId, CompetitionSetupSection parentSection) {
        Optional<SetupStatusResource> setupStatusOpt = setupStatusService.findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId)
                .getOptionalSuccessObject();

        return setupStatusOpt.orElseGet(() -> createNewSetupStatus(competitionId, questionId, parentSection));
    }

    private SetupStatusResource createNewSetupStatus(Long competitionId, Long questionId, CompetitionSetupSection parentSection) {
        return new SetupStatusResource(Question.class.getName(), questionId, getParentIdStatusObjectOrCreateOne(competitionId, parentSection), Competition.class.getName(), competitionId);
    }

    private Long getParentIdStatusObjectOrCreateOne(Long competitionId, CompetitionSetupSection parentSection) {
        Optional<SetupStatusResource> parentStatusOpt =
                setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId).getOptionalSuccessObject();

        return parentStatusOpt
                .orElseGet(() -> competitionSetupService.markSectionIncomplete(competitionId, parentSection)
                        .getSuccessObjectOrThrowException())
                .getId();
    }
}
