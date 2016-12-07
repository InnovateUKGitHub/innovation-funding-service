package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.sectionupdaters.AbstractSectionSaver;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ApplicationQuestionSectionSaver extends AbstractSectionSaver implements CompetitionSetupSubsectionSaver {

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToSave() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public ServiceResult<Void> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		ApplicationQuestionForm form = (ApplicationQuestionForm) competitionSetupForm;
        form.getQuestion().setGuidanceRows(new ArrayList());

        form.getGuidanceRows().forEach(guidanceRow -> {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            guidanceRowResource.setJustification(guidanceRow.getJustification());
            guidanceRowResource.setSubject(guidanceRow.getScoreFrom() + "," + guidanceRow.getScoreTo());
            form.getQuestion().getGuidanceRows().add(guidanceRowResource);
        });

        return competitionSetupQuestionService.updateQuestion(form.getQuestion());
	}

    @Override
    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> questionId) {
        if("removeGuidanceRow".equals(fieldName)) {
            return removeGuidanceRow(questionId, fieldName, value);
        } else {
            return tryUpdateGuidanceRow(questionId, fieldName, value);
        }
    }

    private ServiceResult<Void> removeGuidanceRow(Optional<Long> questionId, String fieldName, String value) {
        return competitionSetupQuestionService.getQuestion(questionId.get()).andOnSuccess(question -> {
            int index = Integer.valueOf(value);
            //If the index is out of range then ignore it, The UI will add rows without them being persisted yet.
            if (question.getGuidanceRows().size() <= index) {
                return ServiceResult.serviceSuccess();
            }

            question.getGuidanceRows().remove(index);
            return competitionSetupQuestionService.updateQuestion(question);
        });
    }

    private List<Error> makeErrorList() {
        return makeErrorList("");
    }

    private List<Error> makeErrorList(String fieldName) {
        return makeErrorList(fieldName, "Unable to save question", "");
    }

    private List<Error> makeErrorList(String fieldName, String error) {
        return asList(fieldError(fieldName, "", error));
    }

    private List<Error> makeErrorList(String fieldName, String error, String rejectedValue) {
        return asList(fieldError(fieldName, rejectedValue, error));
    }



    private Integer getGuidanceRowsIndex(String fieldName) throws ParseException {
        return Integer.parseInt(fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]")));
    }

    private ServiceResult<Void> tryUpdateGuidanceRow(Optional<Long> questionId, String fieldName, String value) {

        return competitionSetupQuestionService.getQuestion(questionId.get()).andOnSuccess(question -> {
            Integer index;
            GuidanceRowResource guidanceRow;

            if (!fieldName.contains("guidanceRow")) {
                return serviceFailure(makeErrorList());
            }

            try {
                index = getGuidanceRowsIndex(fieldName);
                if (index >= question.getGuidanceRows().size()) {
                    addNotSavedGuidanceRows(question, index);
                }

                guidanceRow = question.getGuidanceRows().get(index);

                if (fieldName.endsWith("subject")) {

                    if (StringUtils.isBlank(value)) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.subject.required")));
                    } else if (value.length() > 255) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.subject.max")));
                    }
                    guidanceRow.setSubject(value);

                } else if (fieldName.endsWith("justification")) {

                    if (StringUtils.isBlank(value)) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.justification.required")));
                    } else if (value.length() > 255) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.justification.max")));
                    }
                    guidanceRow.setJustification(value);

                } else if (fieldName.endsWith("scoreFrom")) {

                    if (!NumberUtils.isNumber(value) || Integer.parseInt(value) < 0) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.scorefrom.min")));
                    }
                    guidanceRow.setSubject(modifySubject(question, guidanceRow.getSubject(), value, null));

                } else if (fieldName.endsWith("scoreTo")) {

                    if (!NumberUtils.isNumber(value) || Integer.parseInt(value) < 0) {
                        return serviceFailure(asList(fieldError(fieldName, "", "validation.applicationquestionform.scoreto.min")));
                    }
                    guidanceRow.setSubject(modifySubject(question, guidanceRow.getSubject(), null, value));

                } else {

                    return serviceFailure(asList(new Error("Field not found", HttpStatus.BAD_REQUEST)));
                }
            } catch (ParseException e) {
                return serviceFailure(asList(new Error("Field not found", HttpStatus.BAD_REQUEST)));
            }

            question.getGuidanceRows().set(index, guidanceRow);

            return competitionSetupQuestionService.updateQuestion(question);
        });
    }

    private void addNotSavedGuidanceRows(CompetitionSetupQuestionResource question, Integer index) {
        Integer currentIndexNotUsed = question.getGuidanceRows().size();

        for(Integer i = currentIndexNotUsed; i <= index; i++) {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            question.getGuidanceRows().add(i, guidanceRowResource);
        }
    }

    private String modifySubject(CompetitionSetupQuestionResource question, String subject, String value1, String value2) {
        if (question.getType().equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {

            // Initialise subject for newly created guidance rows as will be null
            if (subject == null) {
                subject = "";
            }

            String[] splitSubject = subject.split(",");

            if (value2 == null) {
                return value1 + "," + (splitSubject.length > 1 ? splitSubject[1] : 0);
            } else {
                return (splitSubject.length > 1 ? splitSubject[0] : 0) + "," + value2;
            }
        } else {
            return value1;
        }
    }

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationQuestionForm.class.equals(clazz);
	}
}
