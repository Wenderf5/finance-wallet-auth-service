CREATE TABLE users (
    id UUID PRIMARY KEY NOT NULL,
    user_name VARCHAR(75) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    photo_url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
