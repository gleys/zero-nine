create table if not exists categories
(
    category_id bigint not null
        primary key,
    name        varchar(255),
    parent_id   bigint
        constraint fk_parent_to_children
        references categories
);

create table if not exists items
(
    items_id      bigint       not null
        primary key,
    item_image    oid,
    name          varchar(255) not null,
    price         bigint       not null,
    url           varchar(255) not null,
    categories_id bigint
        constraint fk_item_to_category
        references categories
);

create table if not exists keywords
(
    keyword_id bigint not null
        primary key,
    name       varchar(255)
        constraint uk_pekgolf79aog8amsef5h7awbw
        unique
);


create table if not exists persistent_logins
(
    series    varchar(64) not null
        primary key,
    last_used timestamp   not null,
    token     varchar(64) not null,
    username  varchar(64) not null
);


create table if not exists users
(
    users_id                         bigint       not null
        primary key,
    bio                              varchar(255),
    created_at                       timestamp,
    email                            varchar(255) not null
        constraint uk_6dotkott2kjsp8vw4d0m25fb7
        unique,
    email_check_token                varchar(255),
    email_check_token_generated_at   timestamp,
    email_verified                   boolean      not null,
    name                             varchar(255) not null
        constraint uk_3g1j96g94xpk3lpxl2qbl985x
        unique,
    order_created_by_email           boolean      not null,
    order_created_by_web             boolean      not null,
    order_enrollment_result_by_email boolean      not null,
    order_enrollment_result_by_web   boolean      not null,
    order_updated_by_email           boolean      not null,
    order_updated_by_web             boolean      not null,
    password                         varchar(255) not null,
    profile_image                    oid,
    url                              varchar(255)
);

create table if not exists notifications
(
    noticiations_id bigint  not null
        primary key,
    checked         boolean not null,
    link            varchar(255),
    message         varchar(255),
    title           varchar(255),
    users_id        bigint
        constraint fk_users_notifications
        references users
);

create table if not exists orders
(
    orders_id       bigint      not null
        primary key,
    closed          boolean     not null,
    created_at      timestamp,
    description     oid,
    number_of_limit integer     not null,
    participant_num integer     not null,
    title           varchar(30) not null,
    items_id        bigint
        constraint fk_orders_to_items
        references items,
    users_id        bigint
        constraint fk_orders_to_user
        references users
);


create table if not exists comments
(
    comments_id bigint not null
        primary key,
    created_at  timestamp,
    text        varchar(255),
    orders_id   bigint
        constraint comments_to_orders
        references orders,
    parent_id   bigint
        constraint children_to_parents
        references comments,
    users_id    bigint
        constraint comments_to_users
        references users
);


create table if not exists orders_users
(
    order_orders_id bigint not null
        constraint fkawl2fs90llxasvqb0tuc1irmh
        references orders,
    users_users_id  bigint not null
        constraint fk8eb34stig4c8hx30xy5j3fbgg
        references users,
    primary key (order_orders_id, users_users_id)
);


create table if not exists users_keywords
(
    users_keywords_id bigint not null
        primary key,
    keyword_id        bigint
        constraint fk_keywords_to_users_keywords
        references keywords,
    user_id           bigint
        constraint fk_users_to_users_keywords
        references users
);
