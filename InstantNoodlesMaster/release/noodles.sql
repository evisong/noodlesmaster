--<ScriptOptions statementTerminator=";"/>
	
CREATE TABLE MANUFACTURER (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		uuid TEXT NOT NULL,
		name TEXT NOT NULL,
		logo TEXT
	);
	
CREATE UNIQUE INDEX manufacturer_uuid_idx ON MANUFACTURER (uuid);

CREATE TABLE BRAND (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		uuid TEXT NOT NULL,
		manufacturer_id INTEGER NOT NULL,
		parent_brand_id INTEGER,
		name TEXT NOT NULL,
		logo TEXT
	);
	
CREATE UNIQUE INDEX brand_uuid_idx ON BRAND (uuid);

CREATE TABLE NOODLES (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		uuid TEXT NOT NULL,
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

CREATE UNIQUE INDEX noodles_uuid_idx ON NOODLES (uuid);

CREATE TABLE STEP (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		uuid TEXT NOT NULL,
		description TEXT,
		icon TEXT
	);

CREATE UNIQUE INDEX step_uuid_idx ON STEP (uuid);

CREATE TABLE BARCODE (
		_id INTEGER PRIMARY KEY AUTOINCREMENT,
		code TEXT NOT NULL,
		noodles_id INTEGER NOT NULL
	);

-- Manufacturers
INSERT INTO manufacturer VALUES (NULL, "a12e6233-d181-4d7f-bf20-1471252447f1", "康师傅控股有限公司", "masterkong.png");
INSERT INTO manufacturer VALUES (NULL, "a2ba56ec-afad-401c-9706-3de35eb7d697", "统一企业(中国);投资有限公司", "uni-president.png");

-- Brands
INSERT INTO brand VALUES (NULL, "43e62c19-72c1-481f-a04c-93fb53df8f13", 1, NULL, "红烧牛肉", NULL);
INSERT INTO brand VALUES (NULL, "db99a17f-a630-48c4-9241-d9ef372e3870", 1, NULL, "香辣牛肉", NULL);
INSERT INTO brand VALUES (NULL, "dcfe8d1f-28d8-46ef-88ae-dba23605f239", 1, NULL, "麻辣牛肉", NULL);
INSERT INTO brand VALUES (NULL, "2fe256a9-417b-4568-9a8c-5765db13efc2", 1, NULL, "麻辣排骨", NULL);
INSERT INTO brand VALUES (NULL, "d9999c28-8516-4fc9-a703-697f5a723440", 1, NULL, "辣旋风", NULL);
INSERT INTO brand VALUES (NULL, "9a7f9ecf-55bc-4753-9c48-4d108874b196", 1, NULL, "海陆鲜汇", NULL);
INSERT INTO brand VALUES (NULL, "7c051984-28c6-49ba-ab5d-a0f83eb17535", 1, NULL, "亚洲精选", NULL);
INSERT INTO brand VALUES (NULL, "41eab31f-4905-4697-9e2d-3080e245ad54", 1, NULL, "酱香传奇", NULL);
INSERT INTO brand VALUES (NULL, "bda1e9af-5790-46ce-8b9a-7a525f39a176", 1, NULL, "东北炖", NULL);
INSERT INTO brand VALUES (NULL, "9284ecfb-1ce1-4ea1-8cb6-031af81ac867", 1, NULL, "油泼辣子", NULL);
INSERT INTO brand VALUES (NULL, "c1fa5251-2839-4d25-aecd-78d9f615b085", 1, NULL, "酸香世家", NULL);
INSERT INTO brand VALUES (NULL, "b0783ace-8850-4531-8ac5-115d12714e83", 1, NULL, "江南美食", NULL);
INSERT INTO brand VALUES (NULL, "c6127ef5-9e61-450c-a99e-7626b6d64d02", 1, NULL, "本帮烧", NULL);
INSERT INTO brand VALUES (NULL, "c79ff907-1abe-416e-9f35-a4b304dc8d69", 1, NULL, "山珍海烩", NULL);
INSERT INTO brand VALUES (NULL, "215c204c-9571-4dd8-b468-d3abcd6a5c93", 1, NULL, "老火靓汤", NULL);
INSERT INTO brand VALUES (NULL, "7caa1322-1bd6-4e7d-82f7-2010b8fa9e00", 1, NULL, "千椒百味", NULL);
INSERT INTO brand VALUES (NULL, "bedcb6fd-4a9b-4c92-962f-ec8d8272b205", 1, NULL, "蒸行家", NULL);
INSERT INTO brand VALUES (NULL, "071e4dd6-6f82-4e73-ba84-6d63beec271c", 1, NULL, "油辣子传奇", NULL);
INSERT INTO brand VALUES (NULL, "f2ad6e41-0071-4c7b-b88e-4c61bd8f50b9", 1, NULL, "陈泡风云", NULL);
INSERT INTO brand VALUES (NULL, "4e878cce-8a10-42e8-b0c0-93c7bd006fc6", 1, NULL, "面霸", NULL);
INSERT INTO brand VALUES (NULL, "fa5cf003-5f01-4240-865a-098b64ba89a8", 1, NULL, "干拌面", NULL);
INSERT INTO brand VALUES (NULL, "be4a4e9a-1d2d-409e-b44c-2f27a0cc1440", 1, NULL, "食面八方", NULL);
INSERT INTO brand VALUES (NULL, "a519b99b-d218-46fe-96fc-f04ea25f1c98", 1, NULL, "好滋味", NULL);
INSERT INTO brand VALUES (NULL, "794fb4d9-30c6-4d22-b2e3-8d9c3486ed04", 1, NULL, "劲爽拉面", NULL);
INSERT INTO brand VALUES (NULL, "edcae558-1512-4194-b165-decca55e7a6a", 1, NULL, "点心面", NULL);
INSERT INTO brand VALUES (NULL, "c91b3c03-82b3-4633-be12-cf2f62b6cf65", 1, NULL, "金牌福满多", NULL);
INSERT INTO brand VALUES (NULL, "192ec75d-6490-4d42-aa79-2fe1934d92b6", 1, NULL, "超级福满多", NULL);
INSERT INTO brand VALUES (NULL, "07e09508-c9d8-491b-9057-76506d5d9433", 1, NULL, "福香脆", NULL);
INSERT INTO brand VALUES (NULL, "741d68fa-18b1-47fc-9575-0321a371f0f0", 1, NULL, "福满多", NULL);
INSERT INTO brand VALUES (NULL, "05a419dd-a4ff-47b0-96a1-67f345f4a6e5", 1, NULL, "一碗香", NULL);

-- Steps                        

-- Noodles                      
INSERT INTO noodles VALUES (NULL, "1f7f90de-5de9-41bf-86d4-743e84b9d97e", 1, "红烧牛肉面", NULL, NULL, NULL, NULL, NULL, NULL, 180, NULL, NULL);
INSERT INTO noodles VALUES (NULL, "e5186599-2ac5-4ce0-b25c-105a5302d53b", 8, "辣酱面", 118, 90, NULL, NULL, NULL, NULL, 180, "严选优质食材；精心料理好酱；正宗辣酱美味", NULL);

-- Barcodes
INSERT INTO barcode VALUES (1, 6903252049343, 2);
