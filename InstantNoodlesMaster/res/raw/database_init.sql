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
	'B00778B90S', 
	'Nongshim Shin Noodle Ramyun Gourmet Spicy, 4.2-Ounce Packages, 20-Count', 
	'Nongshim', 
	'Nongshim', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B00778B90S/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B00778B90S&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=B3PPHU2BBY3CL2SF', 
	1409476144070, 
	1409476144070
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B0028PDFQG', 
	'Myojo Ippeichan Yakisoba Japanese Style Instant Noodles, 4.77-Ounce Tubs (Pack of 6)', 
	'Myojo', 
	'Myojo', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B0028PDFQG/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B0028PDFQG&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=HYOE6FRE7DE6EOXY', 
	1409476144060, 
	1409476144060
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B00824JP3Y', 
	'JML INSTANT NOODLE STEWED BEEF FLAVOR-5bags', 
	'DragonMall', 
	'DragonMall', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B00824JP3Y/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B00824JP3Y&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=26655UNX7QPTLER6', 
	1409476144050, 
	1409476144050
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B000QFOXTS', 
	'30 Packages Mama Tom Yum Flavour Instant Noodles', 
	'MAMA', 
	'MAMA', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B000QFOXTS/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B000QFOXTS&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=DQ5LCJXXNIMJMLFR', 
	1409476144040, 
	1409476144040
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B0054TWPNC', 
	'Nongshim Hot & Spicy Noodle Bowl, 3.03 Ounce Bowls (Pack of 12)', 
	'Nong Shim', 
	'Nong Shim', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B0054TWPNC/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B0054TWPNC&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=LGEX7DJ4SN53RO37', 
	1409476144030, 
	1409476144030
);
INSERT INTO "user_product" VALUES (
	NULL, 
	'B0088I159Q', 
	'Unif bowl instant noodles - artificial beef with sauerkrant flavor (pack of 12)', 
	'DragonMall', 
	'DragonMall', 
	NULL, 
	NULL, 
	'http://www.amazon.com/gp/product/B0088I159Q/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B0088I159Q&linkCode=as2&tag=noodlesmaster-android-app-20&linkId=HJ5PTJZCSJGUEM73', 
	1409476144020, 
	1409476144020
);