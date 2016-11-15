package com.worth.ifs.testdata.builders;

import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.testdata.builders.data.AssessorData;
import com.worth.ifs.user.domain.Ethnicity;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.RoleResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.testdata.builders.AssessorInviteDataBuilder.newAssessorInviteData;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


public class AssessorDataBuilder extends BaseDataBuilder<AssessorData, AssessorDataBuilder> {

    public AssessorDataBuilder registerUser(String firstName, String lastName, String emailAddress, String phoneNumber, String ethnicity, Gender gender, Disability disability, String hash) {

        return with(data -> doAs(systemRegistrar(), () -> {

            Ethnicity ethnicitySelected = ethnicityRepository.findOneByDescription(ethnicity);
            EthnicityResource ethnicityResource = newEthnicityResource().withId(ethnicitySelected.getId()).build();

            UserRegistrationResource registration = newUserRegistrationResource().
                    withFirstName(firstName).
                    withLastName(lastName).
                    withEmail(emailAddress).
                    withPhoneNumber(phoneNumber).
                    withEthnicity(ethnicityResource).
                    withDisability(disability).
                    withGender(gender).
                    withPassword("Passw0rd").
                    withRoles(singletonList(getAssessorRoleResource())).
                    build();

//            assessorService.registerAssessorByHash(hash, registration).getSuccessObjectOrThrowException();
            registrationService.createUser(registration).andOnSuccess(created ->
                    registrationService.activateUser(created.getId())).getSuccessObjectOrThrowException();
        }));
    }

    public AssessorDataBuilder withInviteToAssessCompetition(String competitionName, String emailAddress, String name, String inviteHash) {
        return with(data -> newAssessorInviteData(serviceLocator).withInviteToAssessCompetition(competitionName, emailAddress, name, inviteHash));
    }

    private RoleResource getAssessorRoleResource() {
        return roleService.findByUserRoleType(ASSESSOR).getSuccessObjectOrThrowException();
    }


    public static AssessorDataBuilder newAssessorData(ServiceLocator serviceLocator) {
        return new AssessorDataBuilder(emptyList(), serviceLocator);
    }

    private AssessorDataBuilder(List<BiConsumer<Integer, AssessorData>> multiActions,
                                ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected AssessorDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorData>> actions) {
        return new AssessorDataBuilder(actions, serviceLocator);
    }

    @Override
    protected AssessorData createInitial() {
        return new AssessorData();
    }


}
