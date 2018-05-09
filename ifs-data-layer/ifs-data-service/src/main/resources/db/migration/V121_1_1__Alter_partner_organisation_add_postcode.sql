--IFS-3470 - We are going to rename the column post_code to postcode so that the name matches with the address table
-- As part of ZDD, this will be carried out in three steps (releases). This file performs the first step only.
-- In the first step, we add a new column postcode and copy all data from post_code to postcode and also do code changes to accommodate this.
-- In the second step, we will again copy the data from post_code to postcode with additional code changes. This will make the post_code column redundant.
-- In the third step, we will delete the old post_code column. There will be no code changes in this step.

ALTER TABLE partner_organisation ADD COLUMN postcode VARCHAR(255);

--Bear in mind, that there is no where clause here and which means we are updating all rows
UPDATE partner_organisation
SET postcode = post_code;