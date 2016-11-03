package com.worth.ifs.testdata;

import au.com.bytecode.opencsv.CSVReader;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.user.resource.UserStatus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * TODO DW - document this class
 */
class CsvUtils {

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static List<ExternalUserLine> readExternalUsers() {
        return simpleMap(readCsvLines("external-users"), ExternalUserLine::new);
    }

    static List<InternalUserLine> readInternalUsers() {
        return simpleMap(readCsvLines("internal-users"), InternalUserLine::new);
    }

    static List<CompetitionLine> readCompetitions() {
        return simpleMap(readCsvLines("competitions"), CompetitionLine::new);
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
            openDate = nullableDate(line.get(i++));
            submissionDate = nullableDate(line.get(i++));
            fundersPanelDate = nullableDate(line.get(i++));
            fundersPanelEndDate = nullableDate(line.get(i++));
            assessorAcceptsDate = nullableDate(line.get(i++));
            assessorEndDate = nullableDate(line.get(i++));
            setupComplete = Boolean.parseBoolean(line.get(i++));
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

    private static LocalDateTime nullableDate(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return LocalDateTime.parse(s, DATE_PATTERN);
    }

}


