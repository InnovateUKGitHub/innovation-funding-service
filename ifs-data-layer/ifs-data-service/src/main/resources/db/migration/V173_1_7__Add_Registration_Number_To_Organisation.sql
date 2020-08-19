-- Can we just have one column called unique identifier and then a type column which could be (INTERNATIONAL, COMPANIES HOUSE, UKRPN, REGISTRATION NUMBER)
ALTER TABLE organisation ADD COLUMN registration_number VARCHAR(10) DEFAULT NULL;
