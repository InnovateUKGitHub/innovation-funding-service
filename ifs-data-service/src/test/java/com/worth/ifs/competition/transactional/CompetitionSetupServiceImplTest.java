package com.worth.ifs.competition.transactional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competitiontemplate.domain.CompetitionTemplate;
import com.worth.ifs.competitiontemplate.repository.CompetitionTemplateRepository;

import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competitiontemplate.builder.CompetitionTemplateBuilder.newCompetitionTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

	@InjectMocks
	private CompetitionSetupServiceImpl service;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private CompetitionTypeRepository competitionTypeRepository;
    @Mock
    private CompetitionTemplateRepository competitionTemplateRepository;
    
    @Test
    public void testInitialiseForm() {
    	Competition competition = newCompetition().build();
    	CompetitionTemplate competitionTemplate = newCompetitionTemplate().build();
    	
    	when(competitionRepository.findById(123L)).thenReturn(competition);
    	when(competitionTemplateRepository.findByCompetitionTypeId(4L)).thenReturn(competitionTemplate);
    	
    	ServiceResult<Void> result = service.initialiseFormForCompetitionType(123L, 4L);
    	
    	assertTrue(result.isSuccess());
    }
}
