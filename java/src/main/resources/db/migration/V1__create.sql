create table cases(
  case_id bigserial primary key,
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
