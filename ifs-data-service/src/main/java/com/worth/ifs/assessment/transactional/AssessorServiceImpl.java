package com.worth.ifs.assessment.transactional;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.security.FeedbackLookup;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.ServiceFailures.PROCESS_ROLE_INCORRECT_APPLICATION;
import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.ServiceFailures.PROCESS_ROLE_INCORRECT_TYPE;
import static com.worth.ifs.util.Either.right;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 *
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    @Autowired
    FeedbackLookup feedbackLookup;

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(AssessorServiceImpl.class);

    public enum ServiceFailures {
        UNEXPECTED_ERROR, //
        RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Override
    public Either<ServiceFailure, Feedback> updateAssessorFeedback(Feedback feedback) {

        BiFunction<ProcessRole, Response, Either<ServiceFailure, Feedback>> updateFeedback = (role, response) -> {
            AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(role);
            responseFeedback.setAssessmentValue(feedback.getValue().orElse(null));
            responseFeedback.setAssessmentFeedback(feedback.getText().orElse(null));
            responseRepository.save(response);
            return right(feedback);
        };

        return handlingErrors(() -> getResponse(feedback.getResponseId()).
            map(response -> getProcessRole(feedback.getAssessorProcessRoleId()).
            map(processRole -> validateProcessRoleCorrectType(processRole, UserRoleType.ASSESSOR).
            map(assessorRole -> validateProcessRoleInApplication(response, processRole).
            map(roleInApplication -> updateFeedback.apply(assessorRole, response))
        ))));
    }

    @Override
    public Either<ServiceFailure, Feedback> getFeedback(Feedback.Id id) {
        return handlingErrors(() -> {
            Feedback feedback = feedbackLookup.getFeedback(id);
            return right(feedback);
        });
    }

    /**
     * Validate that the given ProcessRole is correctly related to the given Application.
     *
     * @param response
     * @param processRole
     * @return
     */
    private Either<ServiceFailure, ProcessRole> validateProcessRoleInApplication(Response response, ProcessRole processRole) {
        return response.getApplication().getId().equals(processRole.getApplication().getId()) ? successBody(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_APPLICATION);
    }

    /**
     * Validate that the given ProcessRole is of the expected type.
     *
     * @param processRole
     * @param type
     * @return
     */
    private Either<ServiceFailure, ProcessRole> validateProcessRoleCorrectType(ProcessRole processRole, UserRoleType type) {
        return processRole.getRole().getName().equals(type.getName()) ? successBody(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_TYPE);
    }
}
