-- Change the type of the comments column from varchar to text
ALTER TABLE survey MODIFY `comments` text NOT NULL;