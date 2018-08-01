package org.innovateuk.ifs.competitionsetup.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.populator.*;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupUpdater;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ELIGIBILITY;

@Service
public class CompetitionSetupServiceImpl implements CompetitionSetupService {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    private Map<CompetitionSetupSection, CompetitionSetupFormPopulator> formPopulators;
    private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionFormPopulator> subsectionFormPopulators;

    private Map<CompetitionSetupSection, CompetitionSetupSectionUpdater> sectionSavers;
    private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionUpdater> subsectionSavers;

    private Map<CompetitionSetupSection, CompetitionSetupSectionModelPopulator> modelPopulators;
    private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionModelPopulator> subsectionModelPopulators;

    @Autowired
    public void setCompetitionSetupFormPopulators(Collection<CompetitionSetupFormPopulator> populators) {
        formPopulators = populators.stream().collect(Collectors.toMap(CompetitionSetupFormPopulator::sectionToFill, Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSubsectionFormPopulators(Collection<CompetitionSetupSubsectionFormPopulator> populators) {
        subsectionFormPopulators = populators.stream().collect(Collectors.toMap(CompetitionSetupSubsectionFormPopulator::sectionToFill, Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSectionSavers(Collection<CompetitionSetupSectionUpdater> savers) {
        sectionSavers = savers.stream().collect(Collectors.toMap(CompetitionSetupUpdater::sectionToSave, Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSubsectionSavers(Collection<CompetitionSetupSubsectionUpdater> savers) {
        subsectionSavers = savers.stream().collect(Collectors.toMap(CompetitionSetupSubsectionUpdater::subsectionToSave, Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSectionModelPopulators(Collection<CompetitionSetupSectionModelPopulator> populators) {
        modelPopulators = populators.stream().collect(Collectors.toMap(CompetitionSetupSectionModelPopulator::sectionToPopulateModel, Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSubsectionModelPopulators(Collection<CompetitionSetupSubsectionModelPopulator> populators) {
        subsectionModelPopulators = populators.stream().collect(Collectors.toMap(CompetitionSetupSubsectionModelPopulator::sectionToPopulateModel, Function.identity()));
    }

    @Override
    public CompetitionSetupViewModel populateCompetitionSectionModelAttributes(
            CompetitionResource competitionResource,
            CompetitionSetupSection section
    ) {
        CompetitionSetupViewModel viewModel = null;
        CompetitionSetupSectionModelPopulator populator = modelPopulators.get(section);

        if (populator != null) {
            viewModel = populator.populateModel(competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, section), competitionResource);
        }

        return viewModel;
    }

    @Override
    public CompetitionSetupSubsectionViewModel populateCompetitionSubsectionModelAttributes(
            CompetitionResource competitionResource,
            CompetitionSetupSection section,
            CompetitionSetupSubsection subsection,
            Optional<Long> objectId
    ) {
        CompetitionSetupSubsectionViewModel viewModel = null;
        checkIfSubsectionIsInSection(section, subsection);
        CompetitionSetupSubsectionModelPopulator populator = subsectionModelPopulators.get(subsection);

        if (populator != null) {
            viewModel = populator.populateModel(competitionResource, objectId);
        }

        return viewModel;
    }

    @Override
    public CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
                                                   CompetitionSetupSection section) {
        CompetitionSetupFormPopulator populator = formPopulators.get(section);
        if (populator == null) {
            LOG.error("unable to populate form for section " + section);
            throw new IllegalArgumentException();
        }
        return populator.populateForm(competitionResource);
    }

    @Override
    public CompetitionSetupForm getSubsectionFormData(CompetitionResource competitionResource,
                                                      CompetitionSetupSection section,
                                                      CompetitionSetupSubsection subsection,
                                                      Optional<Long> objectId) {
        checkIfSubsectionIsInSection(section, subsection);
        CompetitionSetupSubsectionFormPopulator populator = subsectionFormPopulators.get(subsection);
        if (populator == null) {
            LOG.error("unable to populate form for subsection " + subsection);
            throw new IllegalArgumentException();
        }

        return populator.populateForm(competitionResource, objectId);
    }


    @Override
    public ServiceResult<Void> autoSaveCompetitionSetupSection(CompetitionResource competitionResource,
                                                               CompetitionSetupSection section,
                                                               String fieldName,
                                                               String value,
                                                               Optional<Long> objectId) {
        checkCompetitionInitialDetailsComplete(competitionResource, section);
        checkIfInitialDetailsFieldIsRestricted(competitionResource, section, fieldName);

        CompetitionSetupSectionUpdater saver = sectionSavers.get(section);
        CompetitionSetupFormPopulator populator = formPopulators.get(section);
        if (saver == null || populator == null) {
            LOG.error("unable to save section " + section);
            throw new IllegalArgumentException();
        }

        CompetitionSetupForm setupForm = populator.populateForm(competitionResource);
        setupForm.setAutoSaveAction(true);
        return saver.autoSaveSectionField(competitionResource, setupForm, fieldName, value, objectId);
    }

    @Override
    public ServiceResult<Void> autoSaveCompetitionSetupSubsection(CompetitionResource competitionResource,
                                                                  CompetitionSetupSection section,
                                                                  CompetitionSetupSubsection subsection,
                                                                  String fieldName,
                                                                  String value,
                                                                  Optional<Long> objectId) {
        checkCompetitionInitialDetailsComplete(competitionResource, section);
        checkIfSubsectionIsInSection(section, subsection);

        CompetitionSetupSubsectionUpdater saver = subsectionSavers.get(subsection);
        CompetitionSetupSubsectionFormPopulator populator = subsectionFormPopulators.get(subsection);
        if (saver == null || populator == null) {
            LOG.error("unable to save subsection " + subsection);
            throw new IllegalArgumentException();
        }

        return saver.autoSaveSectionField(competitionResource, populator.populateForm(competitionResource, objectId), fieldName, value, objectId);
    }

    @Override
    public ServiceResult<Void> saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
                                                           CompetitionResource competitionResource,
                                                           CompetitionSetupSection section) {
        checkCompetitionInitialDetailsComplete(competitionResource, section);

        CompetitionSetupSectionUpdater saver = sectionSavers.get(section);
        if (saver == null || !saver.supportsForm(competitionSetupForm.getClass())) {
            LOG.error("unable to save section " + section);
            throw new IllegalArgumentException();
        }

        return saver.saveSection(competitionResource, competitionSetupForm).andOnSuccess(() -> {
            if (competitionSetupForm.isMarkAsCompleteAction()) {
                return competitionSetupRestService.markSectionComplete(competitionResource.getId(), section).toServiceResult();
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> saveCompetitionSetupSubsection(CompetitionSetupForm competitionSetupForm,
                                                              CompetitionResource competitionResource,
                                                              CompetitionSetupSection section,
                                                              CompetitionSetupSubsection subsection) {

        checkCompetitionInitialDetailsComplete(competitionResource, section);
        checkIfSubsectionIsInSection(section, subsection);

        CompetitionSetupSubsectionUpdater saver = subsectionSavers.get(subsection);
        if (saver == null || !saver.supportsForm(competitionSetupForm.getClass())) {
            LOG.error("unable to save subsection " + subsection);
            throw new IllegalArgumentException();
        }

        ServiceResult<Void> result = saver.saveSection(competitionResource, competitionSetupForm);
        result.andOnSuccess(() -> {
            markQuestionAsComplete(subsection, competitionSetupForm, competitionResource.getId());
            markSubSectionAsComplete(subsection, competitionResource.getId());
        });

        return result;
    }

    private void markSubSectionAsComplete(CompetitionSetupSubsection subsection, Long competitionId) {
        if (CompetitionSetupSubsection.APPLICATION_DETAILS.equals(subsection) ||
                CompetitionSetupSubsection.FINANCES.equals(subsection)) {
            competitionSetupRestService.markSubSectionComplete(competitionId, CompetitionSetupSection.APPLICATION_FORM, subsection);
        }
    }

    private void markQuestionAsComplete(CompetitionSetupSubsection subsection, CompetitionSetupForm competitionSetupForm, Long competitionId) {
        if (CompetitionSetupSubsection.QUESTIONS.equals(subsection) ||
                CompetitionSetupSubsection.PROJECT_DETAILS.equals(subsection)) {
            AbstractQuestionForm form = (AbstractQuestionForm) competitionSetupForm;
            questionSetupRestService.markQuestionSetupComplete(competitionId, CompetitionSetupSection.APPLICATION_FORM, form.getQuestion().getQuestionId());
        }
    }

    private void checkCompetitionInitialDetailsComplete(CompetitionResource competitionResource, CompetitionSetupSection section) {
        if (!isInitialDetailsCompleteOrTouched(competitionResource.getId()) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            throw new IllegalStateException("'Initial Details' section must be completed first");
        }
    }

    private void checkIfInitialDetailsFieldIsRestricted(CompetitionResource competitionResource, CompetitionSetupSection competitionSetupSection, String fieldName) {
        if (isInitialDetailsCompleteOrTouched(competitionResource.getId()) &&
                competitionSetupSection == CompetitionSetupSection.INITIAL_DETAILS) {
            if ("competitionTypeId".equals(fieldName) || "openingDate".equals(fieldName)) {
                throw new IllegalStateException("Cannot update an initial details field that is disabled");
            }
        }
    }

    @Override
    public boolean isInitialDetailsCompleteOrTouched(Long competitionId) {
        Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionId).getSuccess();
        return statuses.get(CompetitionSetupSection.INITIAL_DETAILS).isPresent();
    }

    @Override
    public boolean isCompetitionReadyToOpen(CompetitionResource competitionResource) {
        if (competitionResource.getCompetitionStatus() != CompetitionStatus.COMPETITION_SETUP) {
            return false;
        }

        Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionResource.getId()).getSuccess();

        Optional<CompetitionSetupSection> notDoneSection = getRequiredSectionsForReadyToOpen().stream()
                .filter(section -> isNotDoneSection(statuses, section))
                .findFirst();

        return !notDoneSection.isPresent();
    }

    private boolean isNotDoneSection(Map<CompetitionSetupSection, Optional<Boolean>> statuses, CompetitionSetupSection section) {
        return (!statuses.get(section).isPresent() || !statuses.get(section).get());
    }

    @Override
    public ServiceResult<Void> setCompetitionAsReadyToOpen(Long competitionId) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competitionResource.getCompetitionStatus() == CompetitionStatus.READY_TO_OPEN) {
            return serviceSuccess();
        }

        if (isCompetitionReadyToOpen(competitionResource)) {
            return competitionSetupRestService.markAsSetup(competitionId).toServiceResult();
        } else {
            return serviceFailure(new Error("competition.setup.not.ready.to.open", HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public ServiceResult<Void> setCompetitionAsCompetitionSetup(Long competitionId) {
        return competitionSetupRestService.returnToSetup(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteCompetition(long competitionId) {
        return competitionInviteRestService
                .getInviteStatistics(competitionId).toServiceResult().andOnSuccess(inviteStatistics -> {
                    if (inviteStatistics.getInvited() > 0 || inviteStatistics.getInviteList() > 0) {
                        return serviceFailure(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED);
                    } else {
                        return competitionSetupRestService.delete(competitionId).toServiceResult();
                    }
                });
    }

    @Override
    public ServiceResult<Void> addInnovationLead(Long competitionId, Long innovationLeadUserId) {
        return competitionRestService.addInnovationLead(competitionId, innovationLeadUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeInnovationLead(Long competitionId, Long innovationLeadUserId) {
        return competitionRestService.removeInnovationLead(competitionId, innovationLeadUserId).toServiceResult();
    }

    private List<CompetitionSetupSection> getRequiredSectionsForReadyToOpen() {
        List<CompetitionSetupSection> requiredSections = new ArrayList<>();
        requiredSections.add(CompetitionSetupSection.INITIAL_DETAILS);
        requiredSections.add(CompetitionSetupSection.TERMS_AND_CONDITIONS);
        requiredSections.add(CompetitionSetupSection.ADDITIONAL_INFO);
        requiredSections.add(ELIGIBILITY);
        requiredSections.add(CompetitionSetupSection.MILESTONES);
        requiredSections.add(CompetitionSetupSection.APPLICATION_FORM);
        requiredSections.add(CompetitionSetupSection.CONTENT);
        return requiredSections;
    }

    private void checkIfSubsectionIsInSection(CompetitionSetupSection section, CompetitionSetupSubsection subsection) {
        if (!section.getSubsections().contains(subsection)) {
            LOG.error("Subsection(" + subsection + ") not found on section " + section);
            throw new IllegalArgumentException();
        }
    }

}
