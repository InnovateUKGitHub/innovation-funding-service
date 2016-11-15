package com.worth.ifs.application.controller;

import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.commons.error.exception.SummaryDataUnavailableException;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;

@RestController
@RequestMapping("/application/download")
public class ApplicationDownloadController {
    private static final Log LOG = LogFactory.getLog(ApplicationDownloadController.class);
    private static final String APPLICATION_SUMMARY_QUESTION_NAME = "Project summary";
    public static final int PROJECT_SUMMARY_COLUMN_WITH = 50; // the width in amount of letters.
    public static final String FONT_NAME = "Arial";
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationSummarisationService applicationSummarisationService;
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;
    private Integer cellCount = 0;
    private Integer rowCount = 0;
    private Integer headerCount = 0;

    @RequestMapping("/downloadByCompetition/{competitionId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> getDownloadByCompetitionId(@PathVariable("competitionId") Long competitionId) throws IOException {
        ServiceResult<List<Application>> applicationsResult = applicationService.getApplicationsByCompetitionIdAndStatus(competitionId, SUBMITTED_STATUS_IDS);
        
        List<Application> applications;
        if(applicationsResult.isSuccess()) {
        	applications = applicationsResult.getSuccessObject();
        } else {
        	LOG.error("failed call to get application summaries by competition and status for the download");
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        LOG.info(String.format("Generate download for %s applications with status ", applications.size()));

        POIXMLDocument wb;
        try {
        	wb = getExcelWorkbook(applications);
        } catch (SummaryDataUnavailableException e) {
        	LOG.error("unable to retrieve data required for the excel workbook");
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        headerRow = createHeaderCellWithValue(headerRow, "com.worth.ifs.Application ID");
        headerRow = createHeaderCellWithValue(headerRow, "com.worth.ifs.Application Title");
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
        	
            List<FormInputResponse> projectSummary = formInputResponseRepository.findByApplicationIdAndFormInputQuestionName(a.getId(), APPLICATION_SUMMARY_QUESTION_NAME);
            String projectSummaryString;
            if(projectSummary.isEmpty()){
                projectSummaryString = "";
            } else {
            	projectSummaryString = projectSummary.get(0).getValue();
            }
            
        	ServiceResult<BigDecimal> totalResult = applicationSummarisationService.getTotalProjectCost(a);
        	ServiceResult<BigDecimal> fundingSoughtResult = applicationSummarisationService.getFundingSought(a);
        	
        	BigDecimal total;
        	BigDecimal fundingSought;
        	
        	if(totalResult.isSuccess() && fundingSoughtResult.isSuccess()) {
        		total = totalResult.getSuccessObject();
        		fundingSought = fundingSoughtResult.getSuccessObject();
        	} else {
        		throw new SummaryDataUnavailableException();
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