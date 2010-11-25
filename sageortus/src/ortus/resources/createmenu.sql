drop table if exists sage.menu;
drop table if exists sage.menuitems;
drop table if exists sage.menuitemstatics;
create table sage.menu (
    menuid varchar,
    userid int,
    menuname varchar);
create table sage.menuitems (
    menuid varchar,
    menuitemid varchar,
    title varchar,
    position int,
    xpos int,
    ypos int,
    action varchar,
    menutype varchar);
create table sage.menuitemstatics (
    menuid varchar,
    menuitemid varchar,
    name varchar,
    value varchar);
