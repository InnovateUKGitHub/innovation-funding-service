package com.worth.ifs.competition.controller;

import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    ProcessRoleRepository userAppRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;

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
