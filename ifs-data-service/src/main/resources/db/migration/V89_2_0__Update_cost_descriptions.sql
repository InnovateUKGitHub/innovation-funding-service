update question q
inner join section s on s.id=q.section_id
set q.description=REPLACE(q.description, 'You may incur overhead costs', 'You can incur overhead costs')
where s.name='Overhead costs';

update question q
inner join section s on s.id=q.section_id
set q.description=REPLACE(q.description, 'You may subcontract work if you', 'You can subcontract work if you')
where s.name='Subcontracting costs';

update question q
inner join section s on s.id=q.section_id
set q.description=REPLACE(q.description, 'You may claim the labour costs', 'You can claim the labour costs')
where s.name='Labour';