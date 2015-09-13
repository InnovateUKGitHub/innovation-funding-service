package com.worth.ifs.application.service;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionServiceImpl implements CompetitionService {
    @Autowired
    CompetitionsRestService competitionsRestService;

    @Override
    public List<Competition> getAllCompetitions() {
        return competitionsRestService.getAll();
    }
}
