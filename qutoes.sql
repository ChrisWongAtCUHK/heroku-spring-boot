CREATE TABLE
  quotes (
    quoteID int NOT NULL AUTO_INCREMENT,
    quote_string varchar(255),
    PRIMARY KEY (quoteID)
  );

INSERT INTO
  quotes (quote_string)
VALUES
  (
    'Who in my life has shown up for me in the biggest way?'
  ),
  (
    'What''s been my happiest memory the past few weeks?'
  ),
  ('What have I learned that I need more of?'),
  ('What have I learned that I need less of?'),
  (
    'What has most surprised me about myself recently?'
  ),
  (
    'What do I want to prioritize for myself in the coming months?'
  ),
  (
    'What did I most take for granted before this time that I want to more fully appreciate moving forward?'
  );