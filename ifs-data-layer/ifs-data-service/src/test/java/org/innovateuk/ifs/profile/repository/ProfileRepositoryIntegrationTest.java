package org.innovateuk.ifs.profile.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.junit.Assert.assertTrue;

public class ProfileRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProfileRepository> {

    @Override
    @Autowired
    protected void setRepository(ProfileRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void saveWithInnovationArea() {
        loginPaulPlum();
        User user = userRepository.findOne(getPaulPlum().getId());

        InnovationArea innovationArea = innovationAreaRepository.findByName("Emerging technology");
        Profile profile = newProfile()
                .withCreatedBy(user)
                .withCreatedOn(ZonedDateTime.now())
                .withModifiedBy(user)
                .withModifiedOn(ZonedDateTime.now())
                .build();
        Profile savedProfile = repository.save(profile);
        savedProfile.addInnovationArea(innovationArea);
        savedProfile = repository.save(savedProfile);

        flushAndClearSession();

        Profile retrievedProfile = repository.findOne(savedProfile.getId());

        assertTrue(retrievedProfile.getInnovationAreas().contains(innovationArea));
    }
}
