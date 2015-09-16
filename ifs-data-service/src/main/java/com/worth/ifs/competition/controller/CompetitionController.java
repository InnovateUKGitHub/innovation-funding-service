package com.worth.ifs.competition.controller;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.workflow.controller.ProcessHandler;
import com.worth.ifs.workflow.domain.*;
import com.worth.ifs.workflow.domain.Process;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    CompetitionsRepository repository;

    @Autowired
    ProcessHandler processHandler;

    @Autowired
    AssessmentRepository assessmentRepository;


    private final Log log = LogFactory.getLog(getClass());


    @RequestMapping("/findById/{id}")
    public Competition getCompetitionById(@PathVariable("id") final Long id) {
        return repository.findById(id);
    }

    @RequestMapping("/id/{id}")
    public Competition getApplicationById(@PathVariable("id") final Long id) {
        return repository.findById(id);
    }

    @RequestMapping("/findAll")
    public List<Competition> findAll() {


        Process p  = processHandler.getProcessesByAssigneeAndEvent (new Long(3), ProcessEvent.ASSESSMENT_INVITATION).get(0);
        //Process(ProcessEvent event, ProcessStatus status, Long assigneeId, Long subjectId)
        try {
            processHandler.saveProcess(p);
        }
        catch (Exception e) {
            System.out.println("Exception trying to save a new process");
        }

        System.out.println("There are " + processHandler.getProcessesByAssigneeAndEvent(new Long(1), ProcessEvent.ASSESSMENT_INVITATION).size() + " @ with assignee 1 and event Assessment Invit");
        System.out.println("There are " + processHandler.getProcessesByAssigneeAndEvent(new Long(3),ProcessEvent.ASSESSMENT_INVITATION).size() + " @ with assignee 3 and event Assessment Invit");


        Assessment a = new Assessment(p);

        assessmentRepository.save(a);


        return repository.findAll();

    }
}
