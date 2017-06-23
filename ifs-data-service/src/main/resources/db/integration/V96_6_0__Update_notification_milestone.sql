UPDATE milestone SET date = '2036-03-15 09:00:00' WHERE type = 'NOTIFICATIONS' AND competition_id = 1;
UPDATE milestone SET date = NULL WHERE type = 'LINE_DRAW' AND competition_id = 1;