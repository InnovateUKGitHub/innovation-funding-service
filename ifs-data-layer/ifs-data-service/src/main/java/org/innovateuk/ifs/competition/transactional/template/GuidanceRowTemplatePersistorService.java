package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuidanceRowTemplatePersistorService implements BaseChainedTemplatePersistorService<List<GuidanceRow>, FormInput> {
    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<GuidanceRow> persistByPrecedingEntity(FormInput formInput) {
        return formInput.getGuidanceRows() == null ? Collections.emptyList() : formInput.getGuidanceRows().stream().map(createFormInputGuidanceRow(formInput)).collect(Collectors.toList());
    }

    private Function<GuidanceRow, GuidanceRow> createFormInputGuidanceRow(FormInput formInput) {
        return (GuidanceRow row) -> {
            entityManager.detach(row);
            row.setFormInput(formInput);
            row.setId(null);
            guidanceRowRepository.save(row);
            return row;
        };
    }

    public void cleanForPrecedingEntity(FormInput formInput) {
        List<GuidanceRow> scoreRows = formInput.getGuidanceRows();
        if(scoreRows.size() > 0) {
            guidanceRowRepository.delete(scoreRows);
        }
    }
}
