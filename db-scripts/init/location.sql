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



drop table if exists  tmp_location_price;
create table tmp_location_price(
    ID_T INTEGER Primary key AUTOINCREMENT,
    	N_ACCESS numeric(1),
    	V_ADDRESS varchar(256),
    	N_AMPS numeric(3),
        N_CONFIDENCE NUMERIC(2),
        V_COST varchar(8),
        V_COST_DESCRIPTION varchar(128),
        V_CREATED_AT varchar(16),
        V_CUSTOM_PORTS varchar(32),
        V_DESCRIPTION varchar(256),
        V_ENABLED  varchar(8),
        V_FORMATED_PHONE_NUMBER varchar(16),
        V_HOURS varchar(32),
        V_ICON_TYPE varchar(2),
        V_EXTERNAL_ID varchar(32),
        N_KILOWATTS numeric(10),
        N_LATITUDE numeric(6,6),
        V_LOCALE varchar(32),
        V_LOCATION_ID varchar(32),
        V_LOCKED  varchar(8),
        N_LONGITUDE numeric(6,6),
        V_MANUFACTURER varchar(32),
        V_MODEL varchar(32),
        V_NAME varchar(32),
        NETWORK_ID INTEGER,
        V_NISSAN_NCTC varchar(8),
        V_OCPI_IDS varchar(64),
        V_OCPI_VERSION varchar(16),
        V_OPEN247 varchar(8),
        V_OPENING_DATE varchar(16),
        V_PARCKING_TYPE_NAME  varchar(16),
        V_PAYMENT_ENABLED varchar(8),
        V_PHONE varchar(16),
        V_POI_NAME varchar(64),
        V_PRE_CHARGE_INSTRUCTIONS varchar(64),
        V_PROMOS varchar(64),
        PWPS_VERSION varchar(16),
        V_QR_ENABLED varchar(8),
        V_REQUIRE_ACCESS_CARD varchar(8),
        V_URL varchar(128),
        N_VOLTS numeric(4)
)
