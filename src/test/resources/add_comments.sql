insert into comments (id, text, item_id, author_id, created) values (1, 'text1', 1, 2, now());
ALTER TABLE comments ALTER COLUMN id RESTART WITH 2;