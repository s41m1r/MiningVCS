#!/bin/bash
#usage import.sh directory/of/your/git/project databasename
#generates a file named "project.log" inside the folder

cd "$1"

d=`dirname $1`
f=`basename $1`

dbname=`basename $2`

echo "importing $d/$f in database $dbname"

## Set your merge.renameLimit variable 
git config merge.renameLimit 10000

## Set your diff.renameLimit variable
git config diff.renameLimit 10000

## Generate the log
echo "Generating log ... "
git log --branches --remotes --tags  --reverse --full-history --parents --topo-order --pretty=format:"§§--§§%ncommit %H %P%nAuthor: %an <%ae>%nDate: %cd %nMessage: %s%n---§%n" -M -p > "$f.log" 
echo "Done."
echo "Importing into DB ... "

## Run git importer from command line
# "java -jar /home/saimir/git/GitImporter.jar -logFile %s in -database %s", "$f.log", "$dbname"  
java -jar /home/saimir/git/MiningVCS/GitImporter.jar -logFile "$f.log" -database $dbname 

echo "Done."
