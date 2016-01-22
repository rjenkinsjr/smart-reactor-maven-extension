package info.ronjenkins.maven.rtr.reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import com.google.common.base.Strings;

/**
 * A bi-directional dependency graph of a Maven session's reactor.
 * 
 * <p>
 * Remember that a dependency relationship is defined as a module declaring
 * another module as either a <dependency> or as its <parent>. Aggregation does
 * NOT count as a dependency, so aggregated modules that are not in the
 * dependency graph will not show up here.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class ReactorDependencyGraph {

    private final Node root;

    /**
     * Assembles a reactor graph for the given Maven session.
     * 
     * @param session
     *            not null.
     */
    public ReactorDependencyGraph(final MavenSession session) {
        Validate.notNull(session, "session is null");
        this.root = new Node(null, session.getTopLevelProject(),
                session.getProjectDependencyGraph());
    }

    /**
     * Indicates whether or not this reactor graph is compatible with the Smart
     * Reactor. A reactor graph is compatible with the Smart Reactor if every
     * node in the graph is compatible.
     * 
     * @return true if the graph is compatible, false otherwise.
     * @see Node#isSmartReactorCompatible()
     */
    public boolean isSmartReactorCompatible() {
        return isSmartReactorCompatible(Collections.singletonList(this
                .getRoot()));
    }

    private static boolean isSmartReactorCompatible(final List<Node> nodes) {
        for (final Node node : nodes) {
            if (!node.isSmartReactorCompatible()) {
                return false;
            }
            if (!isSmartReactorCompatible(node.getChildren())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the root of this graph.
     * 
     * @return not null.
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * Logs this reactor graph to the given logger at ERROR severity.
     * 
     * @param logger
     *            not null.
     */
    public void error(final Logger logger) {
        Validate.notNull(logger, "logger is null");
        for (final String line : this.getRoot().asString()) {
            logger.error(line);
        }
    }

    /**
     * A node in the {@link ReactorDependencyGraph}.
     * 
     * @author Ronald Jack Jenkins Jr.
     */
    public final class Node extends MavenProject {

        private final Node parent;
        private final List<Node> children = new ArrayList<Node>();
        private final boolean smartReactorCompatible;

        /**
         * Constructor.
         * 
         * @param parent
         *            the node that is the parent of this node. Null means no
         *            parent (execution root).
         * @param self
         *            the project that this node represents. Not null.
         * @param pdg
         *            the dependency graph being converted into this reactor
         *            graph. Not null.
         */
        public Node(final Node parent, final MavenProject self,
                final ProjectDependencyGraph pdg) {
            super(self);
            Validate.notNull(pdg, "project dependency graph is null");
            this.parent = parent;
            // Get all the child projects.
            // This is recursive construction dependent on "this". BE VERY
            // CAREFUL when editing this code.
            final List<MavenProject> childProjects = pdg.getDownstreamProjects(
                    self, false);
            for (final MavenProject child : childProjects) {
                this.children.add(new Node(this, child, pdg));
            }
            this.smartReactorCompatible = determineSmartReactorCompatibility(pdg);
        }

        private boolean determineSmartReactorCompatibility(
                final ProjectDependencyGraph pdg) {
            if (this.getArtifact().isSnapshot()) {
                // If a node is a SNAPSHOT, it must either not have a parent or
                // all of its ancestors must also be SNAPSHOTs.
                Node ancestor = this.getParent();
                while (ancestor != null) {
                    if (!ancestor.getArtifact().isSnapshot()) {
                        return false;
                    }
                    ancestor = ancestor.getParent();
                }
                return true;
            } else {
                // If a node is a non-SNAPSHOT, all of its children must also be
                // non-SNAPSHOTs. A non-SNAPSHOT node with no children is
                // compatible.
                for (final MavenProject descendant : pdg.getUpstreamProjects(
                        this, true)) {
                    if (descendant.getArtifact().isSnapshot()) {
                        return false;
                    }
                }
                return true;
            }
        }

        /**
         * Returns the parent of this node.
         * 
         * @return null iff this is the execution root of the Maven session from
         *         which this graph was generated.
         */
        public Node getParent() {
            return this.parent;
        }

        /**
         * Returns the children of this node.
         * 
         * @return never null but may be empty.
         */
        public List<Node> getChildren() {
            return this.children;
        }

        /**
         * Indicates whether or not this node is compatible with the Smart
         * Reactor.
         * 
         * <ol>
         * <li>If this node is a SNAPSHOT, and if all of its ancestors are also
         * SNAPSHOTs, this method return true.</li>
         * <li>If this node is a SNAPSHOT, and if this node has no parent, this
         * method return true.</li>
         * <li>If this node is a non-SNAPSHOT, and if all of its descendants are
         * also non-SNAPSHOTS, this method return true.</li>
         * <li>If this node is a non-SNAPSHOT, and if this node has no
         * descendants, this method return true.</li>
         * <li>If none of the above conditions are met, this method return
         * false.</li>
         * </ol>
         * 
         * @return true if it is compatible, false otherwise.
         */
        public boolean isSmartReactorCompatible() {
            return this.smartReactorCompatible;
        }

        /**
         * Returns the reactor graph, starting at this node, as a list of
         * strings.
         * 
         * @return not null.
         */
        public List<String> asString() {
            return this.asString(0);
        }

        private List<String> asString(final int indent) {
            final List<String> graph = new ArrayList<String>();
            final String incompatible;
            if (this.isSmartReactorCompatible()) {
                incompatible = "";
            } else {
                incompatible = " [ERROR]";
            }
            graph.add(Strings.repeat(" ", indent)
                    + this.getArtifact().toString() + incompatible);
            for (int n = 0; n < this.children.size(); n++) {
                graph.addAll(this.children.get(n).asString(indent + 2));
            }
            return graph;
        }

    }

}
