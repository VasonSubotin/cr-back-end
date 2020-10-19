insert into Accounts(ID_ACCOUNT,V_LOGIN, V_FIRST_NAME, V_LAST_NAME, V_EMAIL,DT_CREATED )
values (1, 'testLogin','test','userLast','mytest@mail.com',DateTime('now'));

insert into Accounts(ID_ACCOUNT,V_LOGIN, V_FIRST_NAME, V_LAST_NAME, V_EMAIL,DT_CREATED )
values (2, 'testLogin2','test2','userLast2','mytest2@mail.com',DateTime('now'));

insert into Schedules(ACCOUNT_ID,LOCATION_ID, RESOURCE_ID, SESSION_ID, POLICY_ID,DT_START, DT_STOP,F_CARBON_IMPACT,F_CARBON_SAVINGS,F_FINANCE_SAVINGS,DT_CREATED,BB_DATA)
values (1, 2,33,1,1,DateTime('%s','2020-07-15T07:12:35'),DateTime('%s','2020-07-15T17:32:55'),20,10,100.2,DateTime('%s','now'),'[{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T12:12:35.000Z"},{"duration":1800000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T01:40:00.000Z"},{"duration":1200000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T03:32:15.000Z"},{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T04:50:35.000Z"},{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T06:12:35.000Z"}]');

-- sample for random price location
 insert into Locations(
     F_PRICE,
     N_POWER,
     V_NAME,
     V_DESCRIPTION,
     N_LATITUDE,
     N_LONGITUDE,
     V_TIME_ZONE,
     DT_CREATED,
     V_EXTERNAL_LOCATION_ID,
     B_TOU_ENABLED,
     B_DELETED)
select  CAST((random()%25+25)*.01 as NUMERIC(6,6)),
    N_KILOWATTS*1000,
     V_NAME,
     V_ADDRESS,
     N_LATITUDE,
     N_LONGITUDE,
     'UTC-7',
     strftime('%s', 'now') * 1000,
     'NP15',
     1,
     0
 from tmp_location_price where
 (37.7984792 -0.0674491204439)<=N_LATITUDE
 and N_LATITUDE <=(37.7984792 + 0.0674491204439)
 and  (-122.4118597 -0.08536012648276)<=N_LONGITUDE
 and N_LONGITUDE<=(-122.4118597 +0.08536012648276);


-- test data for driving schedule
-- 1320 5th St  Santa Monica, CA 90401
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.33,25000,'Public Parking Lot 28','1334 5th St, Santa Monica CA 90401, United States',34.017538, -118.495303,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.31,20000,'429 Santa Monica','429 Santa Monica Blvd, Santa Monica, CA 90401',34.017248, -118.494929,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.27,7200,'Santa Monica Public Library','601 Santa Monica Blvd, Santa Monica, CA 90401',34.019063, -118.493001,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.27,8000,'Toyota Santa Monica','801 Santa Monica Blvd, Santa Monica, CA 90401',34.019749, -118.491397,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.30,16000,'Proper Hotel Santa Monica','700 Wilshire Blvd, Santa Monica, CA 90401, USA',34.021088, -118.494537,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.30,16000,'520 Broadway / WeWork','520 Broadway, Santa Monica CA 90401, United States',34.016349, -118.491601,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


-- 1875 Century Park E Suite 100  Los Angeles, CA 90067
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.30,6600,'Watt Plaza Office of the Building','1875 Century Park E, Los Angeles CA 90067',34.061414, -118.414807,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.28,8000,'Lathrop & Gage','1888 Century Park East, Los Angeles, CA, United States',34.061068, -118.413805,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.27,6600,'1900 Avenue of the Stars','1900 Avenue of the Stars, Los Angeles CA 90067, United States',34.060446, -118.416487,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.28,7200,'10100 Santa Monica Office','10100 Santa Monica Blvd',34.061758, -118.416436,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.48,7200,'1800 Century Park East','1800 Century Park East, Los Angeles, CA, 90067',34.062798, -118.415057,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.49,8400,'Parking Concepts','1840 Century Park East, Los Angeles, CA 90067',34.062991, -118.413577,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


--- 1601 Vine St 5th floor, Los Angeles, CA 90028
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.28,7200,'Modern Parking','1627 Vine St, Los Angeles, CA 90028, USA',34.100590, -118.327479,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.3,50000,'LADOT Lot 702','1627 Vine St, Los Angeles, CA 90028, USA',34.100590, -118.327479,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.25,7200,'Trader Joe''s & 1600 Vine Parking Garage','1600 Vine St, Los Angeles, CA, 90028',34.100225, -118.325554,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.40,16000,'W Hollywood Hotel and Residences','6250 Hollywood Blvd., Los Angeles, ca 90028',34.100991, -118.325575,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.3,7200,'El Centro Garage','6200 Hollywood Blvd, Los Angeles, CA 90028, USA',34.101347, -118.324551,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.35,7200,'Cahuenga boul. Parking/Charging','1635 Cahuenga boul., Los Angeles, CA, 90028 USA',34.100888, -118.330277,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.32,7200,'N Cahuenga Blvd Parking/Charging','1520 N Cahuenga Blvd, Los Angeles, CA 90028, USA',34.099252, -118.328972,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


-- 401 S Hope St, Los Angeles, CA 90071
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.31,7200,'Bank of America Plaza','333 S. Hope St., Los Angeles, CA, 90071',34.052955, -118.253885,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.32,8000,'The Westin Bonaventure Hotel & Suites','404 S Figueroa St, Los Angeles, California 90071 United States',34.052955, -118.255718,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.15,7200,'City National Plaza','400 S Flower St, Los Angeles, California 90071, United States',34.052163, -118.254712,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.35,8000,'Wells Fargo Center','333 S Grand Ave Los Angeles, CA 90071',34.052463, -118.252153,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.35,8000,'Hope St. Parking/Charging','400 S Hope St, Los Angeles, CA 90071, United States',34.051867, -118.253688,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.38,7200,'Grand Promenade','255 S Grand Ave, Los Angeles, California, United States, 90012',34.053575, -118.251358,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


--665 W Jefferson Blvd Los Angeles, CA 90007
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.16,7200,'University of Southern California - PSD','649 W 34th St, Los Angeles, CA 90089',34.021990, -118.281874,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.10,7200,'Jefferson Blvd. / Figueroa St.','615 W Jefferson Blvd Los Angeles, California 90007',34.022754, -118.281118,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.25,7200,'University of Southern California - Shrine Parking Structure','645 W Jefferson Blvd, Los Angeles CA 90007, United States',34.022775, -118.281071,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.29,7200,'Shrine Auditorium Parking Structure','3208 Royal St, Los Angeles, CA 90007, USA',34.024057, -118.281984,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.20,7200,'USC Village Parking Garage','3131 S Hoover St, Los Angeles, CA 90007',34.025904, -118.284902,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


-- 565 Pier Ave, Hermosa Beach, CA 90254
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.10,7200,'Hermosa Beach City Hall','1315 Valley Dr, Hermosa Beach, California 90254, United States',33.863580, -118.395537,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.14,7200,'Becker Surfboards','301 Pier Ave, Hermosa Beach, California 90254, United States',33.864232, -118.398127,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.18,35000,'Hermosa Beach Community Center','710 Pier Ave, Hermosa Beach, CA 90254',33.863948, -118.394321,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.20,7680,'Plaza Hermosa','715 Pier Ave Hermosa Beach, CA 90254',33.865960, -118.395379,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);
insert into Locations(F_PRICE, N_POWER,V_NAME,V_DESCRIPTION,N_LATITUDE, N_LONGITUDE,V_TIME_ZONE,DT_CREATED, V_EXTERNAL_LOCATION_ID,B_TOU_ENABLED,B_DELETED)
values (0.19,50000,'Hermosa Beach Supercharger','710 16th St., Hermosa Beach, CA, United States, 90254',33.865995, -118.395285,'UTC-7',strftime('%s', 'now') * 1000,'NP15',1,0);


insert into Locations(
     F_PRICE,
     N_POWER,
     V_NAME,
     V_DESCRIPTION,
     N_LATITUDE,
     N_LONGITUDE,
     V_TIME_ZONE,
     DT_CREATED,
     V_EXTERNAL_LOCATION_ID,
     B_TOU_ENABLED,
     B_DELETED)
select  cost_price as NUMERIC(6,6)),
    kilowatts*1000,
     name,
     address,
     latitude,
     longitude,
     'UTC-7',
     strftime('%s', 'now') * 1000,
     'NP15',
     1,
     0
 from mytable


insert into Locations(
     F_PRICE,
     N_POWER,
     V_NAME,
     V_DESCRIPTION,
     N_LATITUDE,
     N_LONGITUDE,
     V_TIME_ZONE,
     DT_CREATED,
     V_EXTERNAL_LOCATION_ID,
     B_TOU_ENABLED,
     B_DELETED)
select  cost_price as NUMERIC(6,6)),
    kilowatts*1000,
     name,
     address,
     latitude,
     longitude,
     'UTC-7',
     strftime('%s', 'now') * 1000,
     'NP15',
     1,
     0
 from mytable


insert into Locations(
     F_PRICE,
     N_POWER,
     V_NAME,
     V_DESCRIPTION,
     N_LATITUDE,
     N_LONGITUDE,
     V_TIME_ZONE,
     DT_CREATED,
     V_EXTERNAL_LOCATION_ID,
     B_TOU_ENABLED,
     B_DELETED)
select  cost_price as NUMERIC(6,6)),
    kilowatts*1000,
     name,
     address,
     latitude,
     longitude,
     'UTC-7',
     strftime('%s', 'now') * 1000,
     'NP15',
     1,
     0
 from mytable