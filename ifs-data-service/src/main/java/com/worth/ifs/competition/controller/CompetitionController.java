package com.worth.ifs.competition.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.controller.AssessmentHandler;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.controller.AssessmentProcessHandler;
import com.worth.ifs.workflow.domain.*;
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




        return repository.findAll();

    }



    }
