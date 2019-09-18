package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInvite;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class InterviewInviteRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InterviewInviteRepository> {

    private Competition competition;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    @Override
    protected void setRepository(InterviewInviteRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        loginCompAdmin();

        competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withName("competition")
                .build());

        setLoggedInUser(null);
    }

    @Test
    public void getByEmailAndCompetitionId() {
        InterviewInvite invite = new InterviewInvite(userMapper.mapToDomain(getPaulPlum()), "hash", competition);
        InterviewInvite saved = repository.save(invite);

        InterviewInvite retrievedInvite = repository.getByEmailAndCompetitionId("paul.plum@gmail.com", competition.getId());
        assertNotNull(retrievedInvite);

        assertEquals(saved, retrievedInvite);
    }

    @Test
    public void getByCompetitionIdAndStatus() {
        Competition otherCompetition = newCompetition().build();

        repository.saveAll(newInterviewInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        Page<InterviewInvite> pageResult = repository.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);

        List<InterviewInvite> retrievedInvites = pageResult.getContent();

        assertEquals(1, retrievedInvites.size());

        assertEquals(competition, retrievedInvites.get(0).getTarget());
        assertEquals("john@example.com", retrievedInvites.get(0).getEmail());
        assertEquals("1dc914e2-d076-4b15-9fa6-99ee5b711613", retrievedInvites.get(0).getHash());
        assertEquals("John Barnes", retrievedInvites.get(0).getName());
        assertEquals(CREATED, retrievedInvites.get(0).getStatus());
    }

    @Test
    public void getByCompetitionIdAndStatus_asList() throws Exception {
        Competition otherCompetition = newCompetition().build();

        repository.saveAll(newInterviewInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        List<InterviewInvite> retrievedInvites = repository.getByCompetitionIdAndStatus(competition.getId(), CREATED);

        assertEquals(1, retrievedInvites.size());

        assertEquals(competition, retrievedInvites.get(0).getTarget());
        assertEquals("john@example.com", retrievedInvites.get(0).getEmail());
        assertEquals("1dc914e2-d076-4b15-9fa6-99ee5b711613", retrievedInvites.get(0).getHash());
        assertEquals("John Barnes", retrievedInvites.get(0).getName());
        assertEquals(CREATED, retrievedInvites.get(0).getStatus());
    }

    @Test
    public void countByCompetitionIdAndStatus() {
        Competition otherCompetition = newCompetition().build();

        repository.saveAll(newInterviewInvite()
                .with(id(null))
                .withCompetition(competition, otherCompetition, competition, otherCompetition, competition, otherCompetition)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "michael@example.com", "rachel@example.com")
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e", "0253e4b9-8f76-4a55-b40b-689a025a9129",
                        "cba968ac-d792-4f41-b3d2-8a92980d54ce", "9e6032a5-39d5-4ec8-a8b2-883f5956a809", "75a615bf-3e5a-4ae3-9479-1753ae438108")
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Michael King", "Rachel Fish")
                .withStatus(CREATED, CREATED, OPENED, OPENED, SENT, SENT)
                .build(6));

        long count = repository.countByCompetitionIdAndStatusIn(competition.getId(), of(CREATED, OPENED));
        assertEquals(2, count);
    }

    @Test
    public void save() {
        InterviewInvite invite = new InterviewInvite(userMapper.mapToDomain(getPaulPlum()), "hash", competition);
        repository.save(invite);

        flushAndClearSession();

        long id = invite.getId();

        InterviewInvite retrievedInvite = repository.findById(id).get();

        assertEquals("Professor Plum", retrievedInvite.getName());
        assertEquals("paul.plum@gmail.com", retrievedInvite.getEmail());
        assertEquals("hash", retrievedInvite.getHash());
        assertEquals(competition.getId(), retrievedInvite.getTarget().getId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void save_duplicateHash() {
        repository.save(new InterviewInvite(userMapper.mapToDomain(getPaulPlum()), "sameHash", competition));
        repository.save(new InterviewInvite(userMapper.mapToDomain(getFelixWilson()), "sameHash", competition));
    }

    @Test
    public void deleteByCompetitionIdAndStatus() throws Exception {
        List<InterviewInvite> invites = newInterviewInvite()
                .withCompetition(competition)
                .withEmail("test1@test.com", "test2@test.com")
                .withHash("hash1", "hash2")
                .withName("Test Tester 1", "Test Tester 2")
                .build(2);

        HashSet<InviteStatus> inviteStatuses = newHashSet(CREATED);

        repository.saveAll(invites);
        flushAndClearSession();

        assertEquals(2, repository.countByCompetitionIdAndStatusIn(competition.getId(), inviteStatuses));

        repository.deleteByCompetitionIdAndStatus(competition.getId(), CREATED);
        flushAndClearSession();

        assertEquals(0, repository.countByCompetitionIdAndStatusIn(competition.getId(), inviteStatuses));
    }
}
