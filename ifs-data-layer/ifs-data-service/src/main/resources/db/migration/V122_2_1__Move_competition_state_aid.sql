-- IFS-3288 Move the competition state aid flag from the competition type to the competition
ALTER TABLE competition_type DROP state_aid;
ALTER TABLE competition ADD state_aid BIT(1) NULL;