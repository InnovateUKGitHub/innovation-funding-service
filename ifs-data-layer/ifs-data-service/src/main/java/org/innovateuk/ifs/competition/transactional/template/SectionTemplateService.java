package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class SectionTemplateService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionTemplateService questionTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void attachSectionRecursively(Competition competition, List<Section> sectionTemplates, Section parentSection) {
        if (sectionTemplates == null) {
            return;
        }
        new ArrayList<>(sectionTemplates).forEach(attachSectionRecursively(competition, parentSection));
    }

    private Consumer<Section> attachSectionRecursively(Competition competition, Section parentSection) {
        return (Section section) -> {
            entityManager.detach(section);
            section.setCompetition(competition);
            section.setId(null);
            sectionRepository.save(section);
            if (!competition.getSections().contains(section)) {
                competition.getSections().add(section);
            }

            section.setQuestions(questionTemplateService.createQuestions(competition, section, section.getQuestions()));

            attachSectionRecursively(competition, section.getChildSections(), section);

            section.setParentSection(parentSection);
            if (!parentSection.getChildSections().contains(section) || parentSection.getChildSections() == null || parentSection != null) {
                parentSection.getChildSections().add(section);
            }
        };
    }

    public void cleanForCompetition(Competition competition) {
        List<Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
        competition.setSections(new ArrayList());
        sectionRepository.delete(sections);
    }
}
