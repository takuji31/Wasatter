PRAGMA foreign_keys = false;
DROP TABLE IF EXISTS "accounts";
CREATE TABLE "accounts" (
	 "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
	 "type" integer NOT NULL,
	 "name" text NOT NULL,
	 "token" text NOT NULL,
	 "token_secret" text NOT NULL
);
DROP TABLE IF EXISTS "pages";
CREATE TABLE "pages" (
	 "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
	 "name" text NOT NULL,
	 "type" integer NOT NULL,
	 "config" text NOT NULL
);
CREATE INDEX "account_type" ON "accounts" (type ASC);
PRAGMA foreign_keys = true;