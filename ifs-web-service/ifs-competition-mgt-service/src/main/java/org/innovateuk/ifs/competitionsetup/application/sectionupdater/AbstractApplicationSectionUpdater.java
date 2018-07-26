package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.apache.el.parser.ParseException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.competitionsetup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToLinkedHashSet;


public abstract class AbstractApplicationSectionUpdater extends AbstractSectionUpdater {

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return APPLICATION_FORM;
    }


	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        AbstractQuestionForm form = (AbstractQuestionForm) competitionSetupForm;
        mapGuidanceRows(form);
        return competitionSetupQuestionService.updateQuestion(form.getQuestion());
	}

	protected abstract void mapGuidanceRows(AbstractQuestionForm form);

    @Override
    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource,
                                                              String fieldName,
                                                              String value,
                                                              Optional<Long> questionId) {
        if("removeGuidanceRow".equals(fieldName)) {
            return removeGuidanceRow(questionId, value);
        } else if (fieldName.contains("guidanceRow")) {
            return tryUpdateGuidanceRow(questionId, fieldName, value);
        } else if (fieldName.contains("allowedFileTypes")) {
            return updateAllowedFileTypes(questionId, value);
        } else {
            return super.handleIrregularAutosaveCase(competitionResource, fieldName, value, questionId);
        }
    }

    private ServiceResult<Void> updateAllowedFileTypes(Optional<Long> questionId, String value) {
        return competitionSetupQuestionService.getQuestion(questionId.get()).andOnSuccess(question -> {
            String[] strings = StringUtils.commaDelimitedListToStringArray(value);
            Set<FileTypeCategory> fileTypeCategories = simpleToLinkedHashSet(strings, FileTypeCategory::valueOf);

            question.setAllowedFileTypes(fileTypeCategories);

            return competitionSetupQuestionService.updateQuestion(question);
        });
    }

    private ServiceResult<Void> removeGuidanceRow(Optional<Long> questionId, String value) {
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

    private ServiceResult<Void> tryUpdateGuidanceRow(Optional<Long> questionId, String fieldName, String value) {
        return competitionSetupQuestionService.getQuestion(questionId.get()).andOnSuccess(question -> {
            Integer index = getGuidanceRowsIndex(fieldName);
            GuidanceRowResource guidanceRow;
            if (index >= question.getGuidanceRows().size()) {
                addNotSavedGuidanceRows(question, index);
            }

            guidanceRow = question.getGuidanceRows().get(index);
            question.getGuidanceRows().set(index, guidanceRow);

            if (fieldName.endsWith("justification")) {
                guidanceRow.setJustification(value);
            } else {
                return autoSaveGuidanceRowSubject(guidanceRow, fieldName, value).andOnSuccess(() -> serviceSuccess(question));
            }

            return serviceSuccess(question);
        }).andOnSuccess(question -> competitionSetupQuestionService.updateQuestion(question));
    }

    protected abstract ServiceResult<Void> autoSaveGuidanceRowSubject(GuidanceRowResource guidanceRow, String fieldName, String value);

    private Integer getGuidanceRowsIndex(String fieldName) throws ParseException {
        return Integer.parseInt(fieldName.substring(fieldName.indexOf('[') + 1, fieldName.indexOf(']')));
    }

    private void addNotSavedGuidanceRows(CompetitionSetupQuestionResource question, Integer index) {
        Integer currentIndexNotUsed = question.getGuidanceRows().size();

        for(Integer i = currentIndexNotUsed; i <= index; i++) {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            question.getGuidanceRows().add(i, guidanceRowResource);
        }
    }
}
