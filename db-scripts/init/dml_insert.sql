
delete from Policies;
insert into Policies(ID_POLICY,V_NAME,V_DESCRIPTION,DT_CREATED,B_DELETED)
    values (1,'ECO','This policy has priority to minimize CO2 impact',strftime('%s', 'now') * 1000,0);
insert into Policies(ID_POLICY,V_NAME,V_DESCRIPTION,DT_CREATED,B_DELETED)
    values (2,'ECO_PRICE','This policy has priority to minimize CO2 impact and price',strftime('%s', 'now') * 1000,0);
insert into Policies(ID_POLICY,V_NAME,V_DESCRIPTION,DT_CREATED,B_DELETED)
    values (3,'SIMPLE','This policy just have any priority',strftime('%s', 'now') * 1000,0);
insert into Policies(ID_POLICY,V_NAME,V_DESCRIPTION,DT_CREATED,B_DELETED)
    values (4,'PRICE','This policy should minimize price only',strftime('%s', 'now') * 1000,0);


delete from ResourceTypes;
insert into ResourceTypes(ID_RESOURCE_TYPE,V_NAME,V_DESCRIPTION)
    values (1,'ElectricCar','Represents any type of electric car');
insert into ResourceTypes(ID_RESOURCE_TYPE,V_NAME,V_DESCRIPTION)
    values (2,'HybridCar','Represents any type of hybrid car');
insert into ResourceTypes(ID_RESOURCE_TYPE,V_NAME,V_DESCRIPTION)
    values (3, 'Quadrocopter','Represents quadrocopters');


delete from EventTypes;
insert into EventTypes(ID_EVENT_TYPE, V_NAME,V_DESCRIPTION)
    values (1, 'PlugIn','Represents the case when resource has been connected from a charge station');
insert into EventTypes(ID_EVENT_TYPE,V_NAME,V_DESCRIPTION)
    values (2, 'PlugOut','Represents the case when resource has been disconnected from a charge station');
insert into EventTypes(ID_EVENT_TYPE,V_NAME,V_DESCRIPTION)
    values (3, 'ChargingStart','Represents the case when resource start to charge');
insert into EventTypes(ID_EVENT_TYPE,V_NAME,V_DESCRIPTION)
    values (4, 'ChargingStop','Represents the case when resource stop to charge');


delete from SessionTypes;
insert into SessionTypes(ID_SESSION_TYPE, V_NAME,V_DESCRIPTION)
    values (1, 'Charge','Represents charging session');
insert into SessionTypes(ID_SESSION_TYPE,V_NAME,V_DESCRIPTION)
    values (2, 'NonCharge','Represents Non-charging session');


