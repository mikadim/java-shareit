insert into bookings (id, start_date, end_date, item_id, user_id, status) values (1, timestampadd('day', -1, now()), timestampadd('day', 1, now()), 1, 2, 'APPROVED');
insert into bookings (id, start_date, end_date, item_id, user_id, status) values (2, timestampadd('day', 1, now()), timestampadd('day', 2, now()), 1, 2, 'APPROVED');
insert into bookings (id, start_date, end_date, item_id, user_id, status) values (3, timestampadd('day', 2, now()), timestampadd('day', 3, now()), 1, 2, 'APPROVED');
insert into bookings (id, start_date, end_date, item_id, user_id, status) values (4, timestampadd('day', -1, now()), timestampadd('day', 1, now()), 2, 2, 'APPROVED');
ALTER TABLE users ALTER COLUMN id RESTART WITH 5;