package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_content_id", referencedColumnName = "id")
    private PublicContent publicContent;

    private String keyword;


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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
