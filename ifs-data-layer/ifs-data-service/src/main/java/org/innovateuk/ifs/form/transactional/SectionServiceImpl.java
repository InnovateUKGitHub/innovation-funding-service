package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class SectionServiceImpl extends BaseTransactionalService implements SectionService {

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private QuestionService questionService;

    @Override
    public ServiceResult<SectionResource> getById(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource);
    }

    @Override
    public ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId) {
        return find(section(sectionId)).andOnSuccessReturn(section ->
                new HashSet<>(simpleMap(section.fetchAllQuestionsAndChildQuestions(), Question::getId)));
    }

    @Override
    public ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(final Long competitionId, final SectionType type) {
        return getCompetition(competitionId).andOnSuccessReturn(comp -> sectionsOfType(comp, type));
    }

    private List<SectionResource> sectionsOfType(Competition competition, SectionType type) {
        return competition.getSections().stream()
                .filter(s -> s.isType(type))
                .map(sectionMapper::mapToResource)
                .collect(toList());
    }

    @Override
    public ServiceResult<SectionResource> getNextSection(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource).andOnSuccess(this::getNextSection);
    }

    @Override
    public ServiceResult<SectionResource> getNextSection(SectionResource section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getNextSiblingSection(section);
        } else {
            Section nextSection = sectionRepository.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(section.getCompetition(), section.getPriority());
            return find(nextSection, notFoundError(Section.class, section.getCompetition(), section.getPriority())).andOnSuccessReturn(sectionMapper::mapToResource);
        }
    }

    private ServiceResult<SectionResource> getNextSiblingSection(SectionResource section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                section.getCompetition(), section.getParentSection(), section.getPriority());

        if (sibling == null) {
            return getNextSection(section.getParentSection());
        } else {
            return serviceSuccess(sectionMapper.mapToResource(sibling));
        }
    }

    @Override
    public ServiceResult<SectionResource> getPreviousSection(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource).andOnSuccess(this::getPreviousSection);
    }

    @Override
    public ServiceResult<SectionResource> getPreviousSection(SectionResource section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getPreviousSiblingSection(section);
        } else {
            Section firstSection = sectionRepository.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(section.getCompetition(), section.getPriority());

            return find(firstSection, notFoundError(Section.class, section.getCompetition(), section.getPriority())).
                    andOnSuccessReturn(sectionMapper::mapToResource);
        }
    }

    private ServiceResult<SectionResource> getPreviousSiblingSection(SectionResource section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                section.getCompetition(), section.getParentSection(), section.getPriority());

        if (sibling == null) {
            return getPreviousSection(section.getParentSection());
        } else {
            return serviceSuccess(sectionMapper.mapToResource(sibling));
        }
    }

    @Override
    public ServiceResult<SectionResource> getSectionByQuestionId(final Long questionId) {
        return find(sectionRepository.findByQuestionsId(questionId), notFoundError(Section.class, questionId)).
                andOnSuccessReturn(sectionMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<SectionResource>> getByCompetitionId(final Long competitionId) {
        return find(sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competitionId), notFoundError(Section.class, competitionId)).
                andOnSuccessReturn(r -> simpleMap(r, sectionMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId) {
        return serviceSuccess(simpleMap(sectionRepository.findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(competitionId), sectionMapper::mapToResource));
    }

}
