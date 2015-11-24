package com.worth.ifs.assessment.transactional;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.Failures.PROCESS_ROLE_INCORRECT_APPLICATION;
import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.Failures.PROCESS_ROLE_INCORRECT_TYPE;
import static com.worth.ifs.util.Either.right;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 *
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(AssessorServiceImpl.class);

    public enum Failures {
        UNEXPECTED_ERROR, //
        RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Override
    public Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Feedback feedback) {

        BiFunction<ProcessRole, Response, Either<ServiceFailure, ServiceSuccess>> updateFeedback = (role, response) -> {
            AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(role);
            responseFeedback.setAssessmentValue(feedback.getValue().orElse(null));
            responseFeedback.setAssessmentFeedback(feedback.getText().orElse(null));
            responseRepository.save(response);
            return right(new ServiceSuccess());
        };

        return handlingErrors(() -> {
            return getResponse(feedback.getResponseId()).map(response -> {
                return getProcessRole(feedback.getAssessorProcessRoleId()).map(processRole -> {
                    return validateProcessRoleCorrectType(processRole, UserRoleType.ASSESSOR).map(assessorRole -> {
                        return validateProcessRoleInApplication(response, processRole).map(roleInApplication -> {
                            return updateFeedback.apply(assessorRole, response);
                        });
                    });
                });
            });
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
        return response.getApplication().getId().equals(processRole.getApplication().getId()) ? successResponse(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_APPLICATION);
    };

    /**
     * Validate that the given ProcessRole is of the expected type.
     *
     * @param processRole
     * @param type
     * @return
     */
    private Either<ServiceFailure, ProcessRole> validateProcessRoleCorrectType(ProcessRole processRole, UserRoleType type) {
        return processRole.getRole().getName().equals(type.getName()) ? successResponse(processRole) : errorResponse(PROCESS_ROLE_INCORRECT_TYPE);
    };
}
