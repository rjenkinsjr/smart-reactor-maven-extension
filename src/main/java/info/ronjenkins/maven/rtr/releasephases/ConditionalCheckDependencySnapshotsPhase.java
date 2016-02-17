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
import org.apache.maven.shared.release.phase.CheckDependencySnapshotsPhase;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * Checks for SNAPSHOT dependencies in the reactor, but only if requested.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = ReleasePhase.class, hint = "conditional-check-dependency-snapshots")
public class ConditionalCheckDependencySnapshotsPhase extends
CheckDependencySnapshotsPhase {

  @Requirement(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
  private RTR rtr;
  @Requirement
  protected Logger logger;

  /**
   * Calls super, if permitted.
   *
   * @param rd
   *          not null.
   * @param re
   *          not null.
   * @param projects
   *          not null.
   * @return the result of the phase execution.
   * @throws ReleaseExecutionException
   *           as needed.
   * @throws ReleaseFailureException
   *           as needed.
   */
  @Override
  public ReleaseResult execute(final ReleaseDescriptor rd,
      final ReleaseEnvironment re, final List<MavenProject> projects)
          throws ReleaseExecutionException, ReleaseFailureException {
    final ReleaseResult result;
    if (this.rtr.isExternalSnapshotsAllowed()) {
      this.logger
      .warn("External SNAPSHOT artifacts are allowed for this release. Artifacts produced by this build may not behave consistently compared to earlier builds.");
      result = new ReleaseResult();
      result.setResultCode(ReleaseResult.SUCCESS);
    } else {
      result = super.execute(rd, re, projects);
    }
    return result;
  }

  /**
   * Invokes the {@link #execute(ReleaseDescriptor, ReleaseEnvironment, List)
   * execute} method.
   *
   * @param rd
   *          not null.
   * @param re
   *          not null.
   * @param projects
   *          not null.
   * @return the result of the phase execution.
   * @throws ReleaseExecutionException
   *           as needed.
   * @throws ReleaseFailureException
   *           as needed.
   */
  @Override
  public ReleaseResult simulate(final ReleaseDescriptor rd,
      final ReleaseEnvironment re, final List<MavenProject> projects)
          throws ReleaseExecutionException, ReleaseFailureException {
    return this.execute(rd, re, projects);
  }

}
