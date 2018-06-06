-- IFS-3667 Temporarily reinstate the state aid flag on competition_type which was removed in IFS-3288
-- Note is a redundant field and has been replaced by competition.state_aid
-- This field should be removed again once the ETL scripts have been updated to match by InnovateUK
ALTER TABLE competition_type ADD state_aid BIT(1) DEFAULT TRUE;