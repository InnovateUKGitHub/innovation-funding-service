package org.innovateuk.ifs.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_pre_registration_config")
public class ApplicationPreRegistrationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @Column(name = "pre_registration", nullable = false)
    private boolean enableForEOI;

}