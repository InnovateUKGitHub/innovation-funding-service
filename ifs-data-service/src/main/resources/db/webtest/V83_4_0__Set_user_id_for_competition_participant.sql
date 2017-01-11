/*
This patch file is safe to remove during the next baseline of test data if using GenerateTestData.
This is following a recent change made to AssessorDataBuilder#registerUser.

In a new baseline dump, you should be able to observe the following lines:

INSERT INTO `competition_user` VALUES (1,6,'ASSESSOR',NULL,41,NULL,NULL,1);
INSERT INTO `competition_user` VALUES (2,4,'ASSESSOR',NULL,42,NULL,NULL,1);

being replaced with:

INSERT INTO `competition_user` VALUES (1,6,'ASSESSOR',91,41,NULL,NULL,1);
INSERT INTO `competition_user` VALUES (2,4,'ASSESSOR',91,42,NULL,NULL,1);

the difference being that the user_id is now set in both.
*/

SET @jeremyAlufsonEmail := 'worth.email.test+jeremy.alufson@gmail.com';
SELECT @userId := id FROM user u WHERE email = @jeremyAlufsonEmail;
UPDATE competition_user cu SET cu.user_id = @userId WHERE cu.user_id IS NULL AND cu.invite_id IN (
    SELECT i.id FROM invite i WHERE i.type = 'COMPETITION' AND i.email = @jeremyAlufsonEmail);