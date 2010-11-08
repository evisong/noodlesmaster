--<ScriptOptions statementTerminator=";"/>

CREATE TABLE NOODLES (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		brand_id INTEGER NOT NULL,
		name TEXT NOT NULL,
		net_weight INTEGER,
		noodles_weight INTEGER,
		step_1_id INTEGER,
		step_2_id INTEGER,
		step_3_id INTEGER,
		step_4_id INTEGER,
		soakage_time INTEGER NOT NULL,
		description TEXT,
		logo TEXT
	);

CREATE TABLE MANUFACTURER (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		name TEXT NOT NULL,
		logo TEXT
	);

CREATE TABLE STEP (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		description TEXT,
		icon TEXT
	);

CREATE TABLE BRAND (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		manufacturer_id INTEGER NOT NULL,
		parent_brand_id INTEGER,
		name TEXT NOT NULL,
		logo TEXT
	);

CREATE TABLE BARCODE (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		code TEXT NOT NULL,
		noodles_id INTEGER NOT NULL
	);