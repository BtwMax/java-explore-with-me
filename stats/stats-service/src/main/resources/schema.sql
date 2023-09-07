CREATE TABLE IF NOT EXISTS stats
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR NOT NULL,
    uri VARCHAR NOT NULL,
    ip VARCHAR NOT NULL,
    date_time TIMESTAMP NOT NULL
);