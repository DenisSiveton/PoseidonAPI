insert into bid_list(Id, account, type, bid_quantity) VALUES (1, 'acc_1', 'type_1', 8.3);
insert into bid_list(Id, account, type, bid_quantity) VALUES (2, 'acc_2', 'type_2', 12.0);
insert into bid_list(Id, account, type, bid_quantity) VALUES (3, 'acc_1', 'type_2', 5.5);

insert into rule_name(Id, name, description, template) VALUES (1, 'name 1', 'description 1', 'template 1');
insert into rule_name(Id, name, description, template) VALUES (2, 'name 2', 'description 2', 'template 2');
insert into rule_name(Id, name, description, template) VALUES (3, 'name 3', 'description 3', 'template 3');

insert into Users(fullname, username, password, role) VALUES ('User Test', 'Usertest', '$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO', 'USER');