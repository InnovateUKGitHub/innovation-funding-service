package com.worth.ifs.application.controller;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.transactional.ApplicationSummaryService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/application/download")
public class ApplicationDownloadController {
    private static final Log LOG = LogFactory.getLog(ApplicationDownloadController.class);
    private static final Long APPLICATION_SUMMARY_FORM_INPUT_ID = 11L;
    public static final int PROJECT_SUMMARY_COLUMN_WITH = 50; // the width in amount of letters.
    @Autowired
    ApplicationSummaryService applicationSummaryService;
    @Autowired
    CostService costService;
    @Autowired
    FormInputResponseRepository formInputResponseRepository;

    @RequestMapping("/downloadByCompetition/{competitionId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> getDownloadByCompetitionId(@PathVariable("competitionId") Long competitionId) throws IOException {
        ApplicationStatusConstants status = ApplicationStatusConstants.SUBMITTED;
        List<Application> applications = applicationSummaryService.getApplicationSummariesByCompetitionIdAndStatus(competitionId, status.getId());
        LOG.info(String.format("Generate download for %s applications with status %s ", applications.size(), status.getName()));

        XSSFWorkbook wb = getExcelWorkbook(applications);
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
        int rowCount = 0;

        // ADD HEADER ROW
        int headerCount = 0;
        XSSFRow row = sheet.createRow(rowCount++);
        row.createCell(headerCount++).setCellValue("Application ID");
        row.createCell(headerCount++).setCellValue("Application Title");
        row.createCell(headerCount++).setCellValue("Lead Organisation");
        row.createCell(headerCount++).setCellValue("Lead first name");
        row.createCell(headerCount++).setCellValue("Lead last name");
        row.createCell(headerCount++).setCellValue("Email");
        row.createCell(headerCount++).setCellValue("Duration in Months");
        row.createCell(headerCount++).setCellValue("Number of partners");
        row.createCell(headerCount++).setCellValue("Project Summary");
        row.createCell(headerCount++).setCellValue("Total project cost");
        row.createCell(headerCount++).setCellValue("Funding sought");

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
            int cellCount = 0;
            row = sheet.createRow(rowCount++);
            row.createCell(cellCount++).setCellValue(a.getId());
            row.createCell(cellCount++).setCellValue(a.getName());
            row.createCell(cellCount++).setCellValue(a.getLeadOrganisation().getName());
            row.createCell(cellCount++).setCellValue(a.getLeadApplicant().getFirstName());
            row.createCell(cellCount++).setCellValue(a.getLeadApplicant().getLastName());
            row.createCell(cellCount++).setCellValue(a.getLeadApplicant().getEmail());
            row.createCell(cellCount++).setCellValue(a.getDurationInMonths());
            row.createCell(cellCount++).setCellValue(a.getProcessRoles().stream().collect(Collectors.groupingBy(ProcessRole::getOrganisation)).size());
            row.createCell(cellCount++).setCellValue(projectSummaryString);
            row.createCell(cellCount++).setCellValue(totalFormatted);
            row.createCell(cellCount++).setCellValue(fundingSoughtFormatted);
        }
        for (int i = 0; i < headerCount; i++) {
            sheet.autoSizeColumn(i);
        }
        // This column contains the project summary, so might be very long because of autoSize..
        sheet.setColumnWidth(8, PROJECT_SUMMARY_COLUMN_WITH * 256);

        return wb;
    }
}