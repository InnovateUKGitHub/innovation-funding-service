package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.finance.domain.OrganisationSize;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.repository.OrganisationSizeRepository;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by danielsmith on 08/05/2017.
 */
public abstract class ApplicationSubmissionControllerIntegrationTest<ControllerType> extends BaseControllerIntegrationTest<ControllerType>{

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationSizeRepository organisationSizeRepository;

    public void makeApplicationSubmittable(long applicationId) {
        // set up application to be in a submittable state, by setting up responses to questions for all applicants
        final List<ProcessRole> processRoles = new LinkedList<>();
        processRoleRepository.findByApplicationId(applicationId).forEach(pr -> processRoles.add(pr));
        OrganisationSize defaultSize = organisationSizeRepository.findOne(1L);
        questionRepository.findByCompetitionId(applicationRepository.findOne(applicationId).getCompetition().getId()).forEach(q -> {
            Question question = questionRepository.findOne(q.getId());
            processRoles.forEach(processRole -> {
                if(questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(q.getId(), applicationId, processRole.getId()) == null) {
                    QuestionStatus newQs = new QuestionStatus(question, applicationRepository.findOne(applicationId), processRole, Boolean.TRUE);
                    questionStatusRepository.save(newQs);
                    flushAndClearSession();
                }
            });
            processRoles.forEach(processRole -> {
                questionStatusRepository.findByQuestionIdAndApplicationId(q.getId(), applicationId).forEach(qs -> {
                    formInputRepository.findByCompetitionId(qs.getQuestion().getCompetition().getId()).forEach( fi -> {
                        if(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, processRole.getId(), fi.getId()) == null) {
                            FormInputResponse newResponse = new FormInputResponse();
                            newResponse.setFormInput(fi);
                            newResponse.setUpdatedBy(processRole);
                            newResponse.setUpdateDate(ZonedDateTime.now());
                            newResponse.setValue("1");
                            newResponse.setApplication(applicationRepository.findOne(applicationId));
                            formInputResponseRepository.save(newResponse);
                            flushAndClearSession();
                        }
                    });
                    if (qs.getMarkedAsComplete() != Boolean.TRUE && questionStatusRepository.findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(q.getId(), applicationId, processRole.getId()) == null) {
                        qs.markAsComplete();
                        qs.setMarkedAsCompleteBy(processRole);
                        questionStatusRepository.save(qs);
                        flushAndClearSession();
                    }
                });
            });
        });
        // make sure organisation has a size entry in grant max claims
        processRoles.forEach(processRole -> {
            grantClaimMaximumRepository.findAll().forEach(gcm -> {
                if (gcm.getOrganisationType().getId() == organisationRepository.findByProcessRoles(processRole).getOrganisationType().getId()) {
                    gcm.setOrganisationSize(defaultSize);
                }
            });
        });
    }
}
