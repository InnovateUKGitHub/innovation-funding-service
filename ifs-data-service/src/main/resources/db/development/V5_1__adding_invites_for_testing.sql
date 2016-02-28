INSERT  IGNORE INTO `invite_organisation` (`id`, `organisation_name`, `organisation_id`)
    VALUES
        (1,NULL,3),
        (2,'Worth Internet Systems',NULL);


INSERT IGNORE INTO `invite` (`id`, `email`, `hash`, `name`, `status`, `application_id`, `invite_organisation_id`)
    VALUES
    (1,'john@empire.com','1d92a6ace9030f2d992f47ea60529028fd49542dffd6b179f68fae072b4f1cc61f12a419b79a5267','John','SEND',1,1),
    (2,'rogier@worth.systems','4e09372b85241cb03137ffbeb2110a1552daa1086b0bce0ff7d8ff5d2063c8ffc10e943acf4a3c7a','Rogier','SEND',1,2),
    (3,'Michael@worth.systems','b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96','michael','SEND',1,2);

