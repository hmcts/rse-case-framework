create table cases(
  case_id bigserial not null primary key
);

create table events(
  case_id bigint references cases(case_id) not null,
  id varchar not null,
  sequence_number serial not null,
  timestamp timestamp not null,
  state varchar not null,
  user_forename varchar not null,
  user_surname varchar not null,
  unique(case_id, sequence_number)
);

create table unspec_cases(
    case_id bigint references cases(case_id) not null primary key,
    data jsonb not null
);

create view cases_with_states as
with latest_events as (
    select case_id, max(events.sequence_number) as latest_seq
    from unspec_cases
             join events using (case_id)
    group by case_id
)
select
       latest_events.case_id,
       events.state
from latest_events
    join events on events.case_id = latest_events.case_id
    and  events.sequence_number = latest_seq;

create table parties(
  party_id bigserial not null primary key,
  case_id bigint references cases(case_id) not null,
  data jsonb not null
);

create table case_acl(
    username varchar not null,
    case_id bigint not null references cases(case_id)
);

create table citizen(
    case_id bigint not null references cases(case_id),
    title varchar,
    forename varchar,
    surname varchar,
    date_of_birth date,
        status varchar not null
);


