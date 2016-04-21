-- Top 10 changed
SELECT `file_path` as File, count(*) as NumberOfEdits 
FROM `Edit` 
group by File 
order by NumberOfEdits DESC 
LIMIT 100

-- Least 10 changed and File Name does not start with a dot .%
SELECT `file_path` as File, count(*) as NumberOfEdits 
FROM `Edit` 
WHERE `file_path` NOT LIKE '.%'
group by File 
order by NumberOfEdits ASC 
LIMIT 10

-- Top 10 User with more edits
SELECT Name, count(*) as NumberOfEdits 
FROM `User`, `Edit`, `Commit` 
WHERE `User`.`id` = `Commit`.`user_id` AND `Edit`.`commit_id` = `Commit`.`id` 
GROUP BY Name 
ORDER BY NumberOfEdits DESC 
LIMIT 10

-- Top 10 users with the number of lines added/removed
SELECT Name, count(*) as NumberOfEdits, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved
FROM `User`, `Edit`, `Commit` 
WHERE `User`.`id` = `Commit`.`user_id` 
	AND `Edit`.`commit_id` = `Commit`.`id` 
GROUP BY Name 
ORDER BY NumberOfEdits DESC 
LIMIT 10

-- Top 10 users with the number of lines added/removed and delta
SELECT Name, count(*) as NumberOfEdits, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, sum(linesAdded)-sum(linesRemoved) as Delta
FROM `User`, `Edit`, `Commit` 
WHERE `User`.`id` = `Commit`.`user_id` 
AND `Edit`.`commit_id` = `Commit`.`id` 
GROUP BY Name 
ORDER BY Delta DESC 
LIMIT 10

-- Top 10 users whose effort has been more durable
SELECT Name, count(*) as NumberOfEdits, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, 
(sum(linesAdded)-sum(linesRemoved))/(sum(linesAdded)+sum(linesRemoved)) as DeltaIndex
FROM `User`, `Edit`, `Commit` 
WHERE `User`.`id` = `Commit`.`user_id` 
AND `Edit`.`commit_id` = `Commit`.`id` 
GROUP BY Name 
ORDER BY DeltaIndex DESC 
LIMIT 10

-- Commits relative to Action 
SELECT * FROM `Commit` 
WHERE Commit.id in 
		(select commit_id from Edit) and 
	Commit.id in 
		(select commit_id from FileAction where type = "CHMOD")
		
-- Commits relative to all except Action 
SELECT * FROM `Commit` 
WHERE Commit.id in 
		(select commit_id from Edit) and 
	Commit.id in 
		(select commit_id from FileAction where type <> "CHMOD")
		
SELECT comment from Commit WHERE 1;

select File.* from File where 1


-- Select the file that do not appear in Rename
SELECT * FROM `FileAction`,`Commit` 
WHERE `FileAction`.`totalLines`<0 AND `FileAction`.`commit_id` = `Commit`.`id` 
AND `FileAction`.`file_path` NOT IN (SELECT `Rename`.`from_path` FROM `Rename` UNION SELECT `Rename`.`to_path` FROM `Rename`)
ORDER BY `Commit`.`timeStamp` ASC
