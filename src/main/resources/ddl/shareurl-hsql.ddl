create table shares (
	name varchar(64) not null,
	owner varchar(64) not null,
	valid char(1) not null, 
	label varchar(96),
	CONSTRAINT sharekey_unq UNIQUE(name),
	CHECK (valid in ('Y','N'))
);

create table share_preferences (
	sharekey varchar(64) not null,
	preference_type varchar (64) not null,
	preference_key varchar (64) not null,
	preference_value varchar (256) not null,
	CONSTRAINT fk2 FOREIGN KEY (sharekey) REFERENCES shares(name)
);

create table opt_out (
	account varchar(64) not null,
	val char(1) not null,
	effective timestamp not null,
	CHECK (val in ('Y','N'))
);