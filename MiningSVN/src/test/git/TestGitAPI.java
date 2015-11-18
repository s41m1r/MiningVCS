package test.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class TestGitAPI {

	public static void main(String[] args) {
		try {
			Repository repo = new FileRepositoryBuilder()
			.setGitDir(new File("/home/saimir/git/hoosegow/.git"))
			.build();
			
			// Get a reference
			Ref master = repo.getRef("master");

			// Get the object the reference points to
			ObjectId masterTip = master.getObjectId();

			// Rev-parse
			ObjectId obj = repo.resolve("HEAD^{tree}");

			// Load raw object contents
			ObjectLoader loader = repo.open(masterTip);
			loader.copyTo(System.out);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
