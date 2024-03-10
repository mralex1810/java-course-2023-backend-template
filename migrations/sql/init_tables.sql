CREATE TABLE IF NOT EXISTS links
(
    id         bigint GENERATED ALWAYS AS IDENTITY,
    uri        text                     NOT NULL,
    host       text                     NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by text                     NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (uri)
);

CREATE TABLE IF NOT EXISTS link_update_info
(
    id         bigint GENERATED ALWAYS AS IDENTITY,
    link_id    bigint                   NOT NULL REFERENCES links (id),
    updated_at timestamp                NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by text                     NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS chats
(
    id bigint NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS chats_links_settings
(
    link_id bigint NOT NULL REFERENCES links (id),
    chat_id bigint NOT NULL REFERENCES chats (id)
);

