insert into users (id, email, name) values (1, 'test1@mail.ru', 'test1Name');
insert into users (id, email, name) values (2, 'test2@mail.ru', 'test2Name');
insert into users (id, email, name) values (3, 'test3@mail.ru', 'test3Name');
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;