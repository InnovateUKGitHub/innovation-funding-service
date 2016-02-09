package com.worth.ifs.assessment.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.security.FeedbackLookup;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.util.EntityLookupCallbacks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.handlingErrors;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 *
 * Created by dwatson on 06/10/15.
 */
@Service
public class AssessorServiceImpl extends BaseTransactionalService implements AssessorService {

    @Autowired
    private FeedbackLookup feedbackLookup;

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(AssessorServiceImpl.class);

    @Override
    public ServiceResult<Feedback> updateAssessorFeedback(Feedback.Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText) {

        return handlingErrors(() ->

                find(response(feedbackId.getResponseId()), role(ASSESSOR)).andOnSuccess((response, assessorRole) -> {

            Application application = response.getApplication();

            return getAssessorProcessRole(feedbackId.getAssessorUserId(), application.getId(), assessorRole).andOnSuccess(assessorProcessRole -> {

                Feedback feedback = new Feedback().setResponseId(response.getId()).
                        setAssessorUserId(assessorProcessRole.getId()).
                        setValue(feedbackValue).
                        setText(feedbackText);

                AssessorFeedback responseFeedback = response.getOrCreateResponseAssessorFeedback(assessorProcessRole);
                responseFeedback.setAssessmentValue(feedback.getValue().orElse(null));
                responseFeedback.setAssessmentFeedback(feedback.getText().orElse(null));
                responseRepository.save(response);
                return serviceSuccess(feedback);
            });
        }));
    }

    @Override
    public ServiceResult<Feedback> getFeedback(Feedback.Id id) {
        return handlingErrors(() -> {
            Feedback feedback = feedbackLookup.getFeedback(id);
            return serviceSuccess(feedback);
        });
    }

    private ServiceResult<ProcessRole> getAssessorProcessRole(Long assessorUserId, Long applicationId, Role assessorRole) {
        return find(() -> processRoleRepository.findByUserIdAndRoleAndApplicationId(assessorUserId, assessorRole, applicationId),
                notFoundError(ProcessRole.class, assessorUserId, assessorRole.getName(), applicationId)).
                andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail);
    }
}
