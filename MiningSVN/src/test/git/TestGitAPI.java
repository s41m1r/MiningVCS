package test.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class TestGitAPI {

	public static void main(String[] args) {
		try {
			Repository repo = new FileRepositoryBuilder()
			.setGitDir(new File("/home/saimir/git/MiningCVS/.git"))
			.build();
			
			// Get a reference
			Ref master = repo.getRef("master");

			// Get the object the reference points to
			ObjectId masterTip = master.getObjectId();

			// Rev-parse
			ObjectId obj = repo.resolve("id^1^{tree}");

			// Load raw object contents
			ObjectLoader loader = repo.open(masterTip);
			loader.copyTo(System.out);
			
			Git git = new Git(repo);
			Iterable<RevCommit> log = git.log().call();
			
			for (RevCommit revCommit : log) {
				System.out.println(revCommit.getName()+" "+revCommit.getCommitTime()+" "+revCommit.getShortMessage());
			}
			
			LogCommand logCommand = git.log();
			RevWalk revWalk = new RevWalk(repo);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
