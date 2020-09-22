create table cases(
  case_id bigserial not null primary key,
  description varchar
);

create table events(
  case_id bigint references cases(case_id) not null,
  id varchar not null,
  sequence_number integer not null,
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

create table citizen(
--     id serial not null primary key,
    case_id bigint not null references cases(case_id),
    title varchar,
    forename varchar,
    surname varchar,
    date_of_birth date
);


