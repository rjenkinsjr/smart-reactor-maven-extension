package info.ronjenkins.maven.rtr.releasephases;

import info.ronjenkins.maven.rtr.RTR;

import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.apache.maven.shared.release.phase.RestoreBackupPomsPhase;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Restores backup POMs, but only if they were created.
 * 
 * @author Ronald Jack Jenkins Jr.
 * @see IndicatePresenceOfBackupPomsPhase
 * @see RemoveBackupPomsPhase
 */
@Component(role = ReleasePhase.class, hint = "defensive-restore-backup-poms")
public class DefensiveRestoreBackupPomsPhase extends RestoreBackupPomsPhase {

    @Requirement(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
    private RTR rtr;

    /**
     * Invokes the {@link #execute(ReleaseDescriptor, ReleaseEnvironment, List)
     * execute} method.
     * 
     * @param rd
     *            not null.
     * @param re
     *            not null.
     * @param projects
     *            not null.
     * @return the result of the phase execution.
     * @throws ReleaseExecutionException
     *             as needed.
     * @throws ReleaseFailureException
     *             as needed.
     */
    @Override
    public ReleaseResult simulate(final ReleaseDescriptor rd,
	    final ReleaseEnvironment re, final List<MavenProject> projects)
	    throws ReleaseExecutionException, ReleaseFailureException {
	return this.execute(rd, re, projects);
    }

    /**
     * Restores all backup POMs for the given projects, if they were created.
     * 
     * @param rd
     *            not null.
     * @param re
     *            not null.
     * @param projects
     *            not null.
     * @return the result of the phase execution.
     * @throws ReleaseExecutionException
     *             as needed.
     * @throws ReleaseFailureException
     *             as needed.
     */
    @Override
    public ReleaseResult execute(final ReleaseDescriptor rd,
	    final ReleaseEnvironment re, final List<MavenProject> projects)
	    throws ReleaseExecutionException, ReleaseFailureException {
	final ReleaseResult result;
	if (this.rtr.isBackupPomsCreated()) {
	    result = super.execute(rd, re, projects);
	} else {
	    result = new ReleaseResult();
	    result.setResultCode(ReleaseResult.SUCCESS);
	}
	return result;
    }

}
