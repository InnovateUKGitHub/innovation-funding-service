package org.innovateuk.ifs.publiccontent.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Entity that represents the public content associated to a dated event.
 */
@Entity
public class ContentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_content_id", referencedColumnName = "id")
    private PublicContent publicContent;

    private ZonedDateTime date;

    @Column(length=5000)
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublicContent getPublicContent() {
        return publicContent;
    }

    public void setPublicContent(PublicContent publicContent) {
        this.publicContent = publicContent;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
