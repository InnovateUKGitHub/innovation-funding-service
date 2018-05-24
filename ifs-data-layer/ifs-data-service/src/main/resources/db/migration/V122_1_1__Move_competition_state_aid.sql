-- IFS-3288 Move the competition state aid flag from the competition type to the competition
ALTER TABLE competition_type DROP state_aid;
ALTER TABLE competition ADD state_aid bit(1) NULL;

-- Set the state aid flag to true for all existing competitions
UPDATE competition set state_aid = 1;