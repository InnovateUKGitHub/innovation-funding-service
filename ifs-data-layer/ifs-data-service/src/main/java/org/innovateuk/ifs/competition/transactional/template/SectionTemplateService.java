package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SectionTemplateService implements BaseTemplateService<List<Section>, Competition> {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionTemplateService questionTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ServiceResult<List<Section>> createByTemplate(List<Section> section) {
        return null;
    }

    @Transactional
    public List<Section> createByRequisite(Competition competition) {
        if (competition.getSections() == null) {
            return null;
        }

        List<Section> sectionsWithoutParentSections = competition.getSections().stream()
                .filter(s -> s.getParentSection() == null)
                .collect(Collectors.toList());

        new ArrayList<>(sectionsWithoutParentSections).forEach(section -> createChildSectionsRecursively(competition, section));

        return sectionsWithoutParentSections;
    }

    private List<Section> createChildSectionsRecursively(Competition competition, Section parentSection) {
        return parentSection.getChildSections().stream().map(section -> createChildSectionRecursively(competition, section).apply(section)).collect(Collectors.toList());
    }

    private Function<Section, Section> createChildSectionRecursively(Competition competition, Section parentSection) {
        return (Section section) -> {
            entityManager.detach(section);
            section.setCompetition(competition);
            section.setId(null);
            sectionRepository.save(section);
            if (!competition.getSections().contains(section)) {
                competition.getSections().add(section);
            }

            section.setQuestions(questionTemplateService.createByRequisite(section));

            createChildSectionsRecursively(competition, section);

            section.setParentSection(parentSection);
            if (!parentSection.getChildSections().contains(section) || parentSection.getChildSections() == null || parentSection != null) {
                parentSection.getChildSections().add(section);
            }
            return section;
        };
    }

    public void cleanForCompetition(Competition competition) {
        List<Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
        competition.setSections(new ArrayList());
        sectionRepository.delete(sections);
    }
}
