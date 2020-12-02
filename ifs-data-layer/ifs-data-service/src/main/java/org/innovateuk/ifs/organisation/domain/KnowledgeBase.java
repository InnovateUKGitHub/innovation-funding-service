package org.innovateuk.ifs.organisation.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseType;

import javax.persistence.*;

@Entity
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    private KnowledgeBaseType type;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    public KnowledgeBase() {
        // for ORM
    }

    public KnowledgeBase(Long id, String name, String registrationNumber, KnowledgeBaseType type, Address address) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.address = address;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public KnowledgeBaseType getType() {
        return type;
    }

    public void setType(KnowledgeBaseType type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
