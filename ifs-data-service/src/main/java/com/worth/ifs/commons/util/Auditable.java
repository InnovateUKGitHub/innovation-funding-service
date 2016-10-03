package com.worth.ifs.commons.util;

import com.worth.ifs.user.domain.User;

import java.time.LocalDateTime;

/**
 * An Entity supplying read-only auditable properties.
 * <p>
 * This can be implemented to provide support for Spring Data Auditing, in which case classes should have
 * the <pre>@EntityListeners(AuditingEntityListener.class)</pre> annotation for auto-populating the createdBy and modifiedBy
 * {@link User}s.
 * <p>
 * Use @{@link AuditableEntity} for a convienience base class with JPA mapping to standard column names.
 *
 * @see AuditableEntity
 * @see com.worth.ifs.config.AuditConfig
 */
public interface Auditable {
    User getCreatedBy();

    LocalDateTime getCreatedOn();

    User getModifiedBy();

    LocalDateTime getModifiedOn();
}
