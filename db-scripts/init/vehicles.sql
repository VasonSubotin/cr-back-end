drop table if exists  Vehicles;
create table Vehicles(
	ID_VEHICLE INTEGER Primary key AUTOINCREMENT,
	V_MAKER varchar(32),
	V_MODEL varchar(64),
	N_YEAR numeric(4),
	N_BATERY numeric(6),
	DT_CREATED datetime,
	B_DELETED numeric(1)
);

-- =CONCATENATE("insert into Vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values (";"'";A2;"','";B2*1000;"',";D2;",";C2;",strftime('%s', 'now') * 1000,0);")


delete from Vehicles;
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Audi','A6',2019,95000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Audi','A4',2017,95000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Audi','A3',2014,95000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Audi','e-tron',2019,95000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Buick','Verano',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Buick','LaCrosse',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Buick','Enclave',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chevrolet','Volt',2018,16000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chevrolet','Bolt',2018,60000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chevrolet','Equniox',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Dodge','Charger',2018,8000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Dodge','Challenger',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Dodge','Journey',2014,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('GMC','Yukon',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('GMC','Sierra',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('GMC','200',2010,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jeep','Compass',2018,13000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jeep','Cherokee',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jeep','Wrangler',2013,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lexus','ES',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lexus','IS',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lexus','UX300e ',2020,60000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lexus','IS',2014,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('RAM','3500',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('RAM','2500',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('RAM','1500',2014,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Volkswagen','eGolf',2016,32000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Volkswagen','Passat',2015,10000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Volkswagen','Eos',2013,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','330e',2020,7400,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','330i',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','i3',2017,30000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','i3',2016,30000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','X1',2015,10000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('BMW','X1',2011,10000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Cadillac','ATS',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Cadillac','CTS',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Cadillac','Escalade',2009,200000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chrysler','300',2017,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chrysler','Pacifica ',2018,16000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Chrysler','200',2011,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Ford','Focus',2018,23000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Ford','Fusion',2018,9000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Ford','Mustang',2016,68000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Ford','Mustang',2020,88000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jaguar','E-Pace',2020,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jaguar','I-Pace',2018,90000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jaguar','F-Pace',2019,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Jaguar','XJ',2019,90000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Model 3',2017,60000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Model X',2020,75000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Model X',2020,100000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Model S',2016,100000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Model S',2008,60000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Tesla','Roadster',2018,200000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lincoln','MKT',2015,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Lincoln','MKC',2018,0,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Land Rover','Range Rover',2013,15000,strftime('%s', 'now') * 1000,0);
insert into vehicles(v_maker, v_model, n_year, n_batery, dt_created, b_deleted)  values ('Land Rover','LR4',2018,0,strftime('%s', 'now') * 1000,0);


