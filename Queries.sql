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

-- Top 20 Edits for USER with name %bernd%
SELECT `User`.`name` as Name, count(*) as NumberOfEdits 
FROM `User`, `Edit`, `Commit` 
WHERE `User`.`id` = `Commit`.`user_id` AND `Edit`.`commit_id` = `Commit`.`id` AND `User`.`email` LIKE "%bernd%"
GROUP BY Name
ORDER BY NumberOfEdits DESC 
LIMIT 20

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

-- Get the File story for each file
SELECT `File`.`path`, `Commit`.`timeStamp`, `Commit`.`comment`, `User`.`id` as theUser, ABS(`Edit`.`linesAdded`-`Edit`.`linesRemoved`) as delta 
FROM File, FileAction, `Edit`,`Commit`, `User` 
WHERE `User`.`id` = `Commit`.`id` AND FileAction.file_path = File.path AND `Commit`.`id` = FileAction.commit_id 
ORDER BY `File`.`path`, `Commit`.`timeStamp` ASC 
LIMIT 100 


-- Get the File story of a file
SELECT `Commit`.*, `User`.`name`
FROM `File`, `FileAction`, `Commit`, `User` 
WHERE `File`.`path` = 'build.xml' 
	AND `FileAction`.`commit_id` = `Commit`.`id` 
	AND `FileAction`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
ORDER BY `Commit`.`timeStamp` ASC 


SELECT `Commit`.*, `User`.`name`
FROM `File`, `FileAction`, `Commit`, `User` 
WHERE `File`.`path` = 'src/help/fr/EntryEditorHelp.html' 
	AND `FileAction`.`commit_id` = `Commit`.`id` 
	AND `FileAction`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
ORDER BY `Commit`.`timeStamp` ASC 


SELECT `Commit`.`id`, `Commit`.`comment`, `Commit`.`timeStamp`, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, 
sum(linesAdded)/(sum(linesAdded)+sum(linesRemoved)) as PercentAdded,  
(sum(linesAdded)-sum(linesRemoved))/(sum(linesAdded)+sum(linesRemoved)) as DeltaIndex, `User`.`name`
FROM `File`, `Edit`, `Commit`, `User` 
WHERE `File`.`path` = 'pom.xml' 
	AND `Edit`.`commit_id` = `Commit`.`id` 
	AND `Edit`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
GROUP BY `Edit`.`id`
ORDER BY `Commit`.`timeStamp` ASC 

-- Aggregate by date
SELECT GROUP_CONCAT(`Commit`.`comment` SEPARATOR '\n') as Comments, DATE(`Commit`.`timeStamp`) as Date, sum(linesAdded) as TotalLinesAdded, sum(linesAdded+linesRemoved) TotalChangeInTheDay, GROUP_CONCAT(`User`.`name` SEPARATOR '\n') as Users
FROM `File`, `Edit`, `Commit`, `User` 
WHERE `File`.`path` = 'pom.xml' 
	AND `Edit`.`commit_id` = `Commit`.`id` 
	AND `Edit`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
GROUP BY Date
ORDER By Date ASC

-- With totalLines at each day
SELECT GROUP_CONCAT(`Commit`.`comment` SEPARATOR '\n') as Comments, DATE(`Commit`.`timeStamp`) as Date, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, sum(linesAdded+linesRemoved) TotalChangeInTheDay, sum(DISTINCT `FileAction`.`totalLines`) as LinesThisDay, MIN(`FileAction`.`totalLines`) as MinLinesThisDay, MAX(`FileAction`.`totalLines`) as MaxLinesThisDay, AVG(`FileAction`.`totalLines`) as AVGLinesThisDay, GROUP_CONCAT(`User`.`name` SEPARATOR '\n') as Users
FROM `File`, `Edit`, `FileAction`,`Commit`, `User` 
WHERE `File`.`path` = 'pom.xml' 
	AND `Edit`.`commit_id` = `Commit`.`id` 
	AND `Edit`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
    AND `FileAction`.`file_path` = `File`.`path` 
    AND `FileAction`.`commit_id` = `Commit`.`id`
GROUP BY Date
ORDER By Date ASC

-- Same as above with less attributes
SELECT GROUP_CONCAT(`Commit`.`comment` SEPARATOR ' . ') as Comments, DATE(`Commit`.`timeStamp`) as Date, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, sum(linesAdded+linesRemoved) TotalChangeInTheDay, sum(linesAdded-linesRemoved) TotalDiffInTheDay, sum(DISTINCT `FileAction`.`totalLines`) as LinesUntilThisDay, GROUP_CONCAT(`User`.`name` SEPARATOR ' . ') as Users
FROM `File`, `Edit`, `FileAction`,`Commit`, `User` 
WHERE `File`.`path` = 'pom.xml' 
	AND `Edit`.`commit_id` = `Commit`.`id` 
	AND `Edit`.`file_path` = `File`.`path`
	AND `User`.`id` = `Commit`.`user_id`
    AND `FileAction`.`file_path` = `File`.`path` 
    AND `FileAction`.`commit_id` = `Commit`.`id`
GROUP BY Date
ORDER By Date ASC

-- Get change frequency for file
SELECT File.path, SUM(`Commit`.id) as Frq 
FROM File, FileAction, `Commit` 
WHERE FileAction.file_path = File.path AND FileAction.id = `Commit`.`id` 
GROUP BY File.path 
ORDER BY Frq DESC 


