/** This script updates email addresses for test users to use plus and dot in suffix doesnt work in gmail  **/

UPDATE `comp_admin_emails` SET `email` = 'worth.email.test+admin1@gmail.com' WHERE `email` = 'worth.email.test.admin1@gmail.com';
UPDATE `comp_admin_emails` SET `email` = 'worth.email.test+admin2@gmail.com' WHERE `email` = 'worth.email.test.admin2@gmail.com';
UPDATE `comp_admin_emails` SET `email` = 'worth.email.test+admin3@gmail.com' WHERE `email` = 'worth.email.test.admin3@gmail.com';
UPDATE `comp_admin_emails` SET `email` = 'worth.email.test+admin4@gmail.com' WHERE `email` = 'worth.email.test.admin4@gmail.com';
