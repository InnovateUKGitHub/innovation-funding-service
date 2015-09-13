package com.worth.ifs.application.service;

import com.worth.ifs.competition.domain.Competition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompetitionService {
    List<Competition> getAllCompetitions();
}
