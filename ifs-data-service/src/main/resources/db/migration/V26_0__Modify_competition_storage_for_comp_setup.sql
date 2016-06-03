
ALTER TABLE `competition`
    ADD COLUMN `status` VARCHAR(45) NULL DEFAULT NULL COMMENT '' ;

alter table competition add column budget_code varchar(255);
alter table competition add column `code` varchar(255);
alter table competition add column `paf_code` varchar(255);
alter table competition add column executive_user_id bigint;
alter table competition add column lead_technologist_user_id bigint;
alter table competition add constraint FK_oyhemo48a8fegie1npbk759s2 foreign key (executive_user_id) references user (id);
alter table competition add constraint FK_7gcrp1ms5k4o9ehrci3uqx6hg foreign key (lead_technologist_user_id) references user (id);

CREATE TABLE competition_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);
alter table competition add column competition_type_id bigint;
alter table competition add constraint FK_4ymkkm30gi0r9w65d1xuawyws foreign key (competition_type_id) references competition_type (id);


CREATE TABLE category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    type VARCHAR(255),
    parent_id BIGINT,
    PRIMARY KEY (id)
);
CREATE TABLE category_link (
    id BIGINT NOT NULL AUTO_INCREMENT,
    class_name VARCHAR(255),
    class_pk BIGINT,
    category_id BIGINT,
    PRIMARY KEY (id)
);

alter table category_link add constraint UK_3ba9x0jh5patrfe48pcb4twko unique (class_name, class_pk, category_id);
alter table category add constraint FK_81thrbnb8c08gua7tvqj7xdqk foreign key (parent_id) references category (id);
alter table category_link add constraint FK_3kai632y5lw33gxmec08p54s0 foreign key (category_id) references category (id);

create table milestone (id bigint not null auto_increment, date datetime, name varchar(255), competition_id bigint, primary key (id));
alter table milestone add constraint FK_d2gmngr50hf7lkjv8s9mxhfms foreign key (competition_id) references competition (id);