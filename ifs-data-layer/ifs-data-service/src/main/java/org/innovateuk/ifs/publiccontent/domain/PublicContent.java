package org.innovateuk.ifs.publiccontent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity to represent the competitions content that is visible to the public.
 */
@Setter
@Getter
@Entity
public class PublicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long competitionId;

    private ZonedDateTime publishDate;

    private String shortDescription;

    private String projectFundingRange;

    @Column(columnDefinition = "LONGTEXT")
    private String eligibilitySummary;

    private String projectSize;

    private Boolean inviteOnly;

    @Column(unique = true)
    private String hash;

    @Column(length = 5000)
    private String summary;

    @OneToMany(mappedBy="publicContent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContentSection> contentSections;

    @OneToMany(mappedBy = "publicContent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "publicContent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContentEvent> contentEvents;

    public void generateHashIfNecessary() {
        if (publicContentHasNotBeenPublished() && hashHasNotBeenGenerated()) {
            this.hash = UUID.randomUUID().toString();
        }
    }

    private boolean publicContentHasNotBeenPublished() {
        return this.publishDate == null;
    }

    private boolean hashHasNotBeenGenerated() {
        return this.hash == null;
    }
}