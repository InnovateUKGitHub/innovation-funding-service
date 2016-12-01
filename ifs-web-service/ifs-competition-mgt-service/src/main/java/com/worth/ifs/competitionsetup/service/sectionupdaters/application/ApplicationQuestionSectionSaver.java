package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.textToBoolean;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ApplicationQuestionSectionSaver implements CompetitionSetupSubsectionSaver {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToSave() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        ApplicationQuestionForm applicationQuestionForm = (ApplicationQuestionForm) competitionSetupForm;

        CompetitionSetupQuestionResource question = competitionSetupQuestionService.getQuestion(
                applicationQuestionForm.getQuestion().getQuestionId()).getSuccessObjectOrThrowException();

        if(question == null) {
            return makeErrorList();
        }

        question.setGuidanceRows(new ArrayList());

        applicationQuestionForm.getGuidanceRows().forEach(guidanceRow -> {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            guidanceRowResource.setJustification(guidanceRow.getJustification());
            guidanceRowResource.setSubject(guidanceRow.getScoreFrom() + "," + guidanceRow.getScoreTo());
            question.getGuidanceRows().add(guidanceRowResource);
        });

        try {
            competitionSetupQuestionService.updateQuestion(question);
        } catch (RuntimeException e) {
            return makeErrorList();
        }

        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName,
                                            String value, Optional<Long> objectId) {
	    if(objectId.isPresent()) {
            CompetitionSetupQuestionResource question = competitionSetupQuestionService.getQuestion(objectId.get()).getSuccessObjectOrThrowException();

            if(question == null) {
                return makeErrorList();
            }

            List<Error> errors = updateQuestionWithValueByFieldname(question, fieldName, value);
            if(!errors.isEmpty()) {
                return errors;
            }

            competitionSetupQuestionService.updateQuestion(question);
        } else {
            return makeErrorList();
        }

        return Collections.emptyList();
	}

	private List<Error> updateQuestionWithValueByFieldname(CompetitionSetupQuestionResource question, String fieldName, String value) {
        switch (fieldName) {
            case "question.shortTitle" :
                if (!value.isEmpty()) {
                    question.setShortTitle(value);
                } else {
                    return makeErrorList("question.shortTitle", "This field cannot be left blank");
                }
                break;
            case "question.title" :
                question.setTitle(value);
                break;
            case "question.subTitle" :
                question.setSubTitle(value);
                break;
            case "question.guidanceTitle" :
                question.setGuidanceTitle(value);
                break;
            case "question.guidance" :
                question.setGuidance(value);
                break;
            case "question.maxWords" :
                Integer maxWords;
                try {
                    maxWords = Integer.parseInt(value);
                } catch(NumberFormatException e) {
                    return makeErrorList("question.maxWords");
                }
                if (maxWords < 1) {
                    return makeErrorList("question.maxWords", "javax.validation.constraints.Min.message", value);
                }
                question.setMaxWords(maxWords);
                break;
            case "question.appendix" :
                question.setAppendix(textToBoolean(value));
                break;
            case "question.assessmentGuidance" :
                question.setAssessmentGuidance(value);
                break;
            case "question.scored" :
                question.setScored(Boolean.parseBoolean(value));
                break;
            case "question.scoreTotal" :
                question.setScoreTotal(Integer.parseInt(value));
                break;
            case "question.writtenFeedback" :
                question.setWrittenFeedback(Boolean.parseBoolean(value));
                break;
            case "question.assessmentMaxWords" :
                question.setAssessmentMaxWords(Integer.parseInt(value));
                break;
            case "removeGuidanceRow":
                int index = Integer.valueOf(value);
                //If the index is out of range then ignore it, The UI will add rows without them being persisted yet.
                if (question.getGuidanceRows().size() <= index) {
                    break;
                }

                question.getGuidanceRows().remove(index);
                break;
            default:
                 return tryUpdateGuidanceRow(question, fieldName, value);
        }

        return Collections.emptyList();
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

    private List<Error> tryUpdateGuidanceRow(CompetitionSetupQuestionResource question, String fieldName, String value) {
        Integer index;
        GuidanceRowResource guidanceRow;

        if (!fieldName.contains("guidanceRow")) {
            return makeErrorList();
        }

        try {
            index = getGuidanceRowsIndex(fieldName);
            if(index >= question.getGuidanceRows().size()) {
                addNotSavedGuidanceRows(question, index);
            }

            guidanceRow = question.getGuidanceRows().get(index);

            if(fieldName.endsWith("subject")) {

                if (StringUtils.isBlank(value)) {
                    return asList(new Error("validation.applicationquestionform.subject.required", HttpStatus.BAD_REQUEST));
                }
                guidanceRow.setSubject(value);

            } else if(fieldName.endsWith("justification")) {

                if (StringUtils.isBlank(value)) {
                    return asList(new Error("validation.applicationquestionform.justification.required", HttpStatus.BAD_REQUEST));
                }
                guidanceRow.setJustification(value);

            } else if(fieldName.endsWith("scoreFrom")) {

                if (!NumberUtils.isNumber(value) || Integer.parseInt(value) < 0) {
                    return asList(new Error("validation.applicationquestionform.scorefrom.min", HttpStatus.BAD_REQUEST));
                }
                guidanceRow.setSubject(modifySubject(question, guidanceRow.getSubject(), value, null));

            } else if(fieldName.endsWith("scoreTo")) {

                if (!NumberUtils.isNumber(value) || Integer.parseInt(value) < 0) {
                    return asList(new Error("validation.applicationquestionform.scoreto.min", HttpStatus.BAD_REQUEST));
                }
                guidanceRow.setSubject(modifySubject(question, guidanceRow.getSubject(), null, value));

            } else {

                return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
            }
        } catch (ParseException e) {
            return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
        }

        question.getGuidanceRows().set(index, guidanceRow);

        return Collections.emptyList();
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
		return ApplicationFormForm.class.equals(clazz);
	}
}
