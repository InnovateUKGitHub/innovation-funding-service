package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.EMPTY_SET;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MilestonePermissionRulesTest extends BasePermissionRulesTest<MilestonePermissionRules> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

	@Override
	protected MilestonePermissionRules supplyPermissionRulesUnderTest() {
		return new MilestonePermissionRules();
	}

    @Test
    public void internalUsersOtherThanInnovationLeadsCanViewAllMilestones() {
        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(Role.INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER) && allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId.id(1L), user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId.id(1L), user));
            }
        });
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<Role> innovationLeadRoles = singletonList(Role.INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId
                (innovationLeadAssignedToCompetition.getId()).build()).build(1);

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyStakeholdersAssignedToCompCanAccess() {
        List<Role> stakeholderRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        List<Stakeholder> stakeholders = newStakeholder().withUser(newUser().withId
                (stakeholderAssignedToCompetition.getId()).build()).build(1);

        when(stakeholderRepository.findStakeholders(1L)).thenReturn(stakeholders);

        assertTrue(rules.stakeholdersCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholdersCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), stakeholderNotAssignedToCompetition));
    }

    @Test
    public void internalUsersCanReadMilestoneByType() {
        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId.id(1L), user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId.id(1L), user));
            }
        });
    }
}
