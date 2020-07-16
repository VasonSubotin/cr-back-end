insert into Accounts(ID_ACCOUNT,V_LOGIN, V_FIRST_NAME, V_LAST_NAME, V_EMAIL,DT_CREATED )
values (1, 'testLogin','test','userLast','mytest@mail.com',DateTime('now'));

insert into Accounts(ID_ACCOUNT,V_LOGIN, V_FIRST_NAME, V_LAST_NAME, V_EMAIL,DT_CREATED )
values (2, 'testLogin2','test2','userLast2','mytest2@mail.com',DateTime('now'));

insert into Schedules(ACCOUNT_ID,LOCATION_ID, RESOURCE_ID, SESSION_ID, POLICY_ID,DT_START, DT_STOP,F_CARBON_IMPACT,F_CARBON_SAVINGS,F_FINANCE_SAVINGS,DT_CREATED,BB_DATA)
values (1, 2,33,1,1,DateTime('%s','2020-07-15T07:12:35'),DateTime('%s','2020-07-15T17:32:55'),20,10,100.2,DateTime('%s','now'),'[{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T12:12:35.000Z"},{"duration":1800000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T01:40:00.000Z"},{"duration":1200000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T03:32:15.000Z"},{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T04:50:35.000Z"},{"duration":3600000,"charge_rate":1000,"primary_trigger":"test-trigger","interval_type":"test","economic_impact":2.0,"co2_impact":0.9,"starttime":"2020-07-15T06:12:35.000Z"}]');


