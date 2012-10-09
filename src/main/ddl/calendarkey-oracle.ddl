CREATE TABLE SHARES
(
   NAME varchar2(64) NOT NULL,
   OWNER varchar2(96) NOT NULL,
   VALID char(1) NOT NULL,
   LABEL varchar(96)
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