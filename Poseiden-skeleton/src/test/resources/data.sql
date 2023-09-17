insert into bid_list(Id, account, type, bid_quantity) VALUES (1, 'acc_1', 'type_1', 8.3);
insert into bid_list(Id, account, type, bid_quantity) VALUES (2, 'acc_2', 'type_2', 12.0);
insert into bid_list(Id, account, type, bid_quantity) VALUES (3, 'acc_1', 'type_2', 5.5);

insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (1, 'moddys 1', 'sAndP 1', 'fitch rating 1', 1);
insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (2, 'moddys 2', 'sAndP 2', 'fitch rating 2', 2);
insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (3, 'moddys 3', 'sAndP 3', 'fitch rating 3', 3);

insert into Users(fullname, username, password, role) VALUES ('User Test', 'Usertest', '$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO', 'USER');