CREATE TABLE IF NOT EXISTS schema_saga.saga_instance (
    saga_id VARCHAR(36) PRIMARY KEY,
    saga_type VARCHAR(80) NOT NULL,
    status VARCHAR(40) NOT NULL,
    current_step VARCHAR(80),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS schema_saga.saga_step (
    id BIGSERIAL PRIMARY KEY,
    saga_id VARCHAR(36) NOT NULL,
    step_order INTEGER NOT NULL,
    step_name VARCHAR(80) NOT NULL,
    status VARCHAR(40) NOT NULL,
    command_type VARCHAR(120),
    event_type VARCHAR(120),
    payload JSONB,
    response_payload JSONB,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_saga_step_instance
        FOREIGN KEY (saga_id)
        REFERENCES schema_saga.saga_instance(saga_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_saga_step_saga_id
    ON schema_saga.saga_step(saga_id);

CREATE INDEX IF NOT EXISTS idx_saga_instance_status
    ON schema_saga.saga_instance(status);