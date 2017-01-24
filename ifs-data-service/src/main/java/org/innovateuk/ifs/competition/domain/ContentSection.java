package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;

import javax.persistence.*;
import java.util.List;

/**
 * Entity that represents a section of content visible to the public.
 */
@Entity
public class ContentSection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_content_id", referencedColumnName = "id")
    private PublicContent publicContent;

    @Enumerated(EnumType.STRING)
    private PublicContentSection type;

    @Enumerated(EnumType.STRING)
    private PublicContentStatus status;

    @OneToMany(mappedBy="contentSection")
    private List<ContentGroup> contentGroup;

    public PublicContent getPublicContent() {
        return publicContent;
    }

    public void setPublicContent(PublicContent publicContent) {
        this.publicContent = publicContent;
    }

    public PublicContentSection getType() {
        return type;
    }

    public void setType(PublicContentSection type) {
        this.type = type;
    }

    public PublicContentStatus getStatus() {
        return status;
    }

    public void setStatus(PublicContentStatus status) {
        this.status = status;
    }

    public List<ContentGroup> getContentGroup() {
        return contentGroup;
    }

    public void setContentGroup(List<ContentGroup> contentGroup) {
        this.contentGroup = contentGroup;
    }
}
