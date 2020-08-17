package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

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

    @OneToMany(mappedBy = "knowledgeBase",
            cascade = CascadeType.ALL)
    private List<KnowledgeBaseAddress> addresses = new ArrayList<>();

    KnowledgeBase() {
        // for ORM
    }

    public KnowledgeBase(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OrganisationTypeEnum getOrganisationTypeEnum() {
        return OrganisationTypeEnum.getFromId(getOrganisationType().getId());
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
