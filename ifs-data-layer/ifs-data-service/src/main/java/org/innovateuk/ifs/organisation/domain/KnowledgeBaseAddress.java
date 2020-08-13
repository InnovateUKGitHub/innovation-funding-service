package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;

import javax.persistence.*;

@Entity
public class KnowledgeBaseAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    private KnowledgeBase knowledgeBase;

    public KnowledgeBaseAddress() {
    }

    public KnowledgeBaseAddress(Address address, KnowledgeBase knowledgeBase) {
        this.address = address;
        this.knowledgeBase = knowledgeBase;
    }

    public Long getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
}
