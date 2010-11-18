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

-- Manufacturers
INSERT INTO manufacturer VALUES (NULL, "康师傅控股有限公司", "masterkong.png");
INSERT INTO manufacturer VALUES (NULL, "统一企业(中国);投资有限公司", "uni-president.png");

-- Brands
INSERT INTO brand VALUES (NULL, 1, NULL, "红烧牛肉", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "香辣牛肉", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "麻辣牛肉", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "麻辣排骨", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "辣旋风", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "海陆鲜汇", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "亚洲精选", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "酱香传奇", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "东北炖", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "油泼辣子", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "酸香世家", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "江南美食", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "本帮烧", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "山珍海烩", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "老火靓汤", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "千椒百味", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "蒸行家", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "油辣子传奇", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "陈泡风云", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "面霸", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "干拌面", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "食面八方", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "好滋味", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "劲爽拉面", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "点心面", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "金牌福满多", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "超级福满多", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "福香脆", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "福满多", NULL);
INSERT INTO brand VALUES (NULL, 1, NULL, "一碗香", NULL);

-- Steps

-- Noodles
INSERT INTO noodles VALUES (NULL, 1, "红烧牛肉面", NULL, NULL, NULL, NULL, NULL, NULL, 180, NULL, NULL);
INSERT INTO noodles VALUES (NULL, 8, "辣酱面", 118, 90, NULL, NULL, NULL, NULL, 180, "严选优质食材；精心料理好酱；正宗辣酱美味", NULL);

-- Barcodes
INSERT INTO barcode VALUES (1, 6903252049343, 2);
