CREATE TABLE SHARES
(
   NAME varchar2(64) primary key NOT NULL,
   OWNER varchar2(96) NOT NULL,
   VALID char(1) NOT NULL,
   LABEL varchar2(96)
);
CREATE UNIQUE INDEX SHARENAME_UNIQUE_IDX ON SHARES
(
  NAME
);

CREATE TABLE SHARE_PREFERENCES (
	sharekey constraint fk_primary_id references shares(name),
	preference_type varchar2(64) not null,
	preference_key varchar2(64) not null,
	preference_value varchar2(256) not null
);
CREATE TABLE OPT_OUT (
	account varchar2(64) not null,
	val char(1) not null,
	effective date not null
);