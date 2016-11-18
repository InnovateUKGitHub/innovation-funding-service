package com.worth.ifs.testdata.builders;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ActivityType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Generates Assessments for Applications so that Assessors may start assessing them
 */
public class AssessmentDataBuilder extends BaseDataBuilder<Void, AssessmentDataBuilder> {

    public AssessmentDataBuilder withAssessmentData(String assessorEmail, String applicationName, AssessmentStates state) {
        return with(data -> {

            Application application = applicationRepository.findByName(applicationName).get(0);
            User assessor = userRepository.findByEmail(assessorEmail).get();
            Role assessorRole = roleRepository.findOneByName(UserRoleType.ASSESSOR.getName());
            Organisation organisation = organisationRepository.findOneByName("Innovate UK");

            ProcessRole processRole = new ProcessRole(assessor, application, assessorRole, organisation);
            ProcessRole newProcessRole = processRoleRepository.save(processRole);

            ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION_ASSESSMENT, state.getBackingState());

            Assessment assessment = new Assessment(application, newProcessRole);
            assessment.setActivityState(activityState);
            assessmentRepository.save(assessment);
        });
    }

    public static AssessmentDataBuilder newAssessmentData(ServiceLocator serviceLocator) {

        return new AssessmentDataBuilder(emptyList(), serviceLocator);
    }

    private AssessmentDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                  ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected AssessmentDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new AssessmentDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
