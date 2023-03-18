insert into items (id, name, description, available, user_id) values (1, 'test1Item', 'description1', true, 1);
insert into items (id, name, description, available, user_id) values (2, 'test2Item', 'description2', true, 1);
ALTER TABLE items ALTER COLUMN id RESTART WITH 3;