package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.testdata.builders.data.AssessorData;
import org.innovateuk.ifs.user.domain.Ethnicity;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.testdata.builders.AssessorInviteDataBuilder.newAssessorInviteData;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates data for an Assessor on the platform
 */
public class AssessorDataBuilder extends BaseDataBuilder<AssessorData, AssessorDataBuilder> {

    public AssessorDataBuilder registerUser(String firstName,
                                            String lastName,
                                            String emailAddress,
                                            String phoneNumber,
                                            String ethnicity,
                                            Gender gender,
                                            Disability disability,
                                            String hash
    ) {
        return with(data -> doAs(systemRegistrar(), () -> {

            EthnicityResource ethnicityResource;

            if (!isBlank(ethnicity)) {
                Ethnicity ethnicitySelected = ethnicityRepository.findOneByDescription(ethnicity);
                ethnicityResource = newEthnicityResource().withId(ethnicitySelected.getId()).build();
            } else {
                ethnicityResource = newEthnicityResource().withId().build();
            }

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

            assessorService.registerAssessorByHash(hash, registration).getSuccessObjectOrThrowException();

            data.setUser(userService.findByEmail(data.getEmail()).getSuccessObjectOrThrowException());
            data.setEmail(emailAddress);
        }));
    }

    public AssessorDataBuilder withInviteToAssessCompetition(String competitionName,
                                                             String emailAddress,
                                                             String name,
                                                             String inviteHash,
                                                             InviteStatus inviteStatus,
                                                             Optional<User> existingUser,
                                                             String innovationAreaName,
                                                             Optional<User> sentBy,
                                                             Optional<ZonedDateTime> sentOn
    ) {
        return with(data -> {
            newAssessorInviteData(serviceLocator).withInviteToAssessCompetition(
                    competitionName,
                    emailAddress,
                    name,
                    inviteHash,
                    inviteStatus,
                    existingUser,
                    innovationAreaName,
                    sentBy,
                    sentOn
            ).build();

            data.setEmail(emailAddress);
        });
    }

    public AssessorDataBuilder addAssessorRole() {
        return with((AssessorData data) -> {
            User user = userRepository.findByEmail(data.getEmail()).get();

            Role assessorRole = roleRepository.findOneByName(UserRoleType.ASSESSOR.getName());

            if (!user.getRoles().contains(assessorRole)) {
                user.getRoles().add(assessorRole);
            }

            userRepository.save(user);

            UserResource userResource = doAs(systemRegistrar(), () -> userService.findByEmail(data.getEmail()).getSuccessObjectOrThrowException());

            data.setUser(userResource);
        });
    }

    public AssessorDataBuilder addSkills(String skillAreas, BusinessType businessType, List<String> innovationAreas) {
        return with((AssessorData data) -> {
            User user = userRepository.findOne(data.getUser().getId());
            Profile profile = profileRepository.findOne(user.getProfileId());

            Set<String> userInnovationAreaNames = profile.getInnovationAreas().stream()
                    .map(InnovationArea::getName)
                    .collect(toSet());

            Set<InnovationArea> additionalInnovationAreas = innovationAreas.stream()
                    .filter(innovationAreaName -> !userInnovationAreaNames.contains(innovationAreaName))
                    .map(innovationAreaName -> {
                        InnovationArea innovationArea = innovationAreaRepository.findByName(innovationAreaName);

                        if (innovationArea == null) {
                            throw new IllegalArgumentException("Invalid innovation area '" + innovationAreaName + "' for assessor user");
                        }

                        return innovationArea;
                    })
                    .collect(toSet());

            profile.addInnovationAreas(additionalInnovationAreas);

            profileRepository.save(profile);

            if (skillAreas.isEmpty() || businessType == null) {
                return;
            }

            doAs(data.getUser(), () -> {
                ProfileSkillsEditResource profileSkillsEditResource = new ProfileSkillsEditResource();
                profileSkillsEditResource.setBusinessType(businessType);
                profileSkillsEditResource.setSkillsAreas(skillAreas);
                profileSkillsEditResource.setUser(data.getUser().getId());

                userProfileService.updateProfileSkills(data.getUser().getId(), profileSkillsEditResource);
                testService.flushAndClearSession();
            });
        });
    }

    public AssessorDataBuilder addAffiliations(String principalEmployer,
                                               String role,
                                               String professionalAffiliations,
                                               List<Map<String, String>> appointments,
                                               String financialInterests,
                                               List<Map<String, String>> familyAffiliations,
                                               String familyFinancialInterests) {
        return with((AssessorData data) -> {
            if (checkAffiliationsEmpty(
                    principalEmployer,
                    role,
                    professionalAffiliations,
                    appointments,
                    financialInterests,
                    familyAffiliations,
                    familyFinancialInterests
            )) {
                return;
            }

            List<AffiliationResource> allAffiliations = combineLists(
                    combineLists(
                            mapAppointments(appointments),
                            mapFamilyAffiliations(familyAffiliations)
                    ),
                    AffiliationResourceBuilder.createPrincipalEmployer(principalEmployer, role),
                    AffiliationResourceBuilder.createProfessaionAffiliations(professionalAffiliations),
                    AffiliationResourceBuilder.createFinancialInterests(!financialInterests.isEmpty(), financialInterests),
                    AffiliationResourceBuilder.createFamilyFinancialInterests(!familyFinancialInterests.isEmpty(), familyFinancialInterests)
            );

            doAs(data.getUser(), () -> userProfileService.updateUserAffiliations(data.getUser().getId(), allAffiliations));
        });
    }

    public AssessorDataBuilder addAgreementSigned() {
        return with((AssessorData data) ->
                doAs(data.getUser(), () -> {
                    userProfileService.updateProfileAgreement(data.getUser().getId());
                    testService.flushAndClearSession();
                })
        );
    }

    private List<AffiliationResource> mapAppointments(List<Map<String, String>> appointments) {
        if (appointments.isEmpty()) {
            return singletonList(AffiliationResourceBuilder.createEmptyAppointments());
        }

        return simpleMap(appointments, appointment ->
                AffiliationResourceBuilder.createAppointment(appointment.get("Organisation"), appointment.get("Position"))
        );
    }

    private List<AffiliationResource> mapFamilyAffiliations(List<Map<String, String>> familyAffiliations) {
        if (familyAffiliations.isEmpty()) {
            return singletonList(AffiliationResourceBuilder.createEmptyFamilyAffiliations());
        }

        return simpleMap(familyAffiliations, familyAffiliation ->
                AffiliationResourceBuilder.createFamilyAffiliation(
                        familyAffiliation.get("Relation"),
                        familyAffiliation.get("Organisation"),
                        familyAffiliation.get("Position")
                )
        );
    }

    private boolean checkAffiliationsEmpty(String principalEmployer,
                                           String role,
                                           String professionalAffiliations,
                                           List<Map<String, String>> appointments,
                                           String financialInterests,
                                           List<Map<String, String>> familyAffiliations,
                                           String familyFinancialInterests) {
        return principalEmployer.isEmpty() &&
                role.isEmpty() &&
                professionalAffiliations.isEmpty() &&
                appointments.isEmpty() &&
                financialInterests.isEmpty() &&
                familyAffiliations.isEmpty() &&
                familyFinancialInterests.isEmpty();
    }

    public AssessorDataBuilder acceptInvite(String hash) {
        return with(data -> newAssessorInviteData(serviceLocator).acceptInvite(hash, data.getEmail()).build());
    }

    public AssessorDataBuilder rejectInvite(String hash, String rejectionReason, String rejectionComment) {
        return with(data -> newAssessorInviteData(serviceLocator).rejectInvite(hash, rejectionReason, Optional.of(rejectionComment)).build());
    }

    private RoleResource getAssessorRoleResource() {
        return roleService.findByUserRoleType(ASSESSOR).getSuccessObjectOrThrowException();
    }


    public static AssessorDataBuilder newAssessorData(ServiceLocator serviceLocator) {
        return new AssessorDataBuilder(emptyList(), serviceLocator);
    }

    private AssessorDataBuilder(List<BiConsumer<Integer, AssessorData>> multiActions,
                                ServiceLocator serviceLocator
    ) {
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
