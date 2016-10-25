-- Add competition columns for saving data on competition setup.
ALTER TABLE competition ADD COLUMN status VARCHAR(45) NULL DEFAULT NULL COMMENT '';
ALTER TABLE competition ADD COLUMN budget_code VARCHAR(255);
ALTER TABLE competition ADD COLUMN code VARCHAR(255);
ALTER TABLE competition ADD COLUMN paf_code VARCHAR(255);
ALTER TABLE competition ADD COLUMN executive_user_id BIGINT;
ALTER TABLE competition ADD COLUMN lead_technologist_user_id BIGINT;
ALTER TABLE competition ADD CONSTRAINT FK_oyhemo48a8fegie1npbk759s2 FOREIGN KEY (executive_user_id) REFERENCES user (id);
ALTER TABLE competition ADD CONSTRAINT FK_7gcrp1ms5k4o9ehrci3uqx6hg FOREIGN KEY (lead_technologist_user_id) REFERENCES user (id);


-- Add competition Type.
CREATE TABLE competition_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255),
    PRIMARY KEY (id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

ALTER TABLE competition ADD COLUMN competition_type_id BIGINT;
ALTER TABLE competition ADD CONSTRAINT FK_4ymkkm30gi0r9w65d1xuawyws FOREIGN KEY (competition_type_id) REFERENCES competition_type (id);


-- Add Category structure, for example for Innovation Sector and Innovation Area.
CREATE TABLE category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255),
    type VARCHAR(255),
    parent_id BIGINT,
    PRIMARY KEY (id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

CREATE TABLE category_link (
    id BIGINT NOT NULL AUTO_INCREMENT,
    class_name VARCHAR(255),
    class_pk BIGINT,
    category_id BIGINT,
    PRIMARY KEY (id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

ALTER TABLE category_link ADD CONSTRAINT UK_3ba9x0jh5patrfe48pcb4twko UNIQUE (
	class_name,
	class_pk,
	category_id
	);

ALTER TABLE category ADD CONSTRAINT FK_81thrbnb8c08gua7tvqj7xdqk FOREIGN KEY (parent_id) REFERENCES category (id);
ALTER TABLE category_link ADD CONSTRAINT FK_3kai632y5lw33gxmec08p54s0 FOREIGN KEY (category_id) REFERENCES category (id);


-- Add Competition Milestones
CREATE TABLE milestone (
    id BIGINT NOT NULL AUTO_INCREMENT,
    DATE DATETIME,
    NAME VARCHAR(255),
    competition_id BIGINT,
    PRIMARY KEY (id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

ALTER TABLE milestone ADD CONSTRAINT FK_d2gmngr50hf7lkjv8s9mxhfms FOREIGN KEY (competition_id) REFERENCES competition (id);

create table competition_setup_status (
	competition_id bigint not null,
	section VARCHAR(255),
	status tinyint(1) not null
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

alter table competition_type add column state_aid bit;