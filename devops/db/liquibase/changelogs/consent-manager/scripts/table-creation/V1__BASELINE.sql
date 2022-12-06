CREATE TABLE CITIZEN (
	ID SERIAL PRIMARY KEY,
	TAX_CODE VARCHAR(16) NOT NULL,
	CONSENT_TYPE VARCHAR(7) NOT NULL,
	CONSENT_DATE TIMESTAMP NOT NULL,
	CONSENT_UPDATE_DATE TIMESTAMP,
	CONSENT_CLIENT VARCHAR(50) NOT NULL,
	CONSENT_UPDATE_CLIENT VARCHAR(50),
	DELETED BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX CITIZEN_INDEX ON CITIZEN(TAX_CODE, DELETED);

CREATE TABLE CARD (
	ID SERIAL PRIMARY KEY,
	CITIZEN_ID INT NOT NULL,
	HPAN VARCHAR(64) NOT NULL,
	DELETED BOOLEAN NOT NULL DEFAULT FALSE,
	UNIQUE (CITIZEN_ID, HPAN),
	CONSTRAINT FK_CITIZEN FOREIGN KEY(CITIZEN_ID) REFERENCES CITIZEN(ID)
);

CREATE TABLE SERVICE (
	ID SERIAL PRIMARY KEY,
	NAME VARCHAR(20) UNIQUE NOT NULL,
	DESCRIPTION VARCHAR(50)
);

CREATE TABLE CARD_SERVICE (
	CARD_ID INT NOT NULL,
	SERVICE_ID INT NOT NULL,
	CONSENT_TYPE VARCHAR(7) NOT NULL,
	CONSTRAINT FK_CARD FOREIGN KEY(CARD_ID) REFERENCES CARD(ID),
	CONSTRAINT FK_SERVICE FOREIGN KEY(SERVICE_ID) REFERENCES SERVICE(ID),
	PRIMARY KEY(CARD_ID, SERVICE_ID)
);