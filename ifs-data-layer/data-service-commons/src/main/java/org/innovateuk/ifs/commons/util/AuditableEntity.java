package org.innovateuk.ifs.commons.util;

import org.innovateuk.ifs.config.audit.AuditConfig;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 *
 * Base class to provide Spring Data Auditing.
 * <p>
 * Concrete entities should be mapped on to a table with the following columns:
 *
 * <ul>
 *     <li><pre>created_by</pre></li>
 *     <li><pre>created_on</pre></li>
 *     <li><pre>modified_by</pre></li>
 *     <li><pre>modified_on</pre></li>
 * </ul>
 *
 * @see AuditConfig
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity implements Auditable {

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="createdBy", referencedColumnName="id")
    private User createdBy;

    @CreatedDate
    private ZonedDateTime createdOn;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="modifiedBy", referencedColumnName="id")
    private User modifiedBy;

    @LastModifiedDate
    private ZonedDateTime modifiedOn;

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public User getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }
}
