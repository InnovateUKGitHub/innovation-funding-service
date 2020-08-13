package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.organisation.KnowledgeBaseAddress;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType organisationType;

    @OneToMany(mappedBy = "knowledge_base",
            cascade = CascadeType.ALL)
    private List<KnowledgeBaseAddress> addresses = new ArrayList<>();

    KnowledgeBase() {
        // for ORM
    }

    public KnowledgeBase(String name, String identifier, OrganisationType organisationType) {
        this.name = name;
        this.identifier = identifier;
        this.organisationType = organisationType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public List<KnowledgeBaseAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<KnowledgeBaseAddress> addresses) {
        this.addresses = addresses;
    }
}
