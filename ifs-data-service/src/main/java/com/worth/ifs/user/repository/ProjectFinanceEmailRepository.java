package com.worth.ifs.user.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.worth.ifs.user.domain.ProjectFinanceEmail;

public interface ProjectFinanceEmailRepository  extends PagingAndSortingRepository<ProjectFinanceEmail, Long> {
	ProjectFinanceEmail findOneByEmail(@Param("email") String email);
}
