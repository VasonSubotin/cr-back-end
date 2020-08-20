drop table if exists  tmp_location;
create table tmp_location(
	ID_T INTEGER Primary key AUTOINCREMENT,
	N_ACCESS numeric(1),
	V_ADDRESS varchar(256),
	N_CONNECTOR numeric(3),
	V_ICON varchar(256),
	V_ICON_TYPE varchar(2),
	V_EXTERNAL_ID varchar(32),
	N_KILOWATTS numeric(10),
	N_LATITUDE numeric(6,6),
	N_LONGITUDE numeric(6,6),
	V_NAME varchar(32),
	NETWORK_ID INTEGER,
    OUTLETS_ID INTEGER,
    N_POWER NUMERIC(1),
    PWPS_VERSION varchar(16),
    N_SCORE NUMERIC(5,5),
    STATION_ID INTEGER,
    V_URL  varchar(128)
);
