package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.testdata.builders.data.PreRegistrationSectionLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data for Pre Registration Sections and attaches it to a competition
 */
public class PreRegistrationSectionDataBuilder extends BaseDataBuilder<Void, PreRegistrationSectionDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(PreRegistrationSectionDataBuilder.class);

    public PreRegistrationSectionDataBuilder withPreRegistrationSections(PreRegistrationSectionLine sectionLine) {
        return with(data -> {
            testService.doWithinTransaction(() -> {
                Competition competition = retrieveCompetitionByName(sectionLine.getCompetitionName());
                List<SectionResource> competitionSections = sectionService.getByCompetitionId(competition.getId()).getSuccess();

                competitionSections.stream()
                        .forEach(sectionResource -> {
                            if (sectionResource.getName().equals(sectionLine.getSectionName())) {
                                if (sectionLine.getSubSectionName() == null && sectionLine.getQuestionName() == null) {
                                    markSectionForPreRegistration(sectionResource, sectionLine.getSubSectionName(), sectionLine.getQuestionName());
                                } else if (sectionLine.getQuestionName() == null) {
                                    markSubsectionForPreRegistration(sectionResource, sectionLine.getSubSectionName(), sectionLine.getQuestionName());
                                } else {
                                    markQuestionForPreRegistration(sectionResource, sectionLine.getQuestionName());
                                }
                            }
                        });
            });
        });
    }

    private void markSectionForPreRegistration(SectionResource section, String subSectionName, String questionName) {
        section.setEnabledForPreRegistration(false);
        sectionService.save(section);

        markQuestionForPreRegistration(section, questionName);

        markSubsectionForPreRegistration(section, subSectionName, questionName);
    }

    private void markSubsectionForPreRegistration(SectionResource section, String subSectionName, String questionName) {
        sectionService.getChildSectionsByParentId(section.getId()).getSuccess().stream()
                .filter(subSectionResource -> subSectionName == null ? true : subSectionResource.getName().equals(subSectionName))
                .forEach(sectionResource -> markSectionForPreRegistration(sectionResource, subSectionName, questionName));
    }

    private void markQuestionForPreRegistration(SectionResource section, String questionName) {
        section.getQuestions().stream()
                .map(questionId -> questionService.getQuestionById(questionId).getSuccess())
                .filter(questionResource -> questionName == null ? true : questionResource.getName().equals(questionName))
                .forEach(questionResource -> {
                    questionResource.setEnabledForPreRegistration(false);
                    questionService.save(questionResource);
                });
    }

    public static PreRegistrationSectionDataBuilder newCompetitionPreRegistrationSections(ServiceLocator serviceLocator) {
        return new PreRegistrationSectionDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private PreRegistrationSectionDataBuilder(List<BiConsumer<Integer, Void>> multiActions, ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected PreRegistrationSectionDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PreRegistrationSectionDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Enabled Competition Pre Registration Sections");
    }
}
