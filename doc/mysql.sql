drop table if exists TOTOMI;
CREATE TABLE TOTOMI (
    tablename varchar(200),
    states varchar(20), -- SUCCEED,PROCESSING,FAILED
	CONSTRAINT pk_totomi PRIMARY KEY (tablename)
)