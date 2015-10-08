package com.worth.ifs.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.BiFunction;

import static com.worth.ifs.service.ServiceFailure.error;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;

/**
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl implements AssessorService {

    public enum Failures {
        RESPONSE_NOT_FOUND, //
        PROCESS_ROLE_NOT_FOUND, //
        PROCESS_ROLE_INCORRECT_TYPE, //
        PROCESS_ROLE_INCORRECT_APPLICATION, //
    }

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    public Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Long responseId, Long assessorProcessRoleId, Optional<String> feedbackValue, Optional<String> feedbackText) {

        BiFunction<ProcessRole, Response, Either<ServiceFailure, ServiceSuccess>> process = (role, response) -> {
            AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(role);
            responseFeedback.setAssessmentValue(feedbackValue.orElse(null));
            responseFeedback.setAssessmentFeedback(feedbackText.orElse(null));
            responseRepository.save(response);
            return right(new ServiceSuccess());
        };

        return getResponse(responseId).andThen(response -> {
            return getProcessRole(assessorProcessRoleId).andThen(processRole -> {
                return validateProcessRoleInApplication(response, processRole).andThen(roleInApplication -> {
                    return validateProcessRoleCorrectType(processRole, UserRoleType.ASSESSOR).andThen(assessorRole -> {
                        return process.apply(assessorRole, response);
                    });
                });
            });
        });
    }

    private Either<ServiceFailure, Response> getResponse(Long responseId) {
        return Optional.ofNullable(responseRepository.findOne(responseId)).map(AssessorServiceImpl::rightResponse)
                .orElse(left(error(Failures.RESPONSE_NOT_FOUND)));
    };

    private Either<ServiceFailure, ProcessRole> getProcessRole(Long processRoleId) {
        return Optional.ofNullable(processRoleRepository.findOne(processRoleId)).map(AssessorServiceImpl::rightResponse)
                .orElse(left(error(Failures.PROCESS_ROLE_NOT_FOUND)));
    };

    private Either<ServiceFailure, ProcessRole> validateProcessRoleInApplication(Response response, ProcessRole processRole) {
        return response.getApplication().getId().equals(processRole.getApplication().getId()) ? rightResponse(processRole) : left(error(Failures.PROCESS_ROLE_INCORRECT_APPLICATION));
    };

    private Either<ServiceFailure, ProcessRole> validateProcessRoleCorrectType(ProcessRole processRole, UserRoleType type) {
        return processRole.getRole().getName().equals(type.getName()) ? rightResponse(processRole) : left(error(Failures.PROCESS_ROLE_INCORRECT_TYPE));
    };

    private static <T> Either<ServiceFailure, T> rightResponse(T response) {
        return Either.<ServiceFailure, T> right(response);
    }

}
