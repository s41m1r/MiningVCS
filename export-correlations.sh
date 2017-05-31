#!/bin/bash
#usage export-correlations.sh directory-of-project-stories
#must be in the same directory as your project-stories and namesMap folders

#d=`dirname $1`
#f=`basename $1`

SECONDS=0

thisDir=`echo | pwd`

storiesDir=`echo | ls -1d *-stories`
corFolder="correlations"
distFolder="distances"

printf "Exporting correlations from $thisDir into folders $corFolder and $distFolder.\n\n"

printf "Dirs: \n"
for dir in $storiesDir; 
do 
#echo $dir
##Export time series correlations
java -cp project-mining-jar-with-dependencies.jar at.ac.wu.infobiz.projectmining.export.ExportTimeSeriesCorrelation $dir; 
done

printf "\nDone in $(($SECONDS / 3600))h:$(($SECONDS / 60))m:$(($SECONDS))s.\r\n"
