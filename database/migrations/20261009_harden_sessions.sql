-- Apply once to an existing Oracle schema before deploying the hashed session token format.
ALTER TABLE sesion MODIFY (id_sesion VARCHAR2(64 CHAR));

CREATE INDEX idx_sesion_expira_en ON sesion (expira_en);
