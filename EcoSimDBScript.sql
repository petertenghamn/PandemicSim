insert into records values (1, null);

insert into iteration (fox_pop, bunny_pop, grass_pop, records_id, isDynamic) values (0, 0, 0, (select id from records where id = 1), 1); 

delete from records where id = 1;

select * from iteration;