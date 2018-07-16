-- IFS-3673 remove deprecated competition_type.state_aid field from database.
ALTER TABLE competition_type DROP state_aid;