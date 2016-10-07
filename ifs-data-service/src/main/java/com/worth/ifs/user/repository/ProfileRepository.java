package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long> {
}
