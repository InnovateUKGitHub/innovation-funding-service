-- IFS-5446 horizon 2020 type only active on test environments.
update competition_type set active = 1 where name='Horizon 2020'