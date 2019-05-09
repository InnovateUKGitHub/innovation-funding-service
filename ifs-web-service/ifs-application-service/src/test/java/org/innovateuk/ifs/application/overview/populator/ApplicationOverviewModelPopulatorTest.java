package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationOverviewModelPopulatorTest {

    @InjectMocks
    private ApplicationOverviewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private SectionRestService sectionRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private SectionStatusRestService sectionStatusRestService;
    @Mock
    private InviteService inviteService;
    @Mock
    private QuestionService questionService;
    
    @Test
    public void test() {
        Assert.assertTrue(true);
    }
}
