# Description

This software project aims at providing useful analysis tools for version control system log data. By now, GitHub logs are supported. Subversion is only partially supported.

It uses a database structured as in the figure below. ![alt text][dbschema]

[dbschema]: https://github.com/s41m1r/MiningVCS/blob/master/DBSchema.png "Database schema for capturing software development event logs" 

Features main brach:

* Import a GitHub log into a databse 
* Query events
* Export user stories
* Export time series correlations for projects

# Try it out

## Requirements
* Java
* MySQL

## Download
* Download the executable jar file [project-mining-jar-with-dependencies.jar](https://github.com/s41m1r/MiningVCS/blob/master/project-mining-jar-with-dependencies.jar)
* Download the scripts
  * `cleanup.sh`
  * `deploy.sh`
  * `export-correlations.sh`
  * `import.sh`

Put everything under the same directory `~/projectmining`.

## Configure your git client
To keep track of many merges and renames, set your `diff.rename` and `merge.rename` limits to a high number.
```
git config diff.renameLimit 10000
git config merge.renameLimit 10000
```

## Prepare the dataset

1. Choose a project from GitHub and clone it locally. E.g., Tablesaw 

`git clone https://github.com/jtablesaw/tablesaw.git`

This will create a directory, e.g., `~/git/tablesaw`.

2. Import the dataset. Run the `import.sh` script.
``` 
./import.sh ~/git/tablesaw/ tablesaw
```
This creates a new database and populates it with all the data from tablesaw. If you have phpmyadmin you can explore the data through a web browser and make several queries.

## Exporting file stories
Choose the database name of the project whose file stories you want to export and then run the following command. E.g., for `tablesaw`:
```
java -cp project-mining-jar-with-dependencies.jar at.ac.wu.infobiz.projectmining.export.ExportStories tablesaw
```
This will create two folders
* Stories: a set of .csv file stories, one for each file (events aggregated by day). e.g. `tablesaw-stories`
* NamesMap file. Given the huge number and the extremely long names of some files which cannot be stored in all file systems, files have been r1enamed with shorter names like `f1.csv`, `f2.csv`, etc. The file that keep track of this mapping is `namesMap.csv` and can be found under the namesMap folder (e.g.,`tablesaw-namesMap`).


# Publication

Please refer to the [paper](https://link.springer.com/chapter/10.1007/978-3-319-65000-5_10 "Uncovering the Hidden Co-evolution in the Work History of Software Projects") published in BPM 2017.
```
Bala S., Revoredo K., de A.R. Gonçalves J.C., Baião F., Mendling J., Santoro F. (2017) 
Uncovering the Hidden Co-evolution in the Work History of Software Projects. 
In: Carmona J., Engels G., Kumar A. (eds) Business Process Management. BPM 2017. 
Lecture Notes in Computer Science, vol 10445. Springer, Cham
```
BibTex
```
@Inbook{Bala2017,
author="Bala, Saimir
and Revoredo, Kate
and de A.R. Gon{\c{c}}alves, Jo{\~a}o Carlos
and Bai{\~a}o, Fernanda
and Mendling, Jan
and Santoro, Flavia",
editor="Carmona, Josep
and Engels, Gregor
and Kumar, Akhil",
title="Uncovering the Hidden Co-evolution in the Work History of Software Projects",
bookTitle="Business Process Management: 15th International Conference, BPM 2017, Barcelona, Spain, September 10--15, 2017, Proceedings",
year="2017",
publisher="Springer International Publishing",
address="Cham",
pages="164--180",
isbn="978-3-319-65000-5",
doi="10.1007/978-3-319-65000-5_10",
url="https://doi.org/10.1007/978-3-319-65000-5_10"
}

```

The paper was evaluated on the following GitHub projects.

* [Facebook Ads SDK for Java](https://github.com/facebook/facebook-java-ads-sdk)
* [Big List of Naughty Strings](https://github.com/minimaxir/big-list-of-naughty-strings)
* [BPMN Modeler + Properties Panel + Deployer](https://github.com/polenz/camunda-resource-deployer-js-example/tree/master/bpmn-modeler)
* [Caret](https://github.com/thomaswilburn/Caret)
* [dhtmlxGantt v.4.2](https://github.com/DHTMLX/gantt)
* [jgit-cookbook](https://github.com/centic9/jgit-cookbook)
* [GraphQL](https://github.com/facebook/graphql)
* [A Word Aligner for English](https://github.com/ma-sultan/monolingual-word-aligner)
* [mysqlclient](https://github.com/facebook/mysqlclient-python)
* [Operation Code](https://github.com/OperationCode/operationcode)



