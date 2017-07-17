CREATE TABLE affiliation (
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    affiliation_type ENUM('EMPLOYER', 'PROFESSIONAL', 'PERSONAL', 'PERSONAL_FINANCIAL', 'FAMILY', 'FAMILY_FINANCIAL') NOT NULL,
    affiliation_exists TINYINT(1) NOT NULL,
    relation VARCHAR(255),
    organisation VARCHAR(255),
    position VARCHAR(255),
    description VARCHAR(255),
    created_by BIGINT(20) NOT NULL,
    created_on DATETIME NOT NULL,
    modified_on DATETIME NOT NULL,
    modified_by BIGINT(20) NOT NULL,
    CONSTRAINT affiliation_user_to_user_fk FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT affiliation_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
    CONSTRAINT affiliation_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
) DEFAULT CHARSET=utf8;