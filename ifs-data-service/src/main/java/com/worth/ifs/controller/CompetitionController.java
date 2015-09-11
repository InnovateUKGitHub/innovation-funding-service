package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    CompetitionsRepository repository;
    @Autowired
    UserApplicationRoleRepository userAppRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/id/{id}")
    public Competition getApplicationById(@PathVariable("id") final Long id) {
        return repository.findById(id);
    }

    @RequestMapping("/findAll")
    public List<Competition> findAll() {
        return repository.findAll();
    }
}
