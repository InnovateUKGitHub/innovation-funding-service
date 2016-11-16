package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.service.CompetitionSetupQuestionRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

	@Autowired
	private CompetitionSetupQuestionRestService competitionSetupQuestionRestService;


    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getQuestion(final Long questionId) {
        return competitionSetupQuestionRestService.getByQuestionId(questionId).toServiceResult();
    }

    @Override
	public ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionRestService.save(competitionSetupQuestionResource).toServiceResult();
    }
}
