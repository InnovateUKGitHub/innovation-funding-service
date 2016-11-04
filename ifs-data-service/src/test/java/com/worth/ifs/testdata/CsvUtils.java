package com.worth.ifs.testdata;

import au.com.bytecode.opencsv.CSVReader;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.user.resource.UserStatus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * TODO DW - document this class
 */
class CsvUtils {

    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static List<ExternalUserLine> readExternalUsers() {
        return simpleMap(readCsvLines("external-users"), ExternalUserLine::new);
    }

    static List<InternalUserLine> readInternalUsers() {
        return simpleMap(readCsvLines("internal-users"), InternalUserLine::new);
    }

    static List<CompetitionLine> readCompetitions() {
        return simpleMap(readCsvLines("competitions"), CompetitionLine::new);
    }

    static List<ApplicationLine> readApplications() {
        return simpleMap(readCsvLines("applications"), ApplicationLine::new);
    }

    static List<ApplicationQuestionResponseLine> readApplicationQuestionResponses() {
        return simpleMap(readCsvLines("application-questions"), ApplicationQuestionResponseLine::new);
    }

    static class ApplicationQuestionResponseLine {

        String competitionName;
        String applicationName;
        String questionName;
        String value;
        String fileUpload;
        String answeredBy;
        String assignedTo;
        boolean markedAsComplete;

        private ApplicationQuestionResponseLine(List<String> line) {

            int i = 0;
            competitionName = line.get(i++);
            applicationName = line.get(i++);
            questionName = line.get(i++);
            value = nullable(line.get(i++));
            fileUpload = nullable(line.get(i++));
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
            description = nullable(line.get(i++));;
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
        String organisationType;
        String addressLine1;
        String addressLine2;
        String addressLine3;
        String town;
        String postcode;
        String county;
        OrganisationAddressType addressType;

        private UserLine(List<String> line) {

            int i = 0;
            emailAddress = line.get(i++);
            firstName = line.get(i++);
            lastName = line.get(i++);
            emailVerified = UserStatus.valueOf(line.get(i++)) == UserStatus.ACTIVE;
            organisationName = line.get(i++);
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


