package org.innovateuk.ifs.application.forms;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Util class for listing all constants in the applicationForm
 */
public class ApplicationFormUtil {
    public static final String QUESTION_URL = "/question/";
    public static final String QUESTION_ID = "questionId";
    public static final String MODEL_ATTRIBUTE_MODEL = "model";
    public static final String MODEL_ATTRIBUTE_FORM = "form";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_FORM = "application-form";
    public static final String SECTION_URL = "/section/";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_SECTION_AS_COMPLETE = "mark_section_as_complete";
    public static final String ADD_COST = "add_cost";
    public static final String REMOVE_COST = "remove_cost";
    public static final String MARK_SECTION_AS_INCOMPLETE = "mark_section_as_incomplete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String NOT_REQUESTING_FUNDING = "not_requesting_funding";
    public static final String ACADEMIC_FINANCE_REMOVE = "remove_finance_document";
    public static final String REQUESTING_FUNDING = "requesting_funding";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String TERMS_AGREED_KEY = "termsAgreed";
    public static final String STATE_AID_AGREED_KEY = "stateAidAgreed";
    public static final String ORGANISATION_SIZE_KEY = "organisationSize";
    public static final String APPLICATION_BASE_URL = "/application/";
    public static final String APPLICATION_START_DATE = "application.startDate";


    public static boolean isMarkQuestionRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_COMPLETE) || params.containsKey(MARK_AS_INCOMPLETE);
    }

    public static boolean isMarkQuestionAsCompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_COMPLETE);
    }

    public static boolean isMarkQuestionAsIncompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_INCOMPLETE);
    }

    public static boolean isNotRequestingFundingRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(NOT_REQUESTING_FUNDING);
    }

    public static boolean isRequestingFundingRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(REQUESTING_FUNDING);
    }

    public static boolean isFundingRequest(@NotNull Map<String, String[]> params) {
        return isNotRequestingFundingRequest(params) || isRequestingFundingRequest(params);
    }

    public static boolean isMarkSectionRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_COMPLETE) || params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    public static boolean isMarkSectionAsIncompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_INCOMPLETE);
    }

    public static boolean isMarkSectionAsCompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_SECTION_AS_COMPLETE);
    }
}
