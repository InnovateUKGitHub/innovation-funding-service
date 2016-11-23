package com.worth.ifs.competition.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.competition.domain.AssessorCountOption;
import com.worth.ifs.competition.fixtures.AssessorCountOptionFixture;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessorCountOptionServiceImplTest extends BaseUnitTestMocksTest {

	@InjectMocks
	private AssessorCountOptionService assessorCountOptionService = new AssessorCountOptionServiceImpl();

	@Test
	public void testFindAllByCompetitionType() throws Exception {
		List<AssessorCountOption> options = AssessorCountOptionFixture.programmeAssessorOptionsList();
		List<AssessorCountOptionResource> expectedResponse = AssessorCountOptionFixture.programmeAssessorOptionResourcesList();

		when(assessorCountOptionRepositoryMock.findByCompetitionTypeId(anyLong())).thenReturn(options);
		when(assessorCountOptionMapperMock.mapToResource(same(options.get(0)))).thenReturn((expectedResponse.get(0)));
		when(assessorCountOptionMapperMock.mapToResource(same(options.get(1)))).thenReturn((expectedResponse.get(1)));
		when(assessorCountOptionMapperMock.mapToResource(same(options.get(2)))).thenReturn((expectedResponse.get(2)));

		List<AssessorCountOptionResource> actualResponse = assessorCountOptionService.findAllByCompetitionType(1L).getSuccessObject();

		assertEquals(expectedResponse, actualResponse);
		verify(assessorCountOptionRepositoryMock, only()).findByCompetitionTypeId(1L);
	}
}
