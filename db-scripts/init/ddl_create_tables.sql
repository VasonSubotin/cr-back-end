drop table if exists  Accounts;
create table Accounts(
	ID_ACCOUNT INTEGER Primary key AUTOINCREMENT,
	V_LOGIN varchar(32),
	V_PASSWORD varchar(256),
	DT_CREATED datetime,
	V_EMAIL varchar(128),
	V_FIRST_NAME varchar(64),
	V_LAST_NAME varchar(64),
	V_ACCOUNT_TYPE varchar(32),
	N_LIFE_TIME_CHARGE numeric(10),
	B_DELETED numeric(1)
);

drop table if exists ResourceTypes;
create table ResourceTypes(
	ID_RESOURCE_TYPE INTEGER Primary key AUTOINCREMENT,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Resources;
create table Resources(
	ID_RESOURCE INTEGER Primary key AUTOINCREMENT,
	V_EXTERNAL_RESOURCE_ID varchar(32),
	V_VENDOR varchar(64),
	V_MODEL varchar(64),
	POLICY_ID numeric(2),
	ACCOUNT_ID numeric(10),
	N_CHARGE_BY_TIME  numeric(6),
	RESOURCE_TYPE_ID varchar(64),
	V_GROUP_ID varchar(64),
	N_POWER numeric(6),
	N_CAPACITY numeric(6),
	DT_UPDATED datetime,
	DT_CREATED datetime,
	B_DELETED numeric(1),
	B_MOCK numeric(1),
	FOREIGN KEY (RESOURCE_TYPE_ID)  REFERENCES ResourceTypes (ID_RESOURCE_TYPE )
);

drop table if exists Policies;
create table Policies(
	ID_POLICY INTEGER Primary key AUTOINCREMENT,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64),
	DT_CREATED datetime,
	B_DELETED numeric(1)
);

drop table if exists TimeOfUsages;
create table TimeOfUsages(
	ID_TOU INTEGER Primary key AUTOINCREMENT,
	LOCATION_ID INTEGER,
	RESOURCE_ID INTEGER,
	N_START numeric(10),
	N_STOP numeric(10),
	DT_CREATED datetime,
	B_ACTIVE numeric(1),
	B_DELETED numeric(1),
	FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION ),
	FOREIGN KEY (RESOURCE_ID)  REFERENCES Locations (ID_RESOURCE )
);


drop table if exists DREvents;
create table DREvents(
	ID_DR_EVENT INTEGER Primary key AUTOINCREMENT,
	RESOURCE_ID INTEGER,
	LOCATION_ID INTEGER,
	N_START numeric(10),
	N_STOP numeric(10),
	DT_CREATED datetime,
	B_DELETED numeric(1),
	FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE ),
	FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION )
);

drop table if exists UserSessions;
create table UserSessions(
	ID_USER_SESSION INTEGER Primary key AUTOINCREMENT,
	ACCOUNT_ID INTEGER,
	V_TOKEN varchar(512),
	V_REFRESH_TOKEN varchar(512),
	RESOURCE_ID INTEGER,
    V_SESSION_TYPE varchar(32),
    N_TTL numeric(10),
	DT_CREATED datetime,
	B_CLOSED numeric(1),
	FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE ),
	FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT)
);


drop table if exists Locations;
create table Locations(
	ID_LOCATION INTEGER Primary key AUTOINCREMENT,
	ACCOUNT_ID INTEGER,
	F_PRICE numeric(6,6),
	N_POWER numeric(6),
	V_NAME varchar(32),
	V_DESCRIPTION varchar(128),
	N_LATITUDE numeric(3,7),
	N_LONGITUDE numeric(3,7),
	V_TIME_ZONE varchar(8),
	DT_CREATED datetime,
	V_EXTERNAL_LOCATION_ID varchar(32), -- will be used for moer
	B_TOU_ENABLED numeric(1),
	B_DELETED numeric(1)
);


drop table if exists SessionTypes;
create table SessionTypes(
	ID_SESSION_TYPE INTEGER Primary key AUTOINCREMENT,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Sessions;
create table Sessions(
	ID_SESSION INTEGER Primary key AUTOINCREMENT,
	ACCOUNT_ID INTEGER,
	LOCATION_ID INTEGER,
	N_LATITUDE numeric(3,7),
	N_LONGITUDE numeric(3,7),
	RESOURCE_ID INTEGER,
	DT_UPDATED datetime,
	DT_START datetime,
	DT_STOP datetime,
	N_DURATION numeric(6),
	N_ENERGY numeric(10),
	F_CARBON_IMPACT numeric(6,3),
	F_CARBON_SAVINGS numeric(6,3),
	F_FINANCE_SAVINGS numeric(6,3),
	V_STATUS varchar(8),
    SESSION_TYPE_ID INTEGER,
    B_CLOSED numeric(1),
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (SESSION_TYPE_ID)  REFERENCES SessionTypes (ID_SESSION_TYPE)
);


drop table if exists EventTypes;
create table EventTypes(
	ID_EVENT_TYPE INTEGER Primary key AUTOINCREMENT,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Events;
create table Events(
	ID_EVENT INTEGER Primary key AUTOINCREMENT,
	ACCOUNT_ID INTEGER,
	LOCATION_ID INTEGER,
	RESOURCE_ID INTEGER,
	SESSION_ID INTEGER,
	EVENT_TYPE_ID INTEGER,
	N_LATITUDE numeric(3,7),
	N_LONGITUTE numeric(3,7),
	N_CURRENT numeric(6),
	N_ENERGY numeric(10),
	N_POWER numeric(6),
	F_EXT_TEMPERATURE numeric(3,2),
	F_INT_TEMPERATURE numeric(3,2),
	F_CARBON_IMPACT numeric(6,3),
	F_FINANCE_SAVINGS numeric(6,3),
	DT_CREATED datetime,
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (EVENT_TYPE_ID)  REFERENCES EventTypes (ID_IEVENT_TYPE),
    FOREIGN KEY (SESSION_ID)  REFERENCES Sessions (ID_SESSION)
);


drop table if exists Schedules;
create table Schedules(
	ID_SCHEDULE INTEGER  Primary key AUTOINCREMENT,
	ACCOUNT_ID INTEGER,
	LOCATION_ID INTEGER,
	RESOURCE_ID INTEGER,
	SESSION_ID INTEGER,
	POLICY_ID INTEGER,
	V_SCHEDULE_TYPE varchar(8),
	DT_START datetime,
	DT_STOP datetime,
    F_CARBON_IMPACT numeric(6,3),
	F_CARBON_SAVINGS numeric(6,3),
	F_FINANCE_SAVINGS numeric(6,3),
	N_INIT_ENERGY numeric(6),
	N_CAPACITY numeric(6),
	F_END_SOC numeric(3,2),
	BB_DATA blob,
	DT_CREATED datetime,
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (POLICY_ID)  REFERENCES Policies (ID_POLICY_ID),
    FOREIGN KEY (SESSION_ID)  REFERENCES Sessions (ID_SESSION)
);

drop table if exists  MarginalOperatingEmissionsRate;
create table MarginalOperatingEmissionsRate(
	ID_MOER INTEGER Primary key AUTOINCREMENT,
	V_EXTERNAL_LOCATION_ID varchar(32),
	DT_START datetime,
	DT_STOP datetime,
	DT_CREATED datetime,
	B_DELETED numeric(1)
);

drop table if exists  SmartCarCaches;
create table SmartCarCaches(
	V_EXTERNAL_RESOURCE_ID varchar(32)  Primary key AUTOINCREMENT,
	DT_CREATED datetime,
	N_TIMING numeric(6),
	BB_DATA blob
);

