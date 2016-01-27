package info.ronjenkins.maven.rtr;

import org.apache.commons.lang.Validate;
import org.apache.maven.project.ProjectBuilder;

/**
 * Components needed throughout the Smart Reactor that can't be accessed
 * directly via Plexus.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
// TODO: work on eliminating this class if possible.
public final class RTRComponents {

    private final ProjectBuilder projectBuilder;

    /**
     * Constructor.
     * 
     * @param projectBuilder
     *            not null.
     */
    public RTRComponents(final ProjectBuilder projectBuilder) {
        Validate.notNull(projectBuilder, "Project builder is null");
        this.projectBuilder = projectBuilder;
    }

    /**
     * Returns the shared project builder.
     * 
     * @return never null.
     */
    public ProjectBuilder getProjectBuilder() {
        return this.projectBuilder;
    }

}
