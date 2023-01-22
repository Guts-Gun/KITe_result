CREATE TABLE address_email
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_address_id BIGINT                NULL,
    email              VARCHAR(255)          NULL,
    is_deleted         BIT(1)                NULL,
    CONSTRAINT pk_addressemail PRIMARY KEY (id)
);

CREATE TABLE address_group
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_address_id BIGINT                NULL,
    fk_user_group_id   BIGINT                NULL,
    is_deleted         BIT(1)                NULL,
    CONSTRAINT pk_addressgroup PRIMARY KEY (id)
);

CREATE TABLE address_phone
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_address_id VARCHAR(255)          NULL,
    phone              VARCHAR(255)          NULL,
    is_deleted         BIT(1)                NULL,
    CONSTRAINT pk_addressphone PRIMARY KEY (id)
);

CREATE TABLE broker
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255)          NULL,
    ip           VARCHAR(255)          NULL,
    price        FLOAT                 NULL,
    speed        FLOAT                 NULL,
    failure_rate FLOAT                 NULL,
    is_deleted   BIT(1)                NULL,
    CONSTRAINT pk_broker PRIMARY KEY (id)
);

CREATE TABLE result_sending
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id        VARCHAR(255)          NULL,
    fk_sending_id     BIGINT                NULL,
    sending_type      INT                   NULL,
    sending_rule_type INT                   NULL,
    success           BIT(1)                NULL,
    total_message     BIGINT                NULL,
    failed_message    BIGINT                NULL,
    avg_speed         FLOAT                 NULL,
    request_time      BIGINT                NULL,
    input_time        BIGINT                NULL,
    schedule_time     BIGINT                NULL,
    start_time        BIGINT                NULL,
    complete_time     BIGINT                NULL,
    log_time          BIGINT                NULL,
    is_deleted        BIT(1)                NULL,
    CONSTRAINT pk_resultsending PRIMARY KEY (id)
);

CREATE TABLE result_tx
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id           VARCHAR(255)          NULL,
    fk_result_sending_id BIGINT                NULL,
    fk_tx_id             BIGINT                NULL,
    fk_broker_id         BIGINT                NULL,
    fk_sending_x_id      BIGINT                NULL,
    sending_type         INT                   NULL,
    sender               VARCHAR(255)          NULL,
    receiver             VARCHAR(255)          NULL,
    success              BIT(1)                NULL,
    fail_reason          INT                   NULL,
    title                VARCHAR(255)          NULL,
    media_link           VARCHAR(255)          NULL,
    content              VARCHAR(255)          NULL,
    input_time           BIGINT                NULL,
    schedule_time        BIGINT                NULL,
    start_time           BIGINT                NULL,
    send_time            BIGINT                NULL,
    complete_time        BIGINT                NULL,
    log_time             BIGINT                NULL,
    is_deleted           BIT(1)                NULL,
    CONSTRAINT pk_resulttx PRIMARY KEY (id)
);

CREATE TABLE result_tx_failure
(
    id                   BIGINT       NOT NULL,
    fk_user_id           VARCHAR(255) NULL,
    fk_result_sending_id BIGINT       NULL,
    fk_tx_id             BIGINT       NULL,
    fk_broker_id         BIGINT       NULL,
    fk_sending_x_id      BIGINT       NULL,
    sending_type         INT          NULL,
    fail_reason          INT          NULL,
    title                VARCHAR(255) NULL,
    media_link           VARCHAR(255) NULL,
    content              VARCHAR(255) NULL,
    is_deleted           BIT(1)       NULL,
    CONSTRAINT pk_resulttxfailure PRIMARY KEY (id)
);

CREATE TABLE sending
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id        VARCHAR(255)          NULL,
    sending_rule_type INT                   NULL,
    sending_type      INT                   NULL,
    total_message     BIGINT                NULL,
    input_time        BIGINT                NULL,
    schedule_time     BIGINT                NULL,
    title             VARCHAR(255)          NULL,
    media_link        VARCHAR(255)          NULL,
    content           VARCHAR(255)          NULL,
    is_deleted        BIT(1)                NULL,
    CONSTRAINT pk_sending PRIMARY KEY (id)
);

CREATE TABLE sending_block
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    sender     VARCHAR(255)          NULL,
    receiver   VARCHAR(255)          NULL,
    block_time BIGINT                NULL,
    is_deleted BIT(1)                NULL,
    CONSTRAINT pk_sendingblock PRIMARY KEY (id)
);

CREATE TABLE sending_email
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    fk_sending_id BIGINT                NULL,
    sender        VARCHAR(255)          NULL,
    receiver      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NULL,
    var1          VARCHAR(255)          NULL,
    var2          VARCHAR(255)          NULL,
    var3          VARCHAR(255)          NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sendingemail PRIMARY KEY (id)
);

CREATE TABLE sending_msg
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    fk_sending_id BIGINT                NULL,
    sender        VARCHAR(255)          NULL,
    receiver      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NULL,
    var1          VARCHAR(255)          NULL,
    var2          VARCHAR(255)          NULL,
    var3          VARCHAR(255)          NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sending_msg PRIMARY KEY (id)
);

CREATE TABLE sending_rule
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id    VARCHAR(255)          NULL,
    fk_sending_id BIGINT                NULL,
    fk_broker_id  BIGINT                NULL,
    weight        BIGINT                NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sending_rule PRIMARY KEY (id)
);

CREATE TABLE sending_schedule
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    fk_sending_id BIGINT                NULL,
    time          BIGINT                NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sending_schedule PRIMARY KEY (id)
);

CREATE TABLE user
(
    id         VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NULL,
    email      VARCHAR(255) NULL,
    is_deleted BIT(1)       NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_address
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id VARCHAR(255)          NULL,
    name       VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    CONSTRAINT pk_user_address PRIMARY KEY (id)
);

CREATE TABLE user_email
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id VARCHAR(255)          NULL,
    email      VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    CONSTRAINT pk_user_email PRIMARY KEY (id)
);

CREATE TABLE user_group
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id VARCHAR(255)          NULL,
    group_name VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    CONSTRAINT pk_user_group PRIMARY KEY (id)
);

CREATE TABLE user_phone
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    fk_user_id VARCHAR(255)          NULL,
    phone      VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    CONSTRAINT pk_user_phone PRIMARY KEY (id)
);