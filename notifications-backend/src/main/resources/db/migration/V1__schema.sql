CREATE TABLE users
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50)  NOT NULL
);

CREATE TABLE user_subscriptions
(
    user_id               UUID        NOT NULL,
    subscribed_categories VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_channels
(
    user_id  UUID        NOT NULL,
    channels VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE notification_logs
(
    id            UUID PRIMARY KEY,
    user_id       UUID        NOT NULL,
    category      VARCHAR(50) NOT NULL,
    channel       VARCHAR(50) NOT NULL,
    message_body  TEXT        NOT NULL,
    status        VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at    TIMESTAMP   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_logs_created_at ON notification_logs (created_at DESC);
CREATE INDEX idx_user_subs ON user_subscriptions (user_id);