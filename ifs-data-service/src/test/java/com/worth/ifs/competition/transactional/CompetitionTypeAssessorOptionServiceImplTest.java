package com.worth.ifs.competition.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;
import com.worth.ifs.competition.fixtures.CompetitionTypeAssessorOptionFixture;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionTypeAssessorOptionServiceImplTest extends BaseUnitTestMocksTest {

	@InjectMocks
	private CompetitionTypeAssessorOptionService competitionTypeAssessorOptionService = new CompetitionTypeAssessorOptionServiceImpl();

	@Test
	public void testFindAllByCompetitionType() throws Exception {
		List<CompetitionTypeAssessorOption> options = CompetitionTypeAssessorOptionFixture.programmeAssessorOptionsList();
		List<CompetitionTypeAssessorOptionResource> expectedResponse = CompetitionTypeAssessorOptionFixture.programmeAssessorOptionResourcesList();

		when(competitionTypeAssessorOptionRepositoryMock.findByCompetitionTypeId(anyLong())).thenReturn(options);
		when(competitionTypeAssessorOptionMapperMock.mapToResource(same(options.get(0)))).thenReturn((expectedResponse.get(0)));
		when(competitionTypeAssessorOptionMapperMock.mapToResource(same(options.get(1)))).thenReturn((expectedResponse.get(1)));
		when(competitionTypeAssessorOptionMapperMock.mapToResource(same(options.get(2)))).thenReturn((expectedResponse.get(2)));

		List<CompetitionTypeAssessorOptionResource> actualResponse = competitionTypeAssessorOptionService.findAllByCompetitionType(1L).getSuccessObject();

		assertEquals(expectedResponse, actualResponse);
		verify(competitionTypeAssessorOptionRepositoryMock, only()).findByCompetitionTypeId(1L);
	}
}
