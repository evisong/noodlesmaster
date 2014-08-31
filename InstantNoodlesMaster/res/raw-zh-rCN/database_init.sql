DROP INDEX IF EXISTS "user_product_productId_idx";
DROP INDEX IF EXISTS "user_product_lastSelectedOn_idx";
DROP TABLE IF EXISTS "user_product";
CREATE TABLE "user_product" (
	"_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	"productId" TEXT NOT NULL, 
	"name"  TEXT NOT NULL, 
	"brand"  TEXT,
	"manufacturer"  TEXT,
	"description"  TEXT,
	"imageUrl"  TEXT,
	"buyUrl"  TEXT,
	"createdOn"  INTEGER,
	"lastSelectedOn"  INTEGER
);
CREATE UNIQUE INDEX "user_product_productId_idx" ON "user_product" ("productId");
CREATE INDEX "user_product_lastSelectedOn_idx" ON "user_product" ("lastSelectedOn");

INSERT INTO "user_product" VALUES (
	NULL, 
	'B00D3HEPE4', 
	'五谷道场麦香红烧牛肉面五连包101g*5', 
	'五谷道场', 
	'中粮五谷道场食品有限公司', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B00D3HEPE4/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B00D3HEPE4&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144070, 
	1409476144070
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B008FHK03C', 
	'出前一丁油炸方便面(九洲猪骨浓汤味)100g*3(中国香港进口)', 
	'出前一丁', 
	'日清食品有限公司', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B008FHK03C/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B008FHK03C&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144060, 
	1409476144060
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B00EDHBJ5G', 
	'农心辛拉面袋面120g*5', 
	'农心', 
	'沈阳农心食品有限公司北京分公司', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B00EDHBJ5G/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B00EDHBJ5G&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144050, 
	1409476144050
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B003PGRQQC', 
	'Yumyum养养牌冬荫功面(酸辣虾味浓汤面70g*5包)350g(泰国进口)', 
	'Yumyum 养养', 
	'泰国', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B003PGRQQC/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B003PGRQQC&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144040, 
	1409476144040
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B00J4EAFN0', 
	'康师傅经典酸菜牛肉五连包123g*5', 
	'康师傅', 
	'天津顶益食品有限公司北京分公司', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B00J4EAFN0/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B00J4EAFN0&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144030, 
	1409476144030
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B003PGRQUI', 
	'Sedaap喜达香辣干拌面(方便面)88g*5(印度尼西亚进口)', 
	'喜达', 
	'印度尼西亚', 
	NULL, 
	NULL, 
	'http://www.amazon.cn/gp/product/B003PGRQUI/ref=as_li_ss_tl?ie=UTF8&camp=536&creative=3132&creativeASIN=B003PGRQUI&linkCode=as2&tag=noodlesmaster-android-app-23', 
	1409476144020, 
	1409476144020
);