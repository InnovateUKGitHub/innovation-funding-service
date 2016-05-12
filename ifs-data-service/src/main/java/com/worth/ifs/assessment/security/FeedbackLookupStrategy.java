package com.worth.ifs.assessment.security;


import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.assessment.resource.Feedback;
import com.worth.ifs.assessment.resource.Feedback.Id;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.util.CollectionFunctions.onlyElement;
import static com.worth.ifs.util.CollectionFunctions.onlyElementOrNull;
import static java.util.Optional.ofNullable;

@Component
@PermissionEntityLookupStrategies
public class FeedbackLookupStrategy {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionEntityLookupStrategy
    public Feedback getFeedback(Id id) {

        if (id.getResponseId() != null && id.getAssessorUserId() != null) {

            Response response = responseRepository.findOne(id.getResponseId());

            if (response != null) {

                Role assessorRole = onlyElement(roleRepository.findByName(UserRoleType.ASSESSOR.getName()));

                ProcessRole assessorProcessRole = onlyElementOrNull(processRoleRepository.findByUserIdAndRoleAndApplicationId(id.getAssessorUserId(), assessorRole, response.getApplication().getId()));

                if (assessorProcessRole != null) {

                    return response.getResponseAssessmentForAssessor(assessorProcessRole).map(assessorFeedback ->
                            new Feedback()
                                    .setId(id)
                                    .setText(ofNullable(assessorFeedback.getAssessmentFeedback()))
                                    .setValue(ofNullable(assessorFeedback.getAssessmentValue()))
                    ).orElse(null);
                }
            }
        }
        return null;
    }
}
