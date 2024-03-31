CREATE TABLE test_table_1 (
  id INTEGER NOT NULL,
  enum_field VARCHAR(50),
  big_field VARCHAR(1000),
  read_only INTEGER,
  is_deleted SMALLINT,
  double_field DOUBLE PRECISION,
  char_field CHAR(10),
  date_field DATE,
  time_field TIME,
  timestamp_field TIMESTAMP,
  decimal_field_1 DECIMAL(5),
  decimal_field_2 DECIMAL(10,2),
  numeric_field NUMERIC,
  boolean_field BOOLEAN,
  smallint_field SMALLINT,
  bigint_field BIGINT,
  real_field REAL,
  binary_field BYTEA,
  varbinary_field BYTEA,
  other_field JSONB,
  PRIMARY KEY (id)
);

CREATE TABLE test_table_2 (
  id INT NOT NULL,
  enum_field_2 VARCHAR(50),
  big_field_2 VARCHAR(1000),
  read_only INTEGER,
  test_array INTEGER[],
  test_array2 VARCHAR(50)[],
  test_object JSONB,
  is_deleted SMALLINT,
  PRIMARY KEY (id)
);

CREATE TABLE test_table_3 (
  id INTEGER NOT NULL,
  enum_field_2 VARCHAR(50),
  big_field_2 VARCHAR(1000),
  read_only INTEGER,
  is_deleted SMALLINT,
  PRIMARY KEY (id)
);
