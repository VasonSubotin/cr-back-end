drop table if exists  Accounts;
create table Accounts(
	ID_ACCOUNT numeric(10) Primary key,
	V_LOGIN varchar(32),
	DT_CREATED datetime,
	V_EMAIL varchar(128),
	V_FIRST_NAME varchar(64),
	V_LAST_NAME varchar(64),
	N_LIFE_TIME_CHARGE numeric(10),
	B_DELETED numeric(1)
);

drop table if exists ResourceTypes;
create table ResourceTypes(
	ID_RESOURCE_TYPE numeric(3) Primary key,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Resources;
create table Resources(
	ID_RESOURCE numeric(10) Primary key,
	EXTERNAL_RESOURCE_ID varchar(32),
	V_VENDOR varchar(64),
	V_MODEL varchar(64),
	RESOURCE_TYPE_ID varchar(64),
	N_CURRENT_RATING numeric(6),
	N_BATTERY_SIZE numeric(6),
	DT_UPDATED datetime,
	DT_CREATED datetime,
	B_DELETED numeric(1),
	FOREIGN KEY (RESOURCE_TYPE_ID)  REFERENCES ResourceTypes (ID_RESOURCE_TYPE )
);

drop table if exists Policies;
create table Policies(
	ID_POLICY numeric(3) Primary key,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64),
	DT_CREATED datetime,
	B_DELETED numeric(1)
);

drop table if exists TimeOfUsages;
create table TimeOfUsages(
	ID_TOU numeric(3) Primary key,
	ACCOUNT_ID numeric(10),
	N_START numeric(10),
	N_STOP numeric(10),
	DT_CREATED datetime,
	B_DELETED numeric(1),
	FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT )
);


drop table if exists AccountResources;
create table AccountResources(
	ID_ACCOUNT_RESOURCE numeric(10) Primary key,
	RESOURCE_ID numeric(10),
	DEFAULT_POLICY_ID numeric(10),
	ACCOUNT_ID numeric(10),
	DT_CREATED datetime,
	B_DELETED numeric(1),
	FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
	FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
	FOREIGN KEY (DEFAULT_POLICY_ID)  REFERENCES Policies (ID_POLICY)
);



drop table if exists UserSessions;
create table UserSessions(
	ID_USER_SESSION numeric(10) Primary key,
	ACCOUNT_ID numeric(10),
	V_TOKEN varchar(512),
	V_REFRESH_TOKEN varchar(512),
	DT_CREATED datetime,
	B_DELETED numeric(1),
	FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT)
);


drop table if exists Locations;
create table Locations(
	ID_LOCATION numeric(10) Primary key,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(128),
	N_LATITUDE numeric(10),
	N_LONGITUTE numeric(10),
	V_TIME_ZONE varchar(8),
	DT_CREATED datetime,
	B_TOU_ENABLED numeric(1),
	B_DELETED numeric(1)
);


drop table if exists SessionTypes;
create table SessionTypes(
	ID_SESSION_TYPE numeric(2) Primary key,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Sessions;
create table Sessions(
	ID_SESSION numeric(10) Primary key,
	ACCOUNT_ID numeric(10),
	LOCATION_ID numeric(10),
	RESOURCE_ID numeric(10),
	DT_UPDATE datetime,
	DT_START datetime,
	DT_STOP datetime,
	N_DURATION numeric(6),
	N_CHARGE numeric(10),
	F_CARBON_IMPACT numeric(6,3),
	F_CARBON_SAVINGS numeric(6,3),
	F_FINANCE_SAVINGS numeric(6,3),
	V_STATUS varchar(8),
    SESSION_TYPE_ID numeric(10),
    B_CLOSED numeric(1),
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (SESSION_TYPE_ID)  REFERENCES SessionTypes (ID_SESSION_TYPE)
);


drop table if exists EventTypes;
create table EventTypes(
	ID_EVENT_TYPE numeric(2) Primary key,
	V_NAME varchar(32),
	V_DESCRIPTION varchar(64)
);

drop table if exists Events;
create table Events(
	ID_EVENT numeric(10) Primary key,
	ACCOUNT_ID numeric(10),
	LOCATION_ID numeric(10),
	RESOURCE_ID numeric(10),
	SESSION_ID numeric(10),
	IEVENT_TYPE_ID numeric(10),
	N_LATITUDE numeric(10),
	N_LONGITUTE numeric(10),
	N_CHARGE numeric(10),
	F_CARBON_IMPACT numeric(6,3),
	F_FINANCE_SAVINGS numeric(6,3),
	DT_CREATED datetime,
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (IEVENT_TYPE_ID)  REFERENCES EventTypes (ID_IEVENT_TYPE),
    FOREIGN KEY (SESSION_ID)  REFERENCES Sessions (ID_SESSION)
);


drop table if exists Schedulers;
create table Schedulers(
	ID_SCHEDULER numeric(10) Primary key,
	ACCOUNT_ID numeric(10),
	LOCATION_ID numeric(10),
	RESOURCE_ID numeric(10),
	SESSION_ID numeric(10),
	POLICY_ID numeric(10),
	DT_START datetime,
	DT_STOP datetime,
	BB_DATA blob,
	DT_CREATED datetime,
    FOREIGN KEY (ACCOUNT_ID)  REFERENCES Accounts (ID_ACCOUNT),
    FOREIGN KEY (RESOURCE_ID)  REFERENCES Resources (ID_RESOURCE),
    FOREIGN KEY (LOCATION_ID)  REFERENCES Locations (ID_LOCATION),
    FOREIGN KEY (POLICY_ID)  REFERENCES Policies (ID_POLICY_ID),
    FOREIGN KEY (SESSION_ID)  REFERENCES Sessions (ID_SESSION)
);