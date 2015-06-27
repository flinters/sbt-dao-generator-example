create table DEPT (
  DEPT_ID integer not null primary key,
  DEPT_NAME varchar(20) not null,
  VERSION_NO integer not null
);

create table EMP (
  EMP_ID integer not null primary key,
  DEPT_ID integer not null,
  EMP_NAME varchar(20) not null,
  HIREDATE date,
  SALARY numeric(7,2),
  VERSION_NO integer not null,
  FOREIGN KEY (DEPT_ID) REFERENCES DEPT(DEPT_ID),
);

insert into DEPT values(1, '技術部', 1);
insert into DEPT values(2, '総務部', 1);

insert into EMP values(1, 1, '山田太郎', '1980-12-17', 800, 1);
insert into EMP values(2, 2, '山田花子', '1981-02-20', 1600, 1);
