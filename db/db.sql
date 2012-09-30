create table public.device (
  id serial not null,
  name text,
  desc text,
  _status int4,
  _group int4,
  primary key (id),
  foreign key device__status_fkey (_status) references hoststatus(id),
  foreign key device__group_fkey (_group) references hostgroup(id)
);

create table public.hostgroup (
  id serial not null,
  num int4,
  name text,
  primary key (id)
);


create table public.hoststatus (
  id serial not null,
  name text,
  primary key (id)
);


create table public.service (
  id serial not null,
  name text,
  primary key (id)
);


create table public.tl_t (
  _trouble int4 not null,
  _troublelist int4 not null,
  primary key (_trouble, _troublelist)
);

create table public.trouble (
  title text,
  legend text,
  timeout text,
  date_in text,
  date_out text,
  desc text,
  id serial not null,
  _device int4,
  primary key (id),
  foreign key trouble__device_fkey (_device) references device(id)
);


create table public.trouble_service (
  _trouble int4 not null,
  _service int4 not null,
  primary key (_trouble, _service)
);

create table public.troublelist (
  id serial not null,
  name text,
  primary key (id)
);
