-- IFS-2630 - Update "gol-rejected" and "gol-approved" process events to have "signed-" prefix
UPDATE process SET event = 'signed-gol-approved' WHERE event = "gol-approved";
UPDATE process SET event = 'signed-gol-rejected' WHERE event = "gol-rejected";