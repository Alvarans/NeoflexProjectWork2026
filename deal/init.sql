CREATE DATABASE neoflexbd;
CREATE USER neoflexuser WITH PASSWORD 'neoflex';
CREATE SCHEMA IF NOT EXISTS neoflexbd AUTHORIZATION neoflexuser;
ALTER USER neoflexuser SET search_path = neoflexbd;
