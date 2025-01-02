-- users
CREATE TABLE users (
    id int primary key auto_increment,
    username varchar(255) unique not null,
    email varchar(120) unique not null,
    password varchar(255) not null,
    enabled boolean default false not null
);

-- roles
CREATE TABLE roles (
    id int primary key auto_increment,
    name varchar(50) unique not null
);

-- user_roles
CREATE TABLE user_roles (
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (role_id) references roles(id) ON DELETE CASCADE
);

-- verification tokens
CREATE TABLE verification_tokens (
    id int primary key auto_increment,
    token varchar(255) unique not null,
    user_id int not null,
    expiration_date datetime not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE
);

-- password reset
CREATE TABLE password_reset_tokens (
    id int primary key auto_increment,
    token varchar(255) unique not null,
    user_id int not null,
    expiration_date datetime not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE
);

-- oAuth2 providers
CREATE TABLE oauth2_providers (
    id int primary key auto_increment,
    user_id int not null,
    provider varchar(50) not null,
    provider_user_id varchar(255) unique not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE
);

-- refresh tokens
CREATE TABLE refresh_tokens (
    id int primary key auto_increment,
    token varchar(255) unique not null,
    user_id int not null,
    expiration_date datetime not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE
);