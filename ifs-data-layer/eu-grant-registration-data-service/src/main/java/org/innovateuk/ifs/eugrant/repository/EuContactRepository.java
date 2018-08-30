package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EuContactRepository extends CrudRepository<EuContact, UUID> {
}
