# How to execute

java -cp jar-file.jar -f /path/to/logfile -t threshold-integer

Example. This launches the ProjectMiner from the project-mining.jar. The input is ~/git/MiningCVS/MiningSVN/resources/short.log 

´´´ java -cp project-mining.jar main.ProjectMiner -f ~/git/MiningCVS/MiningSVN/resources/short.log -t 3 -g ´´´

## Options

Currently the following parameters are supported 

	 -f,--logFile <arg>     use given file for log
	 -g,--useGitLog         the input log is from a Git repository
	 -h,--help              print this message
	 -svn,--useSvnLog       the input log is from a Subversion repository
	 -t,--threshold <arg>   use given threshold for aggregation
	 
