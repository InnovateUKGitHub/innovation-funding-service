package com.worth.ifs.file.service;

import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.gol.YearlyGOLProfileTable;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.io.File.separator;
import static org.junit.Assert.assertTrue;

/**
 *
 **/
public class FreemarkerGOLTemplateRendererIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private FileTemplateRenderer renderer;

    @Test
    public void testGenerateGrantOfferLetterHtmlFile() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "LeadContact", "Steve Smith",
                "LeadOrgName", "test2",
                "Address1", "Address1",
                "Address2", "Address2",
                "Address3", "Address3",
                "TownCity", "TownCity",
                "PostCode", "PostCode",
                "Date", LocalDateTime.now().toString(),
                "CompetitionName", "CompetitionName",
                "ProjectTitle", "ProjectTitle",
                "ProjectStartDate", LocalDateTime.now().toString(),
                "ProjectLength", "3",
                "ApplicationNumber", "12334",
                "TableData", getYearlyGOLProfileTable()
        );
        assertRenderedGOLFileExpectedLines("grant_offer_letter.html", templateArguments);
    }


    private void assertRenderedGOLFileExpectedLines(String templateName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        ServiceResult<String> renderResult = renderer.renderTemplate("common" + separator + "grantoffer" + separator + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccessObject();

        List<String> expectedMainLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "common" + separator + "grantoffer" + separator + templateName).toURI()).toPath());

        simpleFilterNot(expectedMainLines, StringUtils::isEmpty).forEach(expectedLine -> {
            if (!expectedLine.contains("import")
                    && !expectedLine.contains("css")
                    && !expectedLine.contains("pages")) {
                assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                        processedTemplate.contains(expectedLine));
            }
        });

    }

    private YearlyGOLProfileTable getYearlyGOLProfileTable() {

        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap();
        Map<String, List<String>> organisationYearsMap = new LinkedHashMap();
        List<String> years = new LinkedList<>();
        Map<String, List<BigDecimal>> organisationEligibleCostTotal = new HashMap();
        List<BigDecimal> eligibleCostPerYear = new LinkedList<>();
        Map<String, List<BigDecimal>> organisationGrantAllocationTotal = new HashMap();
        List<BigDecimal> grantPerYear = new LinkedList<>();
        Map<String, BigDecimal> yearEligibleCostTotal = new HashMap();
        Map<String, BigDecimal> yearGrantAllocationTotal = new HashMap();

        organisationAndGrantPercentageMap.put("Empire Ltd", 20);
        organisationAndGrantPercentageMap.put("Ludlow", 30);
        organisationAndGrantPercentageMap.put("EGGS", 50);


        years.add(0, "2016/2017");
        years.add(1, "2017/2018");
        years.add(2, "2018/2019");
        organisationYearsMap.put("Empire Ltd", years);
        organisationYearsMap.put("Ludlow", years);
        organisationYearsMap.put("EGGS", years);

        eligibleCostPerYear.add(0, BigDecimal.valueOf(1000));
        eligibleCostPerYear.add(1, BigDecimal.valueOf(1200));
        eligibleCostPerYear.add(2, BigDecimal.valueOf(1100));
        grantPerYear.add(0, BigDecimal.valueOf(1000));
        grantPerYear.add(1, BigDecimal.valueOf(1200));
        grantPerYear.add(2, BigDecimal.valueOf(1100));
        organisationEligibleCostTotal.put("Empire Ltd", eligibleCostPerYear);
        organisationEligibleCostTotal.put("Ludlow", eligibleCostPerYear);
        organisationEligibleCostTotal.put("EGGS", eligibleCostPerYear);

        organisationGrantAllocationTotal.put("Empire Ltd", grantPerYear);
        organisationGrantAllocationTotal.put("Ludlow", grantPerYear);
        organisationGrantAllocationTotal.put("EGGS", grantPerYear);

        yearEligibleCostTotal.put(years.get(0), BigDecimal.valueOf(3000));
        yearEligibleCostTotal.put(years.get(1), BigDecimal.valueOf(2600));
        yearEligibleCostTotal.put(years.get(2), BigDecimal.valueOf(2300));

        yearGrantAllocationTotal.put(years.get(0), BigDecimal.valueOf(3000));
        yearGrantAllocationTotal.put(years.get(1), BigDecimal.valueOf(2600));
        yearGrantAllocationTotal.put(years.get(2), BigDecimal.valueOf(2300));

        return new YearlyGOLProfileTable(organisationAndGrantPercentageMap, organisationYearsMap,
                organisationEligibleCostTotal, organisationGrantAllocationTotal,
                yearEligibleCostTotal, yearGrantAllocationTotal);
    }


}
