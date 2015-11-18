package test.svn;

import java.io.File;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class TestSVNKit {
	
	public static void main(String[] args) throws SVNException {
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		final SvnLog log = svnOperationFactory.createLog();
		log.setSingleTarget(SvnTarget.fromFile(new File("/home/saimir/svn/ProjectMiningVizualization")));
		log.addRange(SvnRevisionRange.create(SVNRevision.create(12345), SVNRevision.create(12345)));
		log.setDiscoverChangedPaths(true);
		final SVNLogEntry logEntry = log.run();

		final Map<String,SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
		for (Map.Entry<String, SVNLogEntryPath> entry : changedPaths.entrySet()) {
		    final SVNLogEntryPath svnLogEntryPath = entry.getValue();
		    System.out.println(svnLogEntryPath.getType() + " " + svnLogEntryPath.getPath() +
		            (svnLogEntryPath.getCopyPath() == null ?
		                    "" : (" from " + svnLogEntryPath.getCopyPath() + ":" + svnLogEntryPath.getCopyRevision())));
		}
	}
}
