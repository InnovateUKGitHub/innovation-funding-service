package org.innovateuk.ifs.file.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

import static java.io.File.separator;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertTrue;

/**
 *
 **/
public class FreemarkerGOLTemplateRendererIntegrationTest extends BaseIntegrationTest {

    private static final String DATE_PREFIX = "Date: ";
    private static final String DATE_TODAY = DATE_PREFIX +  DateFormatUtils.format(new Date(), "d MMMM yyyy");
    private static final String DUMMY_PROJECT_START_DATE = "26 December 2016";
    private static final String GOL_HTML_TEMPLATE_NAME = "grant_offer_letter.html";
    private static final String EXPECTED_GOL_HTML_NAME = "dummy_full_grant_offer_letter.html";
    private static final String PATH_TO_GOL_HTML_TEMPLATE = "common" + separator + "grantoffer" + separator;
    private static final String PATH_TO_EXPECTED_GOL_HTML = "expectedtemplates" + separator + "common" + separator + "grantoffer" + separator;

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
                "CompetitionName", "Steps to the future",
                "ProjectTitle", "<Time Machine> & \"Teleportation\"", // test escape html
                "ProjectStartDate", DUMMY_PROJECT_START_DATE,
                "ProjectLength", "3",
                "ApplicationNumber", "12334"
        );
        assertRenderedGOLFileExpectedLines(GOL_HTML_TEMPLATE_NAME, templateArguments);
    }

    private void assertRenderedGOLFileExpectedLines(String templateName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        ServiceResult<String> renderResult = renderer.renderTemplate(PATH_TO_GOL_HTML_TEMPLATE + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccessObject();

        List<String> expectedMainLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource(PATH_TO_EXPECTED_GOL_HTML + EXPECTED_GOL_HTML_NAME).toURI()).toPath());
        expectedMainLines.replaceAll(line -> line.contains(DATE_PREFIX) ? DATE_TODAY : line);

        simpleFilterNot(expectedMainLines, StringUtils::isEmpty).forEach(expectedLine -> {
            if (!processedTemplate.contains(expectedLine.trim())) {
                assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                        processedTemplate.contains(expectedLine));
            }
        });

    }
}
