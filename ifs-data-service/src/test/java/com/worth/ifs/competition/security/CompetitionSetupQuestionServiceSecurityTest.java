package com.worth.ifs.competition.security;

import com.worth.ifs.*;
import com.worth.ifs.commons.service.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competition.transactional.*;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.*;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionSetupQuestionServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupQuestionService> {

    @Override
    protected Class<? extends CompetitionSetupQuestionService> getClassUnderTest() {
        return TestCompetitionService.class;
    }


    @Test
    public void testGetByQuestionId() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.getByQuestionId(1L), () -> {
        });
    }

    @Test
    public void testSave() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.save(newCompetitionSetupQuestionResource().build()), () -> {
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionService implements CompetitionSetupQuestionService {

        public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
            return null;
        }

        public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
            return null;
        }


    }
}
