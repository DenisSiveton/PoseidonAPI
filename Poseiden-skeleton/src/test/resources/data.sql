insert into bid_list(Id, account, type, bid_quantity) VALUES (1, 'acc_1', 'type_1', 8.3);
insert into bid_list(Id, account, type, bid_quantity) VALUES (2, 'acc_2', 'type_2', 12.0);
insert into bid_list(Id, account, type, bid_quantity) VALUES (3, 'acc_1', 'type_2', 5.5);

insert into curve_point(Id,curve_id, term, value) VALUES (1,1,2.0,8.3);
insert into curve_point(Id,curve_id, term, value) VALUES (2,5,3.5,1.0);
insert into curve_point(Id,curve_id, term, value) VALUES (3,2,2.5,11.2);

insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (1, 'moodys 1', 'sAndP 1', 'fitch rating 1', 1);
insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (2, 'moodys 2', 'sAndP 2', 'fitch rating 2', 2);
insert into rating(Id, moodys_rating, sandPRating, fitch_rating, order_number) VALUES (3, 'moodys 3', 'sAndP 3', 'fitch rating 3', 3);

insert into rule_name(Id, name, description, template) VALUES (1, 'name 1', 'description 1', 'template 1');
insert into rule_name(Id, name, description, template) VALUES (2, 'name 2', 'description 2', 'template 2');
insert into rule_name(Id, name, description, template) VALUES (3, 'name 3', 'description 3', 'template 3');

insert into Users(fullname, username, password, role) VALUES ('User Test', 'Usertest', '$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO', 'USER');