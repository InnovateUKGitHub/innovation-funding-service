-- IFS-3288 Set the state aid flag to true for all existing competitions
UPDATE competition SET state_aid = 1;