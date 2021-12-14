package org.innovateuk.ifs.application.controller;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.ApplicationSummarisationService;
import org.innovateuk.ifs.commons.exception.SummaryDataUnavailableException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@RestController
@RequestMapping("/application/download")
public class ApplicationDownloadController {
    private static final Log LOG = LogFactory.getLog(ApplicationDownloadController.class);
    private static final String APPLICATION_SUMMARY_QUESTION_NAME = "Project summary";
    private static final int PROJECT_SUMMARY_COLUMN_WITH = 50; // the width in amount of letters.
    private static final String FONT_NAME = "Arial";
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationSummarisationService applicationSummarisationService;
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    private Integer cellCount = 0;
    private Integer headerCount = 0;

    private static final Collection<ApplicationState> SUBMITTED_STATUSES = asList(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED);

    @GetMapping("/download-by-competition/{competitionId}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> getDownloadByCompetitionId(@PathVariable("competitionId") Long competitionId) throws IOException {
        ServiceResult<List<Application>> applicationsResult = applicationService.getApplicationsByCompetitionIdAndState(competitionId, SUBMITTED_STATUSES);

        List<Application> applications;
        if (applicationsResult.isSuccess()) {
            applications = applicationsResult.getSuccess();
        } else {
            LOG.error("failed call to get application summaries by competition and status for the download");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOG.info(String.format("Generate download for %s applications with status ", applications.size()));

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            populateExcelWorkbook(wb, applications);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            HttpHeaders httpHeaders = new HttpHeaders();
            // Prevent caching
            httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
            httpHeaders.add("Pragma", "no-cache");
            httpHeaders.add("Expires", "0");
            return new ResponseEntity<>(new ByteArrayResource(baos.toByteArray()), httpHeaders, HttpStatus.OK);
        } catch (SummaryDataUnavailableException e) {
            LOG.error("unable to retrieve data required for the excel workbook", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void populateExcelWorkbook(XSSFWorkbook wb, List<Application> applications) {
        XSSFSheet sheet = wb.createSheet("Submitted Applications");

        XSSFFont font = wb.createFont();
        font.setFontName(FONT_NAME);
        CellStyle style = sheet.getColumnStyle(0);
        style.setFont(font);
        Integer rowCount = 0;
        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.cloneStyleFrom(style);
        XSSFFont headerFont = headerStyle.getFont();
        headerFont.setBold(true);
        headerFont.setItalic(true);

        // ADD HEADER ROW
        headerCount = 0;
        XSSFRow headerRow = sheet.createRow(rowCount++);
        headerRow = createHeaderCellWithValue(headerRow, "Application ID");
        headerRow = createHeaderCellWithValue(headerRow, "Application Title");
        headerRow = createHeaderCellWithValue(headerRow, "Lead Organisation");
        headerRow = createHeaderCellWithValue(headerRow, "Lead first name");
        headerRow = createHeaderCellWithValue(headerRow, "Lead last name");
        headerRow = createHeaderCellWithValue(headerRow, "Email");
        headerRow = createHeaderCellWithValue(headerRow, "Duration in Months");
        headerRow = createHeaderCellWithValue(headerRow, "Number of partners");
        headerRow = createHeaderCellWithValue(headerRow, "Project Summary");
        headerRow = createHeaderCellWithValue(headerRow, "Total project cost");
        headerRow = createHeaderCellWithValue(headerRow, "Funding sought");

        headerRow.setRowStyle(headerStyle);
        for (int i = 0; i < headerCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setDefaultColumnStyle(i, style);
        }

        for (Application a : applications) {
            // PREPARE APPLICATION INFORMATION

            FormInputResponse projectSummary = formInputResponseRepository.findOneByApplicationIdAndFormInputQuestionName(a.getId(), APPLICATION_SUMMARY_QUESTION_NAME);
            String projectSummaryString = projectSummary == null ? "" : projectSummary.getValue();

            ServiceResult<BigDecimal> totalResult = applicationSummarisationService.getTotalProjectCost(a);
            ServiceResult<BigDecimal> fundingSoughtResult = applicationSummarisationService.getFundingSought(a);

            BigDecimal total;
            BigDecimal fundingSought;

            if (totalResult.isSuccess() && fundingSoughtResult.isSuccess()) {
                total = totalResult.getSuccess();
                fundingSought = fundingSoughtResult.getSuccess();
            } else {
                throw new SummaryDataUnavailableException();
            }

            String totalFormatted = NumberFormat.getCurrencyInstance(Locale.UK).format(total);
            String fundingSoughtFormatted = NumberFormat.getCurrencyInstance(Locale.UK).format(fundingSought);

            ProcessRole leadRole = a.getLeadApplicantProcessRole();
            Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).get();

            // ADD APPLICATION ROW
            cellCount = 0;
            XSSFRow row = sheet.createRow(rowCount++);
            row = createCellWithValue(row, a.getId().toString());
            row = createCellWithValue(row, a.getName());
            row = createCellWithValue(row, leadOrganisation.getName());
            row = createCellWithValue(row, a.getLeadApplicant().getFirstName());
            row = createCellWithValue(row, a.getLeadApplicant().getLastName());
            row = createCellWithValue(row, a.getLeadApplicant().getEmail());
            row = createCellWithValue(row, a.getDurationInMonths().toString());
            row = createCellWithValue(row, String.valueOf(a.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisationId)).size()));
            row = createCellWithValue(row, projectSummaryString);
            row = createCellWithValue(row, totalFormatted);
            createCellWithValue(row, fundingSoughtFormatted);
        }

        for (int i = 0; i < headerCount; i++) {
            sheet.autoSizeColumn(i);
        }
        // This column contains the project summary, so might be very long because of autoSize..
        sheet.setColumnWidth(8, PROJECT_SUMMARY_COLUMN_WITH * 256);
    }

    private XSSFRow createHeaderCellWithValue(XSSFRow row, String value) {
        XSSFCell cell = row.createCell(headerCount++);
        if (StringUtils.hasText(value)) {
            cell.setCellValue(value);
        }
        return row;
    }

    private XSSFRow createCellWithValue(XSSFRow row, String value) {
        XSSFCell cell = row.createCell(cellCount++);
        cell.setCellValue(value);
        return row;
    }
}
