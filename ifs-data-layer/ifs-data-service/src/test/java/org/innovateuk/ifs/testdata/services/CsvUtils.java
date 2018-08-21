package org.innovateuk.ifs.testdata.services;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.BusinessType;
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
public class CsvUtils {

    static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<OrganisationLine> readOrganisations() {
        return simpleMap(readCsvLines("organisations"), OrganisationLine::new);
    }

    public static List<ExternalUserLine> readExternalUsers() {
        return simpleMap(readCsvLines("external-users"), ExternalUserLine::new);
    }

    public static List<InternalUserLine> readInternalUsers() {
        return simpleMap(readCsvLines("internal-users"), InternalUserLine::new);
    }

    public static List<AssessorUserLine> readAssessorUsers() {
        return simpleMap(readCsvLines("assessor-users"), AssessorUserLine::new);
    }

    public static List<CompetitionLine> readCompetitions() {
        return simpleMapWithIndex(readCsvLines("competitions"), CompetitionLine::new);
    }

    public static List<CompetitionFunderLine> readCompetitionFunders() {
        return simpleMap(readCsvLines("competition-funders"), CompetitionFunderLine::new);
    }

    public static List<PublicContentGroupLine> readPublicContentGroups() {
        return simpleMap(readCsvLines("public-content-groups"), PublicContentGroupLine::new);
    }

    public static List<PublicContentDateLine> readPublicContentDates() {
        return simpleMap(readCsvLines("public-content-dates"), PublicContentDateLine::new);
    }

    public static List<ApplicationLine> readApplications() {
        return simpleMap(readCsvLines("applications"), ApplicationLine::new);
    }

    public static List<AssessmentLine> readAssessments() {
        return simpleMap(readCsvLines("assessments"), AssessmentLine::new);
    }

    public static List<AssessorResponseLine> readAssessorResponses() {
        return simpleMap(readCsvLines("assessor-responses"), AssessorResponseLine::new);
    }

    public static List<InviteLine> readInvites() {
        return simpleMap(readCsvLines("invites"), InviteLine::new);
    }

    public static List<ApplicationQuestionResponseLine> readApplicationQuestionResponses() {
        return simpleMap(readCsvLines("application-questions"), ApplicationQuestionResponseLine::new);
    }

    public static List<ProjectLine> readProjects() {
        return simpleMap(readCsvLines("projects"), ProjectLine::new);
    }

    public static class ProjectLine {

        public String name;
        public LocalDate startDate;
        public String projectManager;
        public boolean projectAddressAdded;
        public List<Pair<String, String>> financeContactsForOrganisations;
        public String moFirstName;
        public String moLastName;
        public String moEmail;
        public String moPhoneNumber;
        public List<Triple<String, String, String>> bankDetailsForOrganisations;
        public List<String> organisationsWithApprovedFinanceChecks;
        public ProjectState projectState;

        private ProjectLine(List<String> line) {
            int i = 0;
            name = line.get(i++);
            startDate = nullableDate(line.get(i++));
            projectManager = line.get(i++);
            projectAddressAdded = nullableBoolean(line.get(i++));

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

            projectState = ProjectState.valueOf(line.get(i++));
        }
    }

    public static List<ApplicationOrganisationFinanceBlock> readApplicationFinances() {

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

                if (asList("Working days per year", "Grant claim", "Organisation size", "Work postcode").contains(categoryCell)) {
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

    public static class InviteLine {

        public String email;
        public String hash;
        public String name;
        public InviteStatus status;
        public String type;
        public String targetName;
        public String ownerName;
        public String innovationAreaName;
        public String sentByEmail;
        public ZonedDateTime sentOn;

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

    public static class ApplicationOrganisationFinanceBlock {

        public String competitionName;
        public String applicationName;
        public String organisationName;
        public List<ApplicationFinanceRow> rows = new ArrayList<>();

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

    public static class ApplicationFinanceRow {

        public String category;
        public List<String> metadata;

        private ApplicationFinanceRow(String category, List<String> costDetails) {
            this.category = category;
            this.metadata = costDetails;
        }
    }

    public static class ApplicationQuestionResponseLine {

        public String competitionName;
        public String applicationName;
        public String questionName;
        public String value;
        public List<String> filesUploaded;
        public String answeredBy;
        public String assignedTo;
        public boolean markedAsComplete;

        ApplicationQuestionResponseLine(List<String> line) {

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

    public static class ApplicationLine {

        public String title;
        public String competitionName;
        public LocalDate startDate;
        public Integer durationInMonths;
        public String leadApplicant;
        public List<String> collaborators;
        public ZonedDateTime submittedDate;
        public ApplicationState status;
        public boolean createApplicationResponses;
        public boolean createFinanceResponses;
        public boolean markFinancesComplete;
        public String researchCategory;
        public String innovationArea;
        public boolean resubmission;
        public boolean markDetailsComplete;
        public String ineligibleReason;

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
            status = ApplicationState.valueOf(line.get(i++).toUpperCase());
            createApplicationResponses = nullableBoolean(line.get(i++));
            createFinanceResponses = nullableBoolean(line.get(i++));
            markFinancesComplete = nullableBoolean(line.get(i++));
            researchCategory = nullable(line.get(i++));
            innovationArea = nullable(line.get(i++));
            resubmission = nullableBoolean(line.get(i++));
            markDetailsComplete = nullableBoolean(line.get(i++));
            ineligibleReason = nullable(line.get(i++));
        }
    }

    public static class AssessmentLine {

        public String assessorEmail;
        public String applicationName;
        public AssessmentRejectOutcomeValue rejectReason;
        public String rejectComment;
        public AssessmentState state;
        public String feedback;
        public String recommendComment;

        private AssessmentLine(List<String> line) {

            int i = 0;
            assessorEmail = line.get(i++);
            applicationName = line.get(i++);
            String rejectReasonString = nullable(line.get(i++));
            rejectReason = rejectReasonString != null ? AssessmentRejectOutcomeValue.valueOf(rejectReasonString) : null;
            rejectComment = nullable(line.get(i++));
            state = AssessmentState.valueOf(line.get(i++));
            feedback = nullable(line.get(i++));
            recommendComment = nullable(line.get(i++));
        }
    }

    public static class AssessorResponseLine {

        public String competitionName;
        public String applicationName;
        public String assessorEmail;
        public String shortName;
        public String description;
        public boolean isResearchCategory;
        public String value;

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

    public static class CompetitionLine {

        public int lineNumber;
        public String name;
        public String type;
        public List<String> innovationAreas;
        public String innovationSector;
        public String researchCategory;
        public String collaborationLevel;
        public List<OrganisationTypeEnum> leadApplicantTypes;
        public Integer researchRatio;
        public Boolean resubmission;
        public Boolean multiStream;
        public CompetitionStatus competitionStatus;
        public String leadTechnologist;
        public String compExecutive;
        public boolean setupComplete;
        public String budgetCode;
        public String code;
        public String pafCode;
        public String activityCode;
        public Integer assessorCount;
        public BigDecimal assessorPay;
        public Boolean hasAssessmentPanel;
        public Boolean hasInterviewStage;
        public AssessorFinanceView assessorFinanceView;
        public boolean published;
        public String shortDescription;
        public String fundingRange;
        public String eligibilitySummary;
        public String competitionDescription;
        public FundingType fundingType;
        public String projectSize;
        public List<String> keywords;
        public boolean inviteOnly;
        public boolean nonIfs;
        public String nonIfsUrl;
        public String includeApplicationTeamQuestion;

        private CompetitionLine(List<String> line, int lineNumber) {

            this.lineNumber = lineNumber;
            int i = 0;
            name = nullable(line.get(i++));
            type = nullable(line.get(i++));
            innovationAreas = nullableSplitOnNewLines(line.get(i++));
            innovationSector = nullable(line.get(i++));
            researchCategory = nullable(line.get(i++));
            collaborationLevel = nullable(line.get(i++));
            leadApplicantTypes = simpleMap(nullableSplitOnNewLines(line.get(i++)), OrganisationTypeEnum::valueOf);
            researchRatio = nullableInteger(line.get(i++));
            resubmission = nullableBoolean(line.get(i++));
            multiStream = nullableBoolean(line.get(i++));
            competitionStatus = CompetitionStatus.valueOf(line.get(i++));
            leadTechnologist = nullable((line.get(i++)));
            compExecutive = nullable((line.get(i++)));
            setupComplete = nullableBoolean(line.get(i++));
            pafCode = nullable(line.get(i++));
            budgetCode = nullable(line.get(i++));
            activityCode = nullable(line.get(i++));
            code = nullable(line.get(i++));
            assessorCount = nullableInteger(line.get(i++));
            assessorPay = nullableBigDecimal(line.get(i++));
            hasAssessmentPanel = nullableBoolean(line.get(i++));
            hasInterviewStage = nullableBoolean(line.get(i++));
            assessorFinanceView = nullableEnum(line.get(i++), AssessorFinanceView::valueOf);
            published = nullableBoolean(line.get(i++));
            shortDescription = nullable(line.get(i++));
            fundingRange = nullable(line.get(i++));
            eligibilitySummary = nullable(line.get(i++));
            competitionDescription = nullable(line.get(i++));
            fundingType = nullableEnum(line.get(i++), FundingType::valueOf);
            projectSize = nullable(line.get(i++));
            keywords = nullableSplittableString(line.get(i++));
            inviteOnly = nullableBoolean(line.get(i++));
            nonIfs = nullableBoolean(line.get(i++));
            nonIfsUrl = nullable(line.get(i++));
            includeApplicationTeamQuestion = nullable(line.get(i++));
        }
    }

    public static class CompetitionFunderLine {
        public String competitionName;
        public String funder;
        public BigInteger funder_budget;
        public boolean co_funder;

        private CompetitionFunderLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            funder = nullable(line.get(i++));
            funder_budget = nullableBigInteger(line.get(i++));
            co_funder = nullableBoolean(line.get(i++));
        }
    }

    public static class PublicContentGroupLine {
        public String competitionName;
        public PublicContentSectionType section;
        public String heading;
        public String content;

        private PublicContentGroupLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            section = nullableEnum(line.get(i++), PublicContentSectionType::valueOf);
            heading = nullable(line.get(i++));
            content = nullable(line.get(i++));
        }
    }

    public static class PublicContentDateLine {
        public String competitionName;
        public ZonedDateTime date;
        public String content;

        private PublicContentDateLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            date = nullableDateTime(line.get(i++));
            content = nullable(line.get(i++));
        }
    }


    public static abstract class UserLine {

        public String emailAddress;
        public String firstName;
        public String lastName;
        public boolean emailVerified;
        public String organisationName;
        public String phoneNumber;

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

    public static class OrganisationLine {

        public String name;
        public String organisationType;
        public String addressLine1;
        public String addressLine2;
        public String addressLine3;
        public String town;
        public String postcode;
        public String county;
        public List<OrganisationAddressType> addressType;
        public String companyRegistrationNumber;

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

    public static class AssessorUserLine extends UserLine {

        public String competitionName;
        public String hash;
        public InviteStatus inviteStatus;
        public String rejectionReason;
        public String rejectionComment;
        public String skillAreas;
        public BusinessType businessType;
        public List<String> innovationAreas;
        public String principalEmployer;
        public String role;
        public String professionalAffiliations;
        public List<Map<String, String>> appointments;
        public String financialInterests;
        public List<Map<String, String>> familyAffiliations;
        public String familyFinancialInterests;
        public boolean agreementSigned;

        private AssessorUserLine(List<String> line) {

            super(line);
            int i = line.size() - 16;
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

    public static class QuestionLine {
        public int ordinal;
        public String competitionName;
        public String heading;
        public String title;
        public String subtitle;

        private QuestionLine(List<String> line) {
            int i = 0;
            competitionName = nullable(line.get(i++));
            ordinal = nullableInteger(line.get(i++));
            heading = nullable(line.get(i++));
            title = nullable(line.get(i++));
            subtitle = nullable(line.get(i++));
        }
    }

    public static class ExternalUserLine extends UserLine {
        private ExternalUserLine(List<String> line) {
            super(line);
        }
    }

    public static class InternalUserLine extends UserLine {

        public List<String> roles;

        private InternalUserLine(List<String> line) {
            super(line);
            this.roles = simpleMap(line.get(line.size() - 1).split("&"), s -> s.trim());
        }
    }

    public static List<List<String>> readCsvLines(String csvName) {
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
        return ZonedDateTime.of(LocalDateTime.parse(s, DATE_TIME_PATTERN),
                TimeZoneUtil.UK_TIME_ZONE);
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

    public static List<String> nullableSplittableString(String s) {
        String value = nullable(s);

        if (value == null) {
            return emptyList();
        }

        return Splitter.on("!").trimResults().omitEmptyStrings().splitToList(s)
                .stream().map(StringUtils::normalizeSpace).collect(Collectors.toList());
    }

    public static List<String> nullableSplitOnNewLines(String s) {
        return nullable(s) != null ? simpleMap(s.split("\n"), String::trim) : emptyList();
    }
}