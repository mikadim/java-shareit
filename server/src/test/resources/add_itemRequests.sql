insert into requests (id, description, requestor_id, created) values (1, 'description1', 2, now());
insert into requests (id, description, requestor_id, created) values (2, 'description1', 2, timestampadd('day', 1, now()));
insert into requests (id, description, requestor_id, created) values (3, 'description1', 2, timestampadd('day', 2, now()));
insert into requests (id, description, requestor_id, created) values (4, 'description1', 2, timestampadd('day', 3, now()));
ALTER TABLE requests ALTER COLUMN id RESTART WITH 5;