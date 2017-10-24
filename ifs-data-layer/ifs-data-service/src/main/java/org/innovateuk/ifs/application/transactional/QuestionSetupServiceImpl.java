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
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;

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
    public ServiceResult<SetupStatusResource> markInSetupAsComplete(Long questionId, Long competitionId) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, questionId);
        setupStatus.setCompleted(Boolean.TRUE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Transactional
    @Override
    public ServiceResult<SetupStatusResource> markInSetupAsInComplete(Long questionId, Long competitionId) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, questionId);
        setupStatus.setCompleted(Boolean.FALSE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    public ServiceResult<Map<Long, Boolean>> getQuestionStatuses(Long competitionId, CompetitionSetupSection parentSection) {
        Optional<SetupStatusResource> optParentId = setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId)
                .getOptionalSuccessObject();
        List<SetupStatusResource> setupStatuses = getSetupStatusByTargetAndOptParentId(competitionId, optParentId);

        return ServiceResult.serviceSuccess(setupStatuses
                .stream()
                .filter(setupStatusResource -> setupStatusResource.getClassName().equals(Question.class.getName()))
                .collect(toMap(SetupStatusResource::getClassPk, SetupStatusResource::getCompleted)));
    }

    private List<SetupStatusResource> getSetupStatusByTargetAndOptParentId(Long competitionId, Optional<SetupStatusResource> optParentId) {
        if(optParentId.isPresent()) {
            return setupStatusService
                    .findByTargetClassNameAndTargetIdAndParentId(Competition.class.getName(), competitionId, optParentId.get().getId())
                    .getSuccessObjectOrThrowException();
        } else {
            return setupStatusService
                    .findByTargetClassNameAndTargetId(Competition.class.getName(), competitionId)
                    .getSuccessObjectOrThrowException();
        }
    }

    private SetupStatusResource findOrCreateSetupStatusResource(Long competitionId, Long questionId) {
        Optional<SetupStatusResource> setupStatusOpt = setupStatusService.findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId)
                .getOptionalSuccessObject();

        return setupStatusOpt.orElse(createNewSetupStatus(competitionId, questionId, APPLICATION_FORM));
    }

    private SetupStatusResource createNewSetupStatus(Long competitionId, Long questionId, CompetitionSetupSection parentSection) {
        return new SetupStatusResource(Question.class.getName(), questionId, getParentIdStatusObject(competitionId, parentSection), Competition.class.getName(), competitionId);
    }

    private Long getParentIdStatusObject(Long competitionId, CompetitionSetupSection parentSection) {
        Optional<SetupStatusResource> parentStatusOpt =
                setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId).getOptionalSuccessObject();

        return parentStatusOpt
                .orElse(competitionSetupService.markSectionInComplete(competitionId, parentSection)
                        .getSuccessObjectOrThrowException())
                .getId();
    }
}
