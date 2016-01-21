package com.worth.ifs.competition.controller;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@ExposesResourceFor(CompetitionResource.class)
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    CompetitionRepository repository;

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
