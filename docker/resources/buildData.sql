drop table if exists category;
create table category (
	id              integer not null auto_increment,
	description     varchar(35) not null,
	constraint category_pk primary key (id)
)
engine=innodb
default charset=latin1
collate=latin1_bin;


drop table if exists classes;
create table classes (
	id              integer      not null auto_increment,
	description     varchar(35)  not null,
	reorderpoint    integer      not null,
    constraint classes_pk primary key (id)
)
engine=innodb
default charset=latin1
collate=latin1_bin;



drop table if exists users;
create table users (
	id              integer      not null auto_increment,
	login           varchar(50)  not null,
	firstname       varchar(50)  not null,
	lastname        varchar(50)  not null,
	password        varchar(255) not null,
    email           varchar(100) not null,
	constraint users_pk primary key (id)
)
engine=innodb
default charset=latin1
collate=latin1_bin;

drop table if exists material;
create table material (
	id              integer      not null auto_increment,
	classid         integer      not null,
	categoryid      integer      not null,
	descript        varchar(50)  not null,
	barcode         varchar(50)  not null,
    code            integer      not null,
    minimum         integer      not null,
    buy_price       integer      not null,
    sell_price      integer      not null,
	constraint material_pk primary key (id)
)
engine=innodb
default charset=latin1
collate=latin1_bin;



drop table if exists provider;
create table provider (
	id              integer      not null auto_increment,
	name            varchar(50)  not null,
	constraint provider_pk primary key (id)
)
engine=innodb
default charset=latin1
collate=latin1_bin;

insert into permission values (0,'Admin');
insert into permission values (1,'Gategory');
insert into permission values (2,'Classes');
insert into permission values (3,'Provider');
insert into permission values (4,'Material');



