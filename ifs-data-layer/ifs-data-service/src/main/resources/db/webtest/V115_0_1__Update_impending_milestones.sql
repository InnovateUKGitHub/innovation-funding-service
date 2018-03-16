-- For IFS-2986 - shifting immediate upcoming deadlines
UPDATE milestone SET date = DATE_ADD(date, INTERVAL 1 MONTH) where date like '2018-03-%';