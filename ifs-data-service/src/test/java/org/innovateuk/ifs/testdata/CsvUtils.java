package org.innovateuk.ifs.testdata;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

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

    static List<CompetitionFunderLine> readCompetitionFunders() {
        return simpleMap(readCsvLines("competition-funders"), CompetitionFunderLine::new);
    }

    static List<PublicContentGroupLine> readPublicContentGroups() {
        return simpleMap(readCsvLines("public-content-groups"), PublicContentGroupLine::new);
    }

    static List<PublicContentDateLine> readPublicContentDates() {
        return simpleMap(readCsvLines("public-content-dates"), PublicContentDateLine::new);
    }

    static List<ApplicationLine> readApplications() {
        return simpleMap(readCsvLines("applications"), ApplicationLine::new);
    }

    static List<AssessmentLine> readAssessments() {
        return simpleMap(readCsvLines("assessments"), AssessmentLine::new);
    }

    static List<AssessorResponseLine> readAssessorResponses() {
        return simpleMap(readCsvLines("assessor-responses"), AssessorResponseLine::new);
    }

    static List<InviteLine> readInvites() {
        return simpleMap(readCsvLines("invites"), InviteLine::new);
    }

    static List<ApplicationQuestionResponseLine> readApplicationQuestionResponses() {
        return simpleMap(readCsvLines("application-questions"), ApplicationQuestionResponseLine::new);
    }

    static List<ProjectLine> readProjects() {
        return simpleMap(readCsvLines("projects"), ProjectLine::new);
    }

    static class ProjectLine {

        String name;
        LocalDate startDate;
        String projectManager;
        boolean projectAddressAdded;
        boolean projectDetailsSubmitted;
        List<Pair<String, String>> financeContactsForOrganisations;
        String moFirstName;
        String moLastName;
        String moEmail;
        String moPhoneNumber;
        List<Triple<String, String, String>> bankDetailsForOrganisations;
        List<String> organisationsWithApprovedFinanceChecks;

        private ProjectLine(List<String> line) {
            int i = 0;
            name = line.get(i++);
            startDate = nullableDate(line.get(i++));
            projectManager = line.get(i++);
            projectAddressAdded = nullableBoolean(line.get(i++));
            projectDetailsSubmitted = nullableBoolean(line.get(i++));

            String financeContactsLine = line.get(i++);

            if (!isBlank(financeContactsLine)) {
                List<String> financeContactLines = asList(financeContactsLine.split("\n"));

                financeContactsForOrganisations = simpleMap(financeContactLines, fcLine -> {
                    String[] split = fcLine.split(":");
                    String organisationName = split[0].trim();
                    String financeContactEmail = split[1].trim();
                    return Pair.of(organisationName, financeContactEmail);
                });
            } else {
                financeContactsForOrganisations = emptyList();
            }

            moFirstName = nullable(line.get(i++));
            moLastName = nullable(line.get(i++));
            moEmail = nullable(line.get(i++));
            moPhoneNumber = nullable(line.get(i++));

            String bankDetailsLine = line.get(i++);

            if (!isBlank(bankDetailsLine)) {
                bankDetailsForOrganisations = simpleMap(bankDetailsLine.split("\n"), bdLine -> {
                    String[] split = bdLine.split(":");
                    String organisationName = split[0].trim();
                    String bankDetailsPart = split[1].trim();
                    String[] bankDetailsParts = bankDetailsPart.split("/");
                    String accountNumber = bankDetailsParts[0].trim();
                    String sortCode = bankDetailsParts[1].trim();
                    return Triple.of(organisationName, accountNumber, sortCode);
                });
            } else {
                bankDetailsForOrganisations = emptyList() ;
            }

            String financeChecksLine = line.get(i++);

            if (!isBlank(financeChecksLine)) {
                organisationsWithApprovedFinanceChecks = simpleMap(asList(financeChecksLine.split("\n")), String::trim);
            } else {
                organisationsWithApprovedFinanceChecks = emptyList();
            }
        }
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
        String innovationAreaName;
        String sentByEmail;
        ZonedDateTime sentOn;

        private InviteLine(List<String> line) {
            int i = 0;
            email = line.get(i++);
            hash = line.get(i++);
            name = line.get(i++);
            status = InviteStatus.valueOf(line.get(i++));
            type = line.get(i++);
            targetName = line.get(i++);
            ownerName = line.get(i++);
            innovationAreaName = line.get(i++);
            sentByEmail = nullable(line.get(i++));
            sentOn = nullableDateTime(line.get(i++));
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
            String filesUploadedLine = line.get(i++);
            filesUploaded = !isBlank(filesUploadedLine) ? asList(filesUploadedLine.split(",")) : emptyList();
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
        ZonedDateTime submittedDate;
        ApplicationStatusConstants status;
        boolean markFinancesComplete;
        String researchCategory;
        String innovationArea;
        boolean resubmission;
        boolean markDetailsComplete;

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
            markFinancesComplete = nullableBoolean(line.get(i++));
            researchCategory = nullable(line.get(i++));
            innovationArea = nullable(line.get(i++));
            resubmission = nullableBoolean(line.get(i++));
            markDetailsComplete = nullableBoolean(line.get(i++));
        }
    }

    static class AssessmentLine {

        String assessorEmail;
        String applicationName;
        AssessmentRejectOutcomeValue rejectReason;
        String rejectComment;
        AssessmentStates state;
        String feedback;
        String recommendComment;

        private AssessmentLine(List<String> line) {

            int i = 0;
            assessorEmail = line.get(i++);
            applicationName = line.get(i++);
            String rejectReasonString = nullable(line.get(i++));
            rejectReason = rejectReasonString != null ? AssessmentRejectOutcomeValue.valueOf(rejectReasonString) : null;
            rejectComment = nullable(line.get(i++));
            state = AssessmentStates.valueOf(line.get(i++));
            feedback = nullable(line.get(i++));
            recommendComment = nullable(line.get(i++));
        }
    }

    static class AssessorResponseLine {
        String competitionName;
        String applicationName;
        String assessorEmail;
        String shortName;
        String description;
        boolean isResearchCategory;
        String value;

        private AssessorResponseLine(List<String> line) {
            int i = 0;
            competitionName = line.get(i++);
            applicationName = line.get(i++);
            assessorEmail = line.get(i++);
            shortName = line.get(i++);
            description = line.get(i++);
            isResearchCategory = nullableBoolean(line.get(i++));
            value = line.get(i++);
        }
    }

    static class CompetitionLine {

        String name;
        String description;
        String type;
        String innovationArea;
        String innovationSector;
        String researchCategory;
        String collaborationLevel;
        String leadApplicantType;
        Integer researchRatio;
        Boolean resubmission;
        Boolean multiStream;
        ZonedDateTime openDate;
        ZonedDateTime briefingDate;
        ZonedDateTime submissionDate;
        ZonedDateTime allocateAssessorDate;
        ZonedDateTime assessorBriefingDate;
        ZonedDateTime assessorsNotifiedDate;
        ZonedDateTime assessorAcceptsDate;
        ZonedDateTime assessorEndDate;
        ZonedDateTime assessmentClosedDate;
        ZonedDateTime drawLineDate;
        ZonedDateTime assessmentPanelDate;
        ZonedDateTime panelDate;
        ZonedDateTime fundersPanelDate;
        ZonedDateTime fundersPanelEndDate;
        ZonedDateTime releaseFeedback;
        ZonedDateTime feedbackReleased;
        String leadTechnologist;
        String compExecutive;
        boolean setupComplete;
        String budgetCode;
        String code;
        String pafCode;
        String activityCode;
        Integer assessorCount;
        BigDecimal assessorPay;
        boolean published;
        String shortDescription;
        String fundingRange;
        String eligibilitySummary;
        String competitionDescription;
        FundingType fundingType;
        String projectSize;
        List<String> keywords;
        boolean nonIfs;
        String nonIfsUrl;


        private CompetitionLine(List<String> line) {

            int i = 0;
            name = nullable(line.get(i++));
            description = nullable(line.get(i++));
            type = nullable(line.get(i++));
            innovationArea = nullable(line.get(i++));
            innovationSector = nullable(line.get(i++));
            researchCategory = nullable(line.get(i++));
            collaborationLevel = nullable(line.get(i++));
            leadApplicantType = nullable(line.get(i++));
            researchRatio = nullableInteger(line.get(i++));
            resubmission = nullableBoolean(line.get(i++));
            multiStream = nullableBoolean(line.get(i++));
            openDate = nullableDateTime(line.get(i++));
            briefingDate = nullableDateTime(line.get(i++));
            submissionDate = nullableDateTime(line.get(i++));
            allocateAssessorDate = nullableDateTime(line.get(i++));
            assessorBriefingDate = nullableDateTime(line.get(i++));
            assessorsNotifiedDate = nullableDateTime(line.get(i++));
            assessorAcceptsDate = nullableDateTime(line.get(i++));
            assessorEndDate = nullableDateTime(line.get(i++));
            assessmentClosedDate = nullableDateTime(line.get(i++));
            drawLineDate = nullableDateTime(line.get(i++));
            assessmentPanelDate = nullableDateTime(line.get(i++));
            panelDate = nullableDateTime(line.get(i++));
            fundersPanelDate = nullableDateTime(line.get(i++));
            fundersPanelEndDate = nullableDateTime(line.get(i++));
            releaseFeedback = nullableDateTime(line.get(i++));
            feedbackReleased = nullableDateTime(line.get(i++));
            leadTechnologist = nullable((line.get(i++)));
            compExecutive = nullable((line.get(i++)));
            setupComplete = nullableBoolean(line.get(i++));
            pafCode = nullable(line.get(i++));
            budgetCode = nullable(line.get(i++));
            activityCode = nullable(line.get(i++));
            code = nullable(line.get(i++));
            assessorCount = nullableInteger(line.get(i++));
            assessorPay = nullableBigDecimal(line.get(i++));
            published = nullableBoolean(line.get(i++));
            shortDescription = nullable(line.get(i++));
            fundingRange = nullable(line.get(i++));
            eligibilitySummary = nullable(line.get(i++));
            competitionDescription = nullable(line.get(i++));
            fundingType = nullableEnum(line.get(i++), FundingType::valueOf);
            projectSize = nullable(line.get(i++));
            keywords = nullableSplittableString(line.get(i++));
            nonIfs = nullableBoolean(line.get(i++));
            nonIfsUrl = nullable(line.get(i++));
        }
    }

    static class CompetitionFunderLine {
        String competitionName;
        String funder;
        BigInteger funder_budget;
        boolean co_funder;

        private CompetitionFunderLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            funder = nullable(line.get(i++));
            funder_budget = nullableBigInteger(line.get(i++));
            co_funder = nullableBoolean(line.get(i++));
        }
    }

    static class PublicContentGroupLine {
        String competitionName;
        PublicContentSectionType section;
        String heading;
        String content;

        private PublicContentGroupLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            section = nullableEnum(line.get(i++), PublicContentSectionType::valueOf);
            heading = nullable(line.get(i++));
            content = nullable(line.get(i++));
        }
    }

    static class PublicContentDateLine {
        String competitionName;
        ZonedDateTime date;
        String content;

        private PublicContentDateLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            date = nullableDateTime(line.get(i++));
            content = nullable(line.get(i++));
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
        List<OrganisationAddressType> addressType;
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
                    simpleMap(asList(addressTypeLine.split(",")), OrganisationAddressType::valueOf) :
                emptyList();
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
        String rejectionReason;
        String rejectionComment;
        String skillAreas;
        BusinessType businessType;
        List<String> innovationAreas;
        String principalEmployer;
        String role;
        String professionalAffiliations;
        List<Map<String, String>> appointments;
        String financialInterests;
        List<Map<String, String>> familyAffiliations;
        String familyFinancialInterests;
        boolean agreementSigned;

        private AssessorUserLine(List<String> line) {

            super(line);
            int i = line.size() - 19;
            disability = Disability.fromDisplayName(line.get(i++));
            ethnicity = line.get(i++);
            gender = Gender.fromDisplayName(line.get(i++));
            competitionName = line.get(i++);
            hash = nullable(line.get(i++));
            inviteStatus = InviteStatus.valueOf(line.get(i++));
            rejectionReason = line.get(i++);
            rejectionComment = line.get(i++);
            skillAreas = line.get(i++);

            String businessTypeString = line.get(i++);
            businessType = !businessTypeString.isEmpty() ? BusinessType.valueOf(businessTypeString) : null;

            innovationAreas = simpleMap(line.get(i++).split("\n"), String::trim);
            principalEmployer = line.get(i++);
            role = line.get(i++);
            professionalAffiliations = line.get(i++);
            appointments = extractListOfMaps(line.get(i++));
            financialInterests = line.get(i++);
            familyAffiliations = extractListOfMaps(line.get(i++));
            familyFinancialInterests = line.get(i++);
            agreementSigned = Boolean.valueOf(line.get(i++));
        }

        private List<Map<String, String>> extractListOfMaps(String column) {
            if (column.isEmpty()) {
                return emptyList();
            }

            List<String> rows = asList(column.split("\n"));

            return simpleMap(rows, row -> {
                List<String> pairs = asList(row.split("\\|"));

                Map<String, String> kvMap = new HashMap<>();

                pairs.forEach(pair -> {
                    String[] keyValue = pair.split(":");

                    kvMap.put(keyValue[0].trim(), keyValue[1].trim());
                });

                return kvMap;
            });
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

    private static <T> T nullableEnum(String s, Function<String, T> valueOf) {
        return nullable(s) == null ? null : valueOf.apply(s);
    }

    private static LocalDate nullableDate(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return LocalDate.parse(s, DATE_PATTERN);
    }

    private static ZonedDateTime nullableDateTime(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return LocalDateTime.parse(s, DATE_TIME_PATTERN).atZone(TimeZoneUtil.UK_TIME_ZONE);
    }

    private static Integer nullableInteger(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return Integer.valueOf(s);
    }

    private static BigDecimal nullableBigDecimal(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return new BigDecimal(s);
    }

    private static BigInteger nullableBigInteger(String s) {
        String value = nullable(s);

        if (value == null) {
            return null;
        }

        return new BigInteger(s);
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

        if ("yes".equals(s.toLowerCase())) {
            return true;
        }

        return Boolean.parseBoolean(s);
    }

    private static List<String> nullableSplittableString(String s) {
        String value = nullable(s);

        if (value == null) {
            return Collections.emptyList();
        }

        return Splitter.on("!").trimResults().omitEmptyStrings().splitToList(s)
                .stream().map(StringUtils::normalizeSpace).collect(Collectors.toList());
    }

}


