package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Function;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class GuidanceRowTemplateService  {
    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<GuidanceRow> createFormInputGuidanceRows(FormInput formInput, List<GuidanceRow> guidanceRows) {
        return simpleMap(guidanceRows, createFormInputGuidanceRow(formInput));
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

    public void cleanForCompetition(Competition competition) {
        List<GuidanceRow> scoreRows = guidanceRowRepository.findByFormInputQuestionCompetitionId(competition.getId());
        guidanceRowRepository.delete(scoreRows);
    }
}
