-- DROP INDEX IF EXISTS "user_product_productId_idx";
-- DROP INDEX IF EXISTS "user_product_lastSelectedOn_idx";
-- DROP TABLE IF EXISTS "user_product";
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