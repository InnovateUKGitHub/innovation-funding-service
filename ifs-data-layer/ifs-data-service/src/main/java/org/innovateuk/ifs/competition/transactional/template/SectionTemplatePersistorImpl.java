package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transactional component providing functions for persisting copies of Sections by their parent Competition entity object.
 */
@Component
public class SectionTemplatePersistorImpl implements BaseChainedTemplatePersistor<List<Section>, Competition> {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionTemplatePersistorImpl questionTemplatePersistorServiceServiceImpl;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Section> persistByParentEntity(Competition template) {
        if (template.getSections() == null) {
            return null;
        }

        List<Section> sectionsWithoutParentSections = getTopLevelSections(template);

        new ArrayList<>(sectionsWithoutParentSections).forEach(section -> createChildSectionRecursively(template, null).apply(section));

        return sectionsWithoutParentSections;
    }

    @Override
    public void cleanForParentEntity(Competition competition) {
        getTopLevelSections(competition).stream().forEach(section -> cleanForPrecedingEntityRecursively(section));
    }

    private List<Section> createChildSectionsRecursively(Competition competition, Section parentSectionTemplate) {
        if(parentSectionTemplate.getChildSections() != null) {
            return parentSectionTemplate.getChildSections().stream().map(section -> createChildSectionRecursively(competition, parentSectionTemplate).apply(section)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Function<Section, Section> createChildSectionRecursively(Competition competition, Section parentSection) {
        return (Section section) -> {
            entityManager.detach(section);

            section.setId(null);
            section.setCompetition(competition);
            section.setParentSection(parentSection);

            sectionRepository.save(section);

            questionTemplatePersistorServiceServiceImpl.persistByParentEntity(section);

            createChildSectionsRecursively(competition, section);

            return section;
        };
    }

    private void cleanForPrecedingEntityRecursively(Section section) {
        if(section.getChildSections() != null) {
            section.getChildSections().stream().forEach(s -> cleanForPrecedingEntityRecursively(s));
        }

        questionTemplatePersistorServiceServiceImpl.cleanForParentEntity(section);

        entityManager.detach(section);
        sectionRepository.delete(section);
    }

    private List<Section> getTopLevelSections(Competition competition) {
        return competition.getSections().stream()
                .filter(s -> s.getParentSection() == null)
                .collect(Collectors.toList());
    }
}
