package org.innovateuk.ifs.application.forms;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Util class for listing all constants in the applicationForm
 */
public final class ApplicationFormUtil {
    public static final String QUESTION_URL = "/question/";
    public static final String QUESTION_ID = "questionId";
    public static final String MODEL_ATTRIBUTE_MODEL = "model";
    public static final String MODEL_ATTRIBUTE_FORM = "form";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_FORM = "application-form";
    public static final String APPLICATION_FORM_LEAD = "application-lead-form";
    public static final String SECTION_URL = "/section/";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_SECTION_AS_COMPLETE = "mark_section_as_complete";
    public static final String MARK_SECTION_AS_INCOMPLETE = "mark_section_as_incomplete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String REQUESTING_FUNDING = "requesting_funding";
    public static final String NOT_REQUESTING_FUNDING = "not_requesting_funding";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String ORGANISATION_SIZE_KEY = "organisationSize";
    public static final String APPLICATION_BASE_URL = "/application/";
    public static final String APPLICATION_START_DATE = "application.startDate";
    public static final String SAVE_AND_RETURN_KEY = "save-and-return";

    private ApplicationFormUtil() {}

    public static boolean isMarkQuestionRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_COMPLETE) || params.containsKey(MARK_AS_INCOMPLETE);
    }

    public static boolean isMarkQuestionAsCompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_COMPLETE);
    }

    public static boolean isMarkQuestionAsIncompleteRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(MARK_AS_INCOMPLETE);
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

    public static boolean isSaveAndReturnRequest(@NotNull Map<String, String[]> params) {
        return params.containsKey(SAVE_AND_RETURN_KEY);
    }

}
