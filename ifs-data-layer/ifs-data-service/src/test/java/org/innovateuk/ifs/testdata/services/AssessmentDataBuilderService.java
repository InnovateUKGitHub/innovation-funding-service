package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.testdata.builders.AssessmentDataBuilder.newAssessmentData;
import static org.innovateuk.ifs.testdata.builders.AssessorDataBuilder.newAssessorData;
import static org.innovateuk.ifs.testdata.builders.AssessorInviteDataBuilder.newAssessorInviteData;
import static org.innovateuk.ifs.testdata.builders.AssessorResponseDataBuilder.newAssessorResponseData;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * TODO DW - document this class
 */
@Component
public class AssessmentDataBuilderService extends BaseDataBuilderService {

    private List<CsvUtils.AssessorResponseLine> assessorResponseLines;
    private List<CsvUtils.AssessorUserLine> assessorUserLines;
    private List<CsvUtils.InviteLine> inviteLines;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericApplicationContext applicationContext;

    private List<CsvUtils.AssessmentLine> assessmentLines;

    private AssessmentDataBuilder assessmentBuilder;
    private AssessorDataBuilder assessorUserBuilder;
    private AssessorResponseDataBuilder assessorResponseBuilder;
    private AssessorInviteDataBuilder assessorInviteUserBuilder;

    @PostConstruct
    public void readCsvs() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        assessmentBuilder = newAssessmentData(serviceLocator);
        assessorUserBuilder = newAssessorData(serviceLocator);
        assessorResponseBuilder = newAssessorResponseData(serviceLocator);
        assessorInviteUserBuilder = newAssessorInviteData(serviceLocator);

        assessmentLines = readAssessments();
        assessorUserLines = readAssessorUsers();
        assessorResponseLines = readAssessorResponses();
        inviteLines = readInvites();
    }

    public void createAssessments() {
        assessmentLines.forEach(this::createAssessment);
        assessorResponseLines.forEach(this::createAssessorResponse);
        assessmentLines.forEach(this::submitAssessment);
    }

    public void createAssessors() {
        assessorUserLines.forEach(this::createAssessor);
    }

    public void createNonRegisteredAssessorInvites() {
        List<CsvUtils.InviteLine> assessorInvites = simpleFilter(inviteLines, invite -> "COMPETITION".equals(invite.type));
        List<CsvUtils.InviteLine> nonRegisteredAssessorInvites = simpleFilter(assessorInvites, invite -> !userRepository.findByEmail(invite.email).isPresent());
        nonRegisteredAssessorInvites.forEach(line -> createAssessorInvite(assessorInviteUserBuilder, line));
    }

    private void createAssessment(AssessmentLine line) {
        assessmentBuilder.withAssessmentData(
                line.assessorEmail,
                line.applicationName,
                line.rejectReason,
                line.rejectComment,
                line.state,
                line.feedback,
                line.recommendComment)
                .build();
    }

    private void createAssessorResponse(AssessorResponseLine line) {
        assessorResponseBuilder.withAssessorResponseData(line.competitionName,
                line.applicationName,
                line.assessorEmail,
                line.shortName,
                line.description,
                line.isResearchCategory,
                line.value)
                .build();
    }

    private void submitAssessment(AssessmentLine line) {
        assessmentBuilder.withSubmission(
                line.applicationName,
                line.assessorEmail,
                line.state)
                .build();
    }

    private void createAssessor(AssessorUserLine line) {

        List<InviteLine> assessorInvitesForThisAssessor = simpleFilter(inviteLines, invite ->
                invite.email.equals(line.emailAddress) && invite.type.equals("COMPETITION"));

        AssessorDataBuilder builder = assessorUserBuilder;

        Optional<User> existingUser = userRepository.findByEmail(line.emailAddress);
        Optional<User> sentBy = userRepository.findByEmail("john.doe@innovateuk.test");
        Optional<ZonedDateTime> sentOn = Optional.of(ZonedDateTime.now());

        for (InviteLine invite : assessorInvitesForThisAssessor) {
            builder = builder.withInviteToAssessCompetition(
                    invite.targetName,
                    invite.email,
                    invite.name,
                    invite.hash,
                    invite.status,
                    existingUser,
                    invite.innovationAreaName,
                    sentBy,
                    sentOn
            );
        }

        String inviteHash = !isBlank(line.hash) ? line.hash : UUID.randomUUID().toString();
        String innovationArea = !line.innovationAreas.isEmpty() ? line.innovationAreas.get(0) : "";

        AssessorDataBuilder baseBuilder = builder.withInviteToAssessCompetition(
                line.competitionName,
                line.emailAddress,
                line.firstName + " " + line.lastName,
                inviteHash,
                line.inviteStatus,
                existingUser,
                innovationArea,
                sentBy,
                sentOn
        );

        if (!existingUser.isPresent()) {
            baseBuilder = baseBuilder.registerUser(
                    line.firstName,
                    line.lastName,
                    line.emailAddress,
                    line.phoneNumber,
                    line.ethnicity,
                    line.gender,
                    line.disability,
                    inviteHash
            );
        } else {
            baseBuilder = baseBuilder.addAssessorRole();
        }

        baseBuilder = baseBuilder.addSkills(line.skillAreas, line.businessType, line.innovationAreas);
        baseBuilder = baseBuilder.addAffiliations(
                line.principalEmployer,
                line.role,
                line.professionalAffiliations,
                line.appointments,
                line.financialInterests,
                line.familyAffiliations,
                line.familyFinancialInterests
        );

        if (line.agreementSigned) {
            baseBuilder = baseBuilder.addAgreementSigned();
        }

        if (!line.rejectionReason.isEmpty()) {
            baseBuilder = baseBuilder.rejectInvite(inviteHash, line.rejectionReason, line.rejectionComment);
        } else if (InviteStatus.OPENED.equals(line.inviteStatus)) {
            baseBuilder = baseBuilder.acceptInvite(inviteHash);
        }

        baseBuilder.build();
    }

    private void createAssessorInvite(AssessorInviteDataBuilder assessorInviteUserBuilder, InviteLine line) {
        assessorInviteUserBuilder.withInviteToAssessCompetition(
                line.targetName,
                line.email,
                line.name,
                line.hash,
                line.status,
                userRepository.findByEmail(line.email),
                line.innovationAreaName,
                userRepository.findByEmail(line.sentByEmail),
                Optional.of(line.sentOn)).
                build();
    }



}

