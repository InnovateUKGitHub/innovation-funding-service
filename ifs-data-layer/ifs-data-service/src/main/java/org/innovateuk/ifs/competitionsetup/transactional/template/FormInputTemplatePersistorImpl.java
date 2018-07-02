package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.template.BaseChainedTemplatePersistor;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.GuidanceRow;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Transactional component providing functions for persisting copies of FormInputs by their parent Question entity object.
 */
@Component
public class FormInputTemplatePersistorImpl implements BaseChainedTemplatePersistor<List<FormInput>, Question> {
    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowTemplatePersistorImpl guidanceRowTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FormInput> persistByParentEntity(Question question) {
        return simpleMap(question.getFormInputs(), createFunction(question));
    }

    @Override
    public void cleanForParentEntity(Question question) {
        List<FormInput> formInputs = question.getFormInputs();

        formInputs.forEach(formInput -> guidanceRowTemplateService.cleanForParentEntity(formInput));
        formInputs.forEach(formInput -> entityManager.detach(formInput));
        formInputRepository.deleteAll(formInputs);
    }

    private Function<FormInput, FormInput> createFunction(Question question) {
        return (FormInput formInput) -> {
            // Extract the validators into a new Set as the hibernate Set contains persistence information which alters
            // the original FormValidator
            Set<FormValidator> formValidatorsCopy = new HashSet<>(formInput.getFormValidators());
            List<GuidanceRow> guidanceRowsCopy = new ArrayList<>();
            if (formInput.getGuidanceRows() != null) {
                guidanceRowsCopy.addAll(formInput.getGuidanceRows());
            }

            final Set<FileTypeCategory> allowedFileTypesCopy;
            if (formInput.getAllowedFileTypes() != null) {
                allowedFileTypesCopy = new HashSet<>(formInput.getAllowedFileTypes());
            }
            else {
                allowedFileTypesCopy = null;
            }

            entityManager.detach(formInput);
            formInput.setAllowedFileTypes(allowedFileTypesCopy);

            formInput.setCompetition(question.getCompetition());
            formInput.setQuestion(question);
            formInput.setId(null);
            formInput.setFormValidators(formValidatorsCopy);
            formInput.setGuidanceRows(new ArrayList<>());
            formInput.setActive(!isSectorCompetitionWithScopeQuestion(question.getCompetition(), question, formInput)
                    && formInput.getActive());
            formInputRepository.save(formInput);

            formInput.setGuidanceRows(guidanceRowsCopy);
            guidanceRowTemplateService.persistByParentEntity(formInput);

            return formInput;
        };
    }

    private boolean isSectorCompetitionWithScopeQuestion(Competition competition, Question question, FormInput formInput) {
        return competition.getCompetitionType().isSector() &&
                question.isScope() &&
                formInput.getType() == ASSESSOR_APPLICATION_IN_SCOPE;
    }
}
