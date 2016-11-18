package com.worth.ifs.testdata;

import au.com.bytecode.opencsv.CSVReader;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.UserStatus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Helper class to read from csvs in src/test/resources/testdata into basic structures for the purposes of generating
 * test data from it
 */
class CsvUtils {

    static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static List<OrganisationLine> readOrganisations() {
        return simpleMap(readCsvLines("organisations"), OrganisationLine::new);
    }

    static List<ExternalUserLine> readExternalUsers() {
        return simpleMap(readCsvLines("external-users"), ExternalUserLine::new);
    }

    static List<InternalUserLine> readInternalUsers() {
        return simpleMap(readCsvLines("internal-users"), InternalUserLine::new);
    }

    static List<AssessorUserLine> readAssessorUsers() {
        return simpleMap(readCsvLines("assessor-users"), AssessorUserLine::new);
    }

    static List<CompetitionLine> readCompetitions() {
        return simpleMap(readCsvLines("competitions"), CompetitionLine::new);
    }

    static List<ApplicationLine> readApplications() {
        return simpleMap(readCsvLines("applications"), ApplicationLine::new);
    }

    static List<AssessmentLine> readAssessments() {
        return simpleMap(readCsvLines("assessments"), AssessmentLine::new);
    }

    static List<InviteLine> readInvites() {
        return simpleMap(readCsvLines("invites"), InviteLine::new);
    }

    static List<ApplicationQuestionResponseLine> readApplicationQuestionResponses() {
        return simpleMap(readCsvLines("application-questions"), ApplicationQuestionResponseLine::new);
    }

    static List<ApplicationOrganisationFinanceBlock> readApplicationFinances() {

        List<List<String>> lines = simpleFilterNot(readCsvLines("application-finances"), line -> isBlank(line.get(0)));
        List<List<List<String>>> financeLinesPerOrganisation = new ArrayList<>();

        // read the entirety of a single organisation's finances into a single List<List<String>>
        for (int i = 0; i < lines.size(); i++) {

            List<List<String>> organisationFinanceLines = new ArrayList<>();

            for (; i < lines.size(); i++) {

                organisationFinanceLines.add(lines.get(i));

                if (i == lines.size() - 1 || "Finances".equals(lines.get(i + 1).get(0))) {
                    break;
                }
            }

            financeLinesPerOrganisation.add(organisationFinanceLines);
        }

        List<List<List<String>>> nonEmptyFinances = simpleFilter(financeLinesPerOrganisation, organisationLines -> organisationLines.size() > 1);

        // now process each organisation's finances one by one
        List<ApplicationOrganisationFinanceBlock> organisationFinances = simpleMap(nonEmptyFinances, organisationFinanceLines -> {

            ApplicationOrganisationFinanceBlock organisationCosts = new ApplicationOrganisationFinanceBlock(
                    organisationFinanceLines.get(0).subList(1, 4));

            for (int i = 1; i < organisationFinanceLines.size(); i++) {

                List<String> currentLine = organisationFinanceLines.get(i);

                if (!"Category".equals(currentLine.get(0))) {
                    throw new RuntimeException("Was expecting a Category row but got " + currentLine.get(0));
                }

                String categoryCell = currentLine.get(1);

                if (asList("Working days per year", "Grant claim", "Organisation size").contains(categoryCell)) {
                    organisationCosts.addRow(new ApplicationFinanceRow(categoryCell, singletonList(currentLine.get(2))));
                } else {

                    // skip over category-specific column headers and read the category's lines
                    for (i = i + 2; i < organisationFinanceLines.size(); i++) {

                        if ("Category".equals(organisationFinanceLines.get(i).get(0))) {
                            i--;
                            break;
                        }

                        List<String> costDetailsLine = organisationFinanceLines.get(i);
                        organisationCosts.addRow(new ApplicationFinanceRow(categoryCell, costDetailsLine));
                    }
                }
            }

            return organisationCosts;
        });

        return organisationFinances;
    }

    static class InviteLine {

        String email;
        String hash;
        String name;
        InviteStatus status;
        String type;
        String targetName;
        String ownerName;

        private InviteLine(List<String> line) {
            int i = 0;
            email = line.get(i++);
            hash = line.get(i++);
            name = line.get(i++);
            status = InviteStatus.valueOf(line.get(i++));
            type = line.get(i++);
            targetName = line.get(i++);
            ownerName = line.get(i++);
        }
    }

    static class ApplicationOrganisationFinanceBlock {

        String competitionName;
        String applicationName;
        String organisationName;
        List<ApplicationFinanceRow> rows = new ArrayList<>();

        private ApplicationOrganisationFinanceBlock(List<String> line) {

            int i = 0;
            competitionName = line.get(i++);
            applicationName = line.get(i++);
            organisationName = line.get(i++);
        }

        void addRow(ApplicationFinanceRow row) {
            rows.add(row);
        }
    }

    static class ApplicationFinanceRow {

        String category;
        List<String> metadata;

        private ApplicationFinanceRow(String category, List<String> costDetails) {
            this.category = category;
            this.metadata = costDetails;
        }
    }

    static class ApplicationQuestionResponseLine {

        String competitionName;
        String applicationName;
        String questionName;
        String value;
        List<String> filesUploaded;
        String answeredBy;
        String assignedTo;
        boolean markedAsComplete;

        private ApplicationQuestionResponseLine(List<String> line) {

            int i = 0;
            competitionName = line.get(i++);
            applicationName = line.get(i++);
            questionName = line.get(i++);
            value = nullable(line.get(i++));
            filesUploaded = asList(line.get(i++).split(","));
            answeredBy = nullable(line.get(i++));
            assignedTo = nullable(line.get(i++));
            markedAsComplete = nullableBoolean(line.get(i++));
        }
    }

    static class ApplicationLine {

        String title;
        String competitionName;
        LocalDate startDate;
        Integer durationInMonths;
        String leadApplicant;
        List<String> collaborators;
        LocalDateTime submittedDate;
        ApplicationStatusConstants status;

        private ApplicationLine(List<String> line) {
            int i = 0;
            title = line.get(i++);
            competitionName = line.get(i++);
            startDate = nullableDate(line.get(i++));
            durationInMonths = nullableInteger(line.get(i++));
            leadApplicant = line.get(i++);
            String collaboratorString = nullable(line.get(i++));
            collaborators = collaboratorString != null ? asList(collaboratorString.split(",")) : emptyList();
            submittedDate = nullableDateTime(line.get(i++));
            status = ApplicationStatusConstants.getFromName(line.get(i++));
        }
    }

    static class AssessmentLine {

        String assessorEmail;
        String applicationName;
        AssessmentStates state;

        private AssessmentLine(List<String> line) {

            int i = 0;
            assessorEmail = line.get(i++);
            applicationName = line.get(i++);
            state = AssessmentStates.valueOf(line.get(i++));
        }
    }

    static class CompetitionLine {

        String name;
        String description;
        String type;
        String innovationArea;
        String innovationSector;
        String researchCategory;
        LocalDateTime openDate;
        LocalDateTime submissionDate;
        LocalDateTime fundersPanelDate;
        LocalDateTime fundersPanelEndDate;
        LocalDateTime assessorAcceptsDate;
        LocalDateTime assessorEndDate;
        boolean setupComplete;

        private CompetitionLine(List<String> line) {

            int i = 0;
            name = nullable(line.get(i++));
            description = nullable(line.get(i++));
            ;
            type = line.get(i++);
            innovationArea = nullable(line.get(i++));
            innovationSector = nullable(line.get(i++));
            researchCategory = nullable(line.get(i++));
            openDate = nullableDateTime(line.get(i++));
            submissionDate = nullableDateTime(line.get(i++));
            fundersPanelDate = nullableDateTime(line.get(i++));
            fundersPanelEndDate = nullableDateTime(line.get(i++));
            assessorAcceptsDate = nullableDateTime(line.get(i++));
            assessorEndDate = nullableDateTime(line.get(i++));
            setupComplete = nullableBoolean(line.get(i++));
        }
    }

    static abstract class UserLine {

        String emailAddress;
        String firstName;
        String lastName;
        boolean emailVerified;
        String organisationName;
        String phoneNumber;

        private UserLine(List<String> line) {

            int i = 0;
            emailAddress = line.get(i++);
            firstName = line.get(i++);
            lastName = line.get(i++);
            emailVerified = UserStatus.valueOf(line.get(i++)) == UserStatus.ACTIVE;
            organisationName = line.get(i++);
            phoneNumber = nullable(line.get(i++));
        }
    }

    static class OrganisationLine {

        String name;
        String organisationType;
        String addressLine1;
        String addressLine2;
        String addressLine3;
        String town;
        String postcode;
        String county;
        OrganisationAddressType addressType;
        String companyRegistrationNumber;

        private OrganisationLine(List<String> line) {

            int i = 0;
            name = line.get(i++);
            organisationType = line.get(i++);
            addressLine1 = nullable(line.get(i++));
            addressLine2 = nullable(line.get(i++));
            addressLine3 = nullable(line.get(i++));
            town = nullable(line.get(i++));
            postcode = nullable(line.get(i++));
            county = nullable(line.get(i++));
            String addressTypeLine = nullable(line.get(i++));
            addressType = addressTypeLine != null ?
                    OrganisationAddressType.valueOf(addressTypeLine) : null;
            companyRegistrationNumber = nullable(line.get(i++));
        }
    }

    static class AssessorUserLine extends UserLine {

        Disability disability;
        Gender gender;
        String ethnicity;
        String competitionName;
        String hash;
        InviteStatus inviteStatus;

        private AssessorUserLine(List<String> line) {

            super(line);
            int i = line.size() - 6;
            disability = Disability.fromDisplayName(line.get(i++));
            ethnicity = line.get(i++);
            gender = Gender.fromDisplayName(line.get(i++));
            competitionName = line.get(i++);
            hash = nullable(line.get(i++));
            inviteStatus = InviteStatus.valueOf(line.get(i++));
        }
    }

    static class ExternalUserLine extends UserLine {
        private ExternalUserLine(List<String> line) {
            super(line);
        }
    }

    static class InternalUserLine extends UserLine {

        String role;

        private InternalUserLine(List<String> line) {
            super(line);
            this.role = line.get(line.size() - 1);
        }
    }

    private static List<List<String>> readCsvLines(String csvName) {
        try {
            File file = new File(CsvUtils.class.getResource("/testdata/" + csvName + ".csv").toURI());
            CSVReader reader = new CSVReader(new FileReader(file), ',', '"');
            List<String[]> data = reader.readAll();
            List<List<String>> lists = simpleMap(data, Arrays::asList);
            return lists.subList(1, lists.size());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String nullable(String s) {
        return isBlank(s) || "N".equals(s) ? null : s;
    }

    private static LocalDate nullableDate(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return LocalDate.parse(s, DATE_PATTERN);
    }

    private static LocalDateTime nullableDateTime(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return LocalDateTime.parse(s, DATE_TIME_PATTERN);
    }

    private static Integer nullableInteger(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return Integer.valueOf(s);
    }

    private static boolean nullableBoolean(String s) {
        String value = nullable(s);

        if (value == null) {
            return false;
        }

        if ("0".equals(s)) {
            return false;
        }

        if ("1".equals(s)) {
            return true;
        }

        return Boolean.parseBoolean(s);
    }

}


