package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.transactional.ApplicationSummaryService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;

@RestController
@RequestMapping("/application/download")
public class ApplicationDownloadController {
    private static final Log LOG = LogFactory.getLog(ApplicationDownloadController.class);
    private static final Long APPLICATION_SUMMARY_FORM_INPUT_ID = 11L;
    public static final int PROJECT_SUMMARY_COLUMN_WITH = 50; // the width in amount of letters.
    public static final String FONT_NAME = "Arial";
    @Autowired
    ApplicationSummaryService applicationSummaryService;
    @Autowired
    CostService costService;
    @Autowired
    FormInputResponseRepository formInputResponseRepository;
    private Integer cellCount = 0;
    private Integer rowCount = 0;
    private Integer headerCount = 0;

    @RequestMapping("/downloadByCompetition/{competitionId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> getDownloadByCompetitionId(@PathVariable("competitionId") Long competitionId) throws IOException {
        List<Application> applications = applicationSummaryService.getApplicationSummariesByCompetitionIdAndStatus(competitionId, SUBMITTED_STATUS_IDS);
        LOG.info(String.format("Generate download for %s applications with status ", applications.size()));

        POIXMLDocument wb = getExcelWorkbook(applications);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);

        HttpHeaders httpHeaders = new HttpHeaders();
        // Prevent caching
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.add("Pragma", "no-cache");
        httpHeaders.add("Expires", "0");
        return new ResponseEntity<>(new ByteArrayResource(baos.toByteArray()), httpHeaders, HttpStatus.OK);
    }

    private XSSFWorkbook getExcelWorkbook(List<Application> applications) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Submitted Applications");


        XSSFFont font = wb.createFont();
        font.setFontName(FONT_NAME);
        CellStyle style = sheet.getColumnStyle(0);
        style.setFont(font);
        rowCount = 0;
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
            Optional<List<ApplicationFinanceResource>> financeTotalsOptional = costService.financeTotals(a.getId()).getOptionalSuccessObject();
            List<FormInputResponse> projectSummary = formInputResponseRepository.findByApplicationIdAndFormInputId(a.getId(), APPLICATION_SUMMARY_FORM_INPUT_ID);
            String projectSummaryString = "";
            if(!projectSummary.isEmpty()){
                projectSummaryString = projectSummary.get(0).getValue();
            }

            BigDecimal total = BigDecimal.ZERO;
            BigDecimal fundingSought = BigDecimal.ZERO;
            if(financeTotalsOptional.isPresent()){
                List<ApplicationFinanceResource> financeTotals;
                financeTotals = financeTotalsOptional.get();
                total = financeTotals.stream().map(t -> t.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
                fundingSought = financeTotals.stream()
                        .filter(of -> of != null && of.getGrantClaimPercentage() != null)
                        .map(of -> of.getTotalFundingSought())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            String totalFormatted = NumberFormat.getCurrencyInstance(Locale.UK).format(total);
            String fundingSoughtFormatted = NumberFormat.getCurrencyInstance(Locale.UK).format(fundingSought);

            // ADD APPLICATION ROW
            cellCount = 0;
            XSSFRow row = sheet.createRow(rowCount++);
            row = createCellWithValue(row, a.getFormattedId());
            row = createCellWithValue(row, a.getName());
            row = createCellWithValue(row, a.getLeadOrganisation().getName());
            row = createCellWithValue(row, a.getLeadApplicant().getFirstName());
            row = createCellWithValue(row, a.getLeadApplicant().getLastName());
            row = createCellWithValue(row, a.getLeadApplicant().getEmail());
            row = createCellWithValue(row, a.getDurationInMonths().toString());
            row = createCellWithValue(row, String.valueOf(a.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisation)).size()));
            row = createCellWithValue(row, projectSummaryString);
            row = createCellWithValue(row, totalFormatted);
            row = createCellWithValue(row, fundingSoughtFormatted);
        }


        for (int i = 0; i < headerCount; i++) {
            sheet.autoSizeColumn(i);
        }
        // This column contains the project summary, so might be very long because of autoSize..
        sheet.setColumnWidth(8, PROJECT_SUMMARY_COLUMN_WITH * 256);
        return wb;
    }

    private XSSFRow createHeaderCellWithValue(XSSFRow row, String value){
        XSSFCell cell = row.createCell(headerCount++);
        if(StringUtils.hasText(value)){
            cell.setCellValue(value);
        }
        return row;
    }

    private XSSFRow createCellWithValue(XSSFRow row, String value){
        XSSFCell cell = row.createCell(cellCount++);
        cell.setCellValue(value);
        return row;
    }
}