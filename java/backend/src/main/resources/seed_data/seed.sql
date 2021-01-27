insert into cases select id, 2542345663454321  from generate_series(2542345663454323, 2542345663564324) id;
insert into events select id, 'CreateClaim', id, now(), 'Created', 'a62f4e6f-c223-467d-acc1-fe91444783f5'
from generate_series(2542345663454323, 2542345663564324) id;

insert into parties select id, id, '{}'::jsonb
from generate_series(2542345663454323, 2542345663564324) id;

analyze;
