package com.worth.ifs.assessment.security;


import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Feedback.Id;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
@PermissionEntityLookupStrategies()
public class FeedbackLookup {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionEntityLookupStrategy
    public Feedback getFeedback(Id id) {
        if (id.getResponseId() != null && id.getAssessorProcessRoleId() != null) {
            Response response = responseRepository.findOne(id.getResponseId());
            if (response != null) {
                return response.getResponseAssessmentForAssessor(processRoleRepository.findOne(id.getAssessorProcessRoleId())).map(
                        assessorFeedback -> {
                            return new Feedback()
                                    .setId(id)
                                    .setText(ofNullable(assessorFeedback.getAssessmentFeedback()))
                                    .setValue(ofNullable(assessorFeedback.getAssessmentValue()));
                        }
                ).orElse(null);
            }
        }
        return null;
    }
}
