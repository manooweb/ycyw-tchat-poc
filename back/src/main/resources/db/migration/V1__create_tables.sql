-- Schéma du tchat : 3 tables en troisième forme normale (3NF).
-- Chaque information n'est stockée qu'à un endroit : le nom d'un auteur est dans app_user,
-- un message ne garde que l'identifiant de son auteur.

-- Personnes qui utilisent le tchat : un client ou un agent du service client.
-- Pas d'e-mail ni de mot de passe : la PoC ne gère pas l'authentification.
CREATE TABLE app_user (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    CONSTRAINT ck_app_user_role CHECK (role IN ('CLIENT', 'AGENT'))
);

-- Une conversation est ouverte par un client ; tous les agents peuvent y répondre.
CREATE TABLE conversation (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id  BIGINT      NOT NULL REFERENCES app_user (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Messages d'une conversation, écrits par le client ou par un agent.
CREATE TABLE message (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conversation_id BIGINT        NOT NULL REFERENCES conversation (id),
    author_id       BIGINT        NOT NULL REFERENCES app_user (id),
    content         VARCHAR(2000) NOT NULL,
    sent_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_message_content_not_blank CHECK (char_length(btrim(content)) > 0)
);

-- Accélère le chargement de l'historique d'une conversation.
-- PostgreSQL n'indexe pas automatiquement les clés étrangères.
CREATE INDEX ix_message_conversation_id ON message (conversation_id);
