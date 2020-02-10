package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.testdata.builders.data.AssessorData;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.testdata.builders.AssessorInviteDataBuilder.newAssessorInviteData;
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
                                            String hash
    ) {
        return with(data -> doAs(systemRegistrar(), () -> {
            UserRegistrationResource registration = newUserRegistrationResource().
                    withFirstName(firstName).
                    withLastName(lastName).
                    withEmail(emailAddress).
                    withPhoneNumber(phoneNumber).
                    withPassword("Passw0rd").
                    withRoles(singletonList(Role.ASSESSOR)).
                    build();

            assessorService.registerAssessorByHash(hash, registration).getSuccess();

            data.setUser(userService.findByEmail(data.getEmail()).getSuccess());
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

            testService.doWithinTransaction(() -> {

                User user = userRepository.findByEmail(data.getEmail()).get();

                if (!user.getRoles().contains(Role.ASSESSOR)) {
                    user.getRoles().add(Role.ASSESSOR);
                }

                userRepository.save(user);
            });

            UserResource userResource = doAs(systemRegistrar(), () -> userService.findByEmail(data.getEmail()).getSuccess());

            data.setUser(userResource);
        });
    }

    public AssessorDataBuilder addSkills(String skillAreas, BusinessType businessType, List<String> innovationAreas) {
        return with((AssessorData data) -> {

            testService.doWithinTransaction(() -> {

                User user = userRepository.findById(data.getUser().getId()).get();
                Profile profile = profileRepository.findById(user.getProfileId()).get();

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
            });

            if (skillAreas.isEmpty() || businessType == null) {
                return;
            }

            doAs(data.getUser(), () -> {
                ProfileSkillsEditResource profileSkillsEditResource = new ProfileSkillsEditResource();
                profileSkillsEditResource.setBusinessType(businessType);
                profileSkillsEditResource.setSkillsAreas(skillAreas);
                profileSkillsEditResource.setUser(data.getUser().getId());

                profileService.updateProfileSkills(data.getUser().getId(), profileSkillsEditResource);
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

            AffiliationListResource allAffiliationsList = new AffiliationListResource(allAffiliations);

            doAs(data.getUser(), () -> affiliationService.updateUserAffiliations(data.getUser().getId(), allAffiliationsList));
        });
    }

    public AssessorDataBuilder addAgreementSigned() {
        return with((AssessorData data) ->
                doAs(data.getUser(), () -> {
                    profileService.updateProfileAgreement(data.getUser().getId());
                })
        );
    }

    public AssessorDataBuilder updateRoleProfileState(RoleProfileState roleProfileState) {
        return with((AssessorData data) ->
                doAs(projectFinanceUser(), () -> {
                    roleProfileStatusService.updateUserStatus(data.getUser().getId(),
                            new RoleProfileStatusResource(data.getUser().getId(), roleProfileState, ProfileRole.ASSESSOR,
                            roleProfileState.equals(RoleProfileState.DISABLED) ? "The user no longer works as an assessor."
                                    : "The user is unavailable to work as an assessor until further notice.")).getSuccess();
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