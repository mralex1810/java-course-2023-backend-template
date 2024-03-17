CREATE TABLE IF NOT EXISTS links
(
    id         bigint GENERATED ALWAYS AS IDENTITY,
    uri        text                     NOT NULL,
    link_updated_at timestamp           NOT NULL,
    link_checked_at timestamp           NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by text                     NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (uri)
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

