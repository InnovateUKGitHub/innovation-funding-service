package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.innovateuk.ifs.competition.transactional.template.BaseChainedTemplatePersistor;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.repository.MultipleChoiceOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transactional component providing functions for persisting copies of GuidanceRows by their parent FormInput entity object.
 */
@Component
public class MultipleChoiceOptionTemplatePersistorImpl implements BaseChainedTemplatePersistor<List<MultipleChoiceOption>, FormInput> {
    @Autowired
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<MultipleChoiceOption> persistByParentEntity(FormInput formInput) {
        return formInput.getMultipleChoiceOptions() == null || formInput.getMultipleChoiceOptions().isEmpty() ? Collections.emptyList() : formInput.getMultipleChoiceOptions().stream().map(createMultipleChoiceOption(formInput)).collect(Collectors.toList());
    }

    private Function<MultipleChoiceOption, MultipleChoiceOption> createMultipleChoiceOption(FormInput formInput) {
        return (MultipleChoiceOption row) -> {
            entityManager.detach(row);
            row.setFormInput(formInput);
            row.setId(null);
            multipleChoiceOptionRepository.save(row);
            return row;
        };
    }

    @Transactional
    public void cleanForParentEntity(FormInput formInput) {
        List<MultipleChoiceOption> choiceRows = formInput.getMultipleChoiceOptions();
        if(!choiceRows.isEmpty()) {
            choiceRows.forEach(row -> entityManager.detach(row));
            multipleChoiceOptionRepository.deleteAll(choiceRows);
        }
    }
}
