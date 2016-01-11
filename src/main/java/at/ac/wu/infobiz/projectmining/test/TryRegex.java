package at.ac.wu.infobiz.projectmining.test;

public class TryRegex {

	public static void main(String[] args) {
		
		String pattern = "(?<=@@)(.*)(?=@@)";
		String input = ""
				+ "@@ -2,25 +2,20 @@ package org.dstadler.jgit.api;\n" + 
				" \n" + 
				" import java.io.IOException;\n" + 
				" \n" + 
				"+import org.dstadler.jgit.helper.CookbookHelper;\n" + 
				" import org.eclipse.jgit.lib.ObjectLoader;\n" + 
				" import org.eclipse.jgit.lib.Ref;\n" + 
				" import org.eclipse.jgit.lib.Repository;\n" + 
				"-import org.eclipse.jgit.storage.file.FileRepositoryBuilder;\n" + 
				"-\n" + 
				"-\n" + 
				"+import org.eclipse.jgit.revwalk.RevCommit;\n" + 
				"+import org.eclipse.jgit.revwalk.RevTree;\n" + 
				"+import org.eclipse.jgit.revwalk.RevWalk;\n" + 
				" \n" + 
				" /**\n" + 
				"  * Simple snippet which shows how to retrieve a Ref for some reference string.\n" + 
				"- *\n" + 
				"- * @author dominik.stadler@gmx.at\n" + 
				"  */\n" + 
				" public class ReadBlobContents {\n" + 
				" 	public static void main(String[] args) throws IOException {\n" + 
				"-		FileRepositoryBuilder builder = new FileRepositoryBuilder();\n" + 
				"-		Repository repository = builder\n" + 
				"-		  .readEnvironment() // scan environment GIT_* variables\n" + 
				"-		  .findGitDir() // scan up the file system tree\n" + 
				"-		  .build();\n" + 
				"+		Repository repository = CookbookHelper.openJGitCookbookRepository();\n" + 
				" \n" + 
				" 		// the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)\n" + 
				" 		Ref head = repository.getRef(\"refs/heads/master\");\n" + 
				"@@ -30,6 +25,16 @@ public class ReadBlobContents {\n" + 
				" 		ObjectLoader loader = repository.open(head.getObjectId());\n" + 
				" 		loader.copyTo(System.out);\n" + 
				" \n" + 
				"+		System.out.println(\"Print contents of tree of head of master branch, i.e. the latest binary tree information\");\n" + 
				"+\n" + 
				"+		// a commit points to a tree\n" + 
				"+		RevWalk walk = new RevWalk(repository);\n" + 
				"+		RevCommit commit = walk.parseCommit(head.getObjectId());\n" + 
				"+		RevTree tree = walk.parseTree(commit.getTree().getId());\n" + 
				"+		System.out.println(\"Found Tree: \" + tree);\n" + 
				"+		loader = repository.open(tree.getId());\n" + 
				"+		loader.copyTo(System.out);\n" + 
				"+\n" + 
				" 		repository.close();\n" + 
				" 	}\n" + 
				" }\n" + 
				"";
		
		String[] split = input.split(pattern);
	}

}
