# --- Sample dataset

# --- !Ups

insert into company (id,name) values (  1,'Apple Inc.');
insert into company (id,name) values (  2,'Thinking Machines');
insert into company (id,name) values (  3,'RCA');
insert into company (id,name) values (  4,'Netronics');
insert into company (id,name) values (  5,'Tandy Corporation');
insert into company (id,name) values (  6,'Commodore International');
insert into company (id,name) values (  7,'MOS Technology');
insert into company (id,name) values (  8,'Micro Instrumentation and Telemetry Systems');
insert into company (id,name) values (  9,'IMS Associates, Inc.');
insert into company (id,name) values ( 10,'Digital Equipment Corporation');
insert into company (id,name) values ( 11,'Lincoln Laboratory');
insert into company (id,name) values ( 12,'Moore School of Electrical Engineering');
insert into company (id,name) values ( 13,'IBM');
insert into company (id,name) values ( 14,'Amiga Corporation');
insert into company (id,name) values ( 15,'Canon');
insert into company (id,name) values ( 16,'Nokia');
insert into company (id,name) values ( 17,'Sony');
insert into company (id,name) values ( 18,'OQO');
insert into company (id,name) values ( 19,'NeXT');    
insert into company (id,name) values ( 20,'Atari');
insert into company (id,name) values ( 22,'Acorn person');
insert into company (id,name) values ( 23,'Timex Sinclair');
insert into company (id,name) values ( 24,'Nintendo');
insert into company (id,name) values ( 25,'Sinclair Research Ltd');
insert into company (id,name) values ( 26,'Xerox');
insert into company (id,name) values ( 27,'Hewlett-Packard');
insert into company (id,name) values ( 28,'Zemmix');
insert into company (id,name) values ( 29,'ACVS');
insert into company (id,name) values ( 30,'Sanyo');
insert into company (id,name) values ( 31,'Cray');
insert into company (id,name) values ( 32,'Evans & Sutherland');    
insert into company (id,name) values ( 33,'E.S.R. Inc.');
insert into company (id,name) values ( 34,'OMRON');
insert into company (id,name) values ( 35,'BBN Technologies');
insert into company (id,name) values ( 36,'Lenovo Group');
insert into company (id,name) values ( 37,'ASUS');
insert into company (id,name) values ( 38,'Amstrad');
insert into company (id,name) values ( 39,'Sun Microsystems');
insert into company (id,name) values ( 40,'Texas Instruments');
insert into company (id,name) values ( 41,'HTC Corporation');
insert into company (id,name) values ( 42,'Research In Motion');
insert into company (id,name) values ( 43,'Samsung Electronics');

insert into person (id,name,email,tel,company_id) values (  1,'Jonny Cavell','jonny.cavell@gmail.com','123123123123',1);
insert into person (id,name,email,tel,company_id) values (  2,'Bobby Davro','bobby.davro@gmail.com','9999999999',5);
insert into person (id,name,email,tel,company_id) values (  3,'Charlie Weasley','charlie.weasley@gmail.com','88888888888888',10);


# --- !Downs

delete from person;
delete from company;
