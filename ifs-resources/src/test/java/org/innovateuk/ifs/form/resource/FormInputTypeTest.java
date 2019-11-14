package org.innovateuk.ifs.form.resource;

import org.junit.Test;

import java.util.EnumSet;

import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.junit.Assert.*;

public class FormInputTypeTest {

    @Test
    public void isDisplayablePrintType() {
        EnumSet<FormInputType> expectedDisplayablePrintTypes = EnumSet.of(TEXTAREA, APPLICATION_DETAILS);
        EnumSet<FormInputType> expectedNotDisplayablePrintTypes = EnumSet.of(FILEUPLOAD, FINANCIAL_SUMMARY,
                ASSESSOR_RESEARCH_CATEGORY, ASSESSOR_APPLICATION_IN_SCOPE, ASSESSOR_SCORE, ORGANISATION_TURNOVER,
                STAFF_COUNT, FINANCIAL_YEAR_END, FINANCIAL_OVERVIEW_ROW, FINANCIAL_STAFF_COUNT, TEMPLATE_DOCUMENT);

        assertTrue(intersection(expectedDisplayablePrintTypes, expectedNotDisplayablePrintTypes).isEmpty());

        for (FormInputType formInputType : FormInputType.values()) {
            if (expectedDisplayablePrintTypes.contains(formInputType)) {
                assertTrue("Expected Displayable Print Type: " + formInputType, formInputType.isDisplayablePrintType());
            }
            else if (expectedNotDisplayablePrintTypes.contains(formInputType)) {
                assertFalse("Expected Not Displayable Print Type: " + formInputType, formInputType.isDisplayablePrintType());
            }
            else {
                fail("untested FormInputType: " + formInputType + " (add it to one of the expected sets)");
            }
        }
    }

    @Test
    public void isDisplayableQuestionType() {
        EnumSet<FormInputType> expectedDisplayableQuestionTypes = EnumSet.of(TEXTAREA, APPLICATION_DETAILS, FILEUPLOAD,
                FINANCIAL_SUMMARY, ASSESSOR_RESEARCH_CATEGORY,  ASSESSOR_APPLICATION_IN_SCOPE, ASSESSOR_SCORE,
                ORGANISATION_TURNOVER, STAFF_COUNT, FINANCIAL_YEAR_END, FINANCIAL_OVERVIEW_ROW, FINANCIAL_STAFF_COUNT);
        EnumSet<FormInputType> expectedNotDisplayableQuestionTypes = EnumSet.of(TEMPLATE_DOCUMENT);

        assertTrue(intersection(expectedDisplayableQuestionTypes, expectedNotDisplayableQuestionTypes).isEmpty());

        for (FormInputType formInputType : FormInputType.values()) {
            if (expectedDisplayableQuestionTypes.contains(formInputType)) {
                assertTrue("Expected Displayable Question Type: " + formInputType, formInputType.isDisplayableQuestionType());
            }
            else if (expectedNotDisplayableQuestionTypes.contains(formInputType)) {
                assertFalse("Expected Not Displayable Question Type: " + formInputType, formInputType.isDisplayableQuestionType());
            }
            else {
                fail("untested FormInputType: " + formInputType + " (add it to one of the expected sets)");
            }
        }
    }

    private static <T extends Enum<T>> EnumSet<T> intersection(EnumSet<T> set1, EnumSet<T> set2) {
        EnumSet<T> intersection = EnumSet.copyOf(set1);
        intersection.retainAll(set2);
        return intersection;
    }
}