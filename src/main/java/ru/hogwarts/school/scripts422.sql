create table car(
id serial primary key,
brand varchar not null,
model varchar not null,
price money
);

create table person(
id serial primary key,
name varchar not null,
age integer not null,
driver_license boolean default false,
car_id integer, foreign key(car_id) references car(id)
);