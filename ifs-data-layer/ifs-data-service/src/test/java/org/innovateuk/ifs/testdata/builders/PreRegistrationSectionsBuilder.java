package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.testdata.services.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data for Pre Registration Sections and attaches it to a competition
 */
public class PreRegistrationSectionsBuilder extends BaseDataBuilder<Void, PreRegistrationSectionsBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(PreRegistrationSectionsBuilder.class);

    public PreRegistrationSectionsBuilder withPreRegistrationSections(CsvUtils.CompetitionSectionLineDisabledForPreRegistration sectionLine) {
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

    public static PreRegistrationSectionsBuilder newCompetitionPreRegistrationSections(ServiceLocator serviceLocator) {
        return new PreRegistrationSectionsBuilder(Collections.emptyList(), serviceLocator);
    }

    private PreRegistrationSectionsBuilder(List<BiConsumer<Integer, Void>> multiActions, ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected PreRegistrationSectionsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PreRegistrationSectionsBuilder(actions, serviceLocator);
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
