#this resets the data in v15_0 as we need an open competition for acceptance tests
UPDATE `competition` SET  `end_date`='2016-09-09 12:00:00',`start_date`='2016-03-15 09:00:00' WHERE `id`='1';
