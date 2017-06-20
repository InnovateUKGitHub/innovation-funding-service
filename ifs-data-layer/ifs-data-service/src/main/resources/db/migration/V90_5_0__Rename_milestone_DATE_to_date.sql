-- fix the case of milestone.date
ALTER TABLE milestone CHANGE DATE date datetime;