package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.innovateuk.ifs.assessment.resource.AssessmentEvent.FEEDBACK;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class FormInputTemplatePersistorService implements BaseChainedTemplatePersistorService<List<FormInput>, Question> {
    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowTemplatePersistorService guidanceRowTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<FormInput> persistByPrecedingEntity(Question question) {
        return simpleMap(question.getFormInputs(), createFunction(question));
    }

    @Transactional
    public void cleanForPrecedingEntity(Question question) {
        List<FormInput> formInputs = question.getFormInputs();


        formInputs.stream().forEach(formInput -> guidanceRowTemplateService.cleanForPrecedingEntity(formInput));
        formInputs.stream().forEach(formInput -> entityManager.detach(formInput));
        formInputRepository.delete(formInputs);
    }

    public Function<FormInput, FormInput> createFunction(Question question) {
        return (FormInput formInput) -> {
            // Extract the validators into a new Set as the hibernate Set contains persistence information which alters
            // the original FormValidator
            Set<FormValidator> copy = new HashSet<>(formInput.getFormValidators());

            entityManager.detach(formInput);
            formInput.setCompetition(question.getCompetition());
            formInput.setQuestion(question);
            formInput.setId(null);
            formInput.setFormValidators(copy);
            formInput.setActive(isSectorCompetitionWithScopeQuestion(question.getCompetition(), question, formInput) ? false : formInput.getActive());
            formInputRepository.save(formInput);
            formInput.setGuidanceRows(guidanceRowTemplateService.persistByPrecedingEntity(formInput));
            return formInput;
        };
    }

    private boolean isSectorCompetitionWithScopeQuestion(Competition competition, Question question, FormInput formInput) {
        if (competition.getCompetitionType().isSector() && question.isScope()) {
            if (formInput.getType() == ASSESSOR_APPLICATION_IN_SCOPE || formInput.getDescription().equals(FEEDBACK)) {
                return true;
            }
        }
        return false;
    }
}
