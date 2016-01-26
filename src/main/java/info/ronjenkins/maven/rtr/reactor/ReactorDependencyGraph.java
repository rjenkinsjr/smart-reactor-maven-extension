/*
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * A traversable, bi-directional version of a {@link ProjectDependencyGraph},
 * based on the {@link MavenSession} that owns said graph.
 * 
 * <p>
 * A graph is composed of one or more {@link Node}s. The <i>root node</i> is the
 * only node that has no parent node; it is defined as
 * {@link MavenSession#getTopLevelProject()}. All other nodes have a parent
 * node. Nodes have zero or more children, all of which are also nodes.
 * 
 * <p>
 * If a graph is {@link #destroy() destroyed}, all methods of the graph and its
 * contained nodes will throw an {@link IllegalStateException}.
 * 
 * <p>
 * Remember that a dependency relationship is defined as a module declaring
 * another module as either a <code>&lt;dependency&gt;</code> or as its
 * <code>&lt;parent&gt;</code>. Aggregation does NOT count as a dependency, so
 * aggregated modules that are part of the reactor but not part of the
 * dependency graph will not appear in this object.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class ReactorDependencyGraph {

    private static final String DESTROYED_ERROR = "Graph has been destroyed.";

    private final Object lock = new Object();

    private MavenSession session;
    private Node root;
    private boolean destroyed = false;

    /**
     * Assembles a reactor graph for the given Maven session.
     * 
     * @param session
     *            not null.
     */
    public ReactorDependencyGraph(final MavenSession session) {
        Validate.notNull(session, "session is null");
        this.root = new Node(null, session.getTopLevelProject());
    }

    /**
     * Returns the root of this graph.
     * 
     * @return never null.
     */
    public Node getRoot() {
        synchronized (this.lock) {
            this.checkForDestruction();
            return this.root;
        }
    }

    /**
     * Indicates whether or not this reactor graph is compatible with the Smart
     * Reactor. A reactor graph is compatible with the Smart Reactor if every
     * node in the graph is compatible. Node compatibility is defined as
     * follows:
     * 
     * <ol>
     * <li>If a node is a SNAPSHOT, and if all of its ancestors are also
     * SNAPSHOTs, it is compatible.</li>
     * <li>If a node is a SNAPSHOT, and if the node has no ancestors (no
     * parent), it is compatible.</li>
     * <li>If a node is a non-SNAPSHOT, and if all of its descendants are also
     * non-SNAPSHOTS, it is compatible.</li>
     * <li>If a node is a non-SNAPSHOT, and if the node has no descendants (no
     * children), it is compatible.</li>
     * <li>If none of the above conditions are met, it is not compatible.</li>
     * </ol>
     * 
     * @return true if the graph is compatible, false otherwise.
     */
    public boolean isSmartReactorCompatible() {
        synchronized (this.lock) {
            this.checkForDestruction();
            return this.root.isGraphSmartReactorCompatible();
        }
    }

    /**
     * Logs this reactor graph to the given logger at ERROR severity.
     * 
     * @param logger
     *            not null.
     */
    public void error(final Logger logger) {
        synchronized (this.lock) {
            this.checkForDestruction();
            Validate.notNull(logger, "logger is null");
            for (final String line : this.root.asString(0)) {
                logger.error(line);
            }
        }
    }

    /**
     * Destroys this graph, causing all nodes in the graph to release their
     * references to each other. Clients should call this method once done with
     * the graph so that the MavenProjects are not accidentally retained in
     * memory.
     * 
     * <p>
     * Calling any other method after calling this method causes an
     * IllegalStateException.
     */
    public void destroy() {
        synchronized (this.lock) {
            this.checkForDestruction();
            this.session = null;
            this.root.destroy();
            this.root = null;
            this.destroyed = true;
        }
    }

    private void checkForDestruction() {
        if (this.destroyed) {
            throw new IllegalStateException(DESTROYED_ERROR);
        }
    }

    /**
     * A node in a {@link ReactorDependencyGraph}. Note that nodes are also
     * Maven projects.
     * 
     * @author Ronald Jack Jenkins Jr.
     */
    public final class Node extends MavenProject {

        private ReactorDependencyGraph graph;
        private Node parent;
        private List<Node> children = new ArrayList<Node>();
        private boolean destroyed = false;

        /**
         * Constructor.
         * 
         * @param parent
         *            the node that is the parent of this node. Null means no
         *            parent (root node).
         * @param self
         *            the project that this node represents. Not null.
         */
        private Node(final Node parent, final MavenProject self) {
            super(self);
            this.graph = ReactorDependencyGraph.this;
            this.parent = parent;
            final ProjectDependencyGraph pdg = graph.session
                    .getProjectDependencyGraph();
            final List<MavenProject> childProjects = pdg.getDownstreamProjects(
                    self, false);
            for (final MavenProject child : childProjects) {
                this.children.add(new Node(this, child));
            }
        }

        /**
         * Returns the graph to which this node belongs.
         * 
         * @return never null.
         */
        public ReactorDependencyGraph getGraph() {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
                return this.graph;
            }
        }

        /**
         * Returns the parent of this node.
         * 
         * @return null iff this is the root node.
         */
        public Node getParent() {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
                return this.parent;
            }
        }

        /**
         * Returns the children of this node.
         * 
         * @return never null but may be empty. Unmodifiable.
         */
        public List<Node> getChildren() {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
                return Collections.unmodifiableList(this.children);
            }
        }

        private void checkForDestruction() {
            if (this.destroyed) {
                throw new IllegalStateException(DESTROYED_ERROR);
            }
        }

        private boolean isGraphSmartReactorCompatible() {
            if (!this.isSmartReactorCompatible()) {
                return false;
            }
            // Only check this node's children if this node is compatible.
            for (final Node child : this.children) {
                if (!child.isGraphSmartReactorCompatible()) {
                    return false;
                }
            }
            // This portion of the graph is compatible.
            return true;
        }

        private boolean isSmartReactorCompatible() {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
                if (this.getArtifact().isSnapshot()) {
                    // A SNAPSHOT node's ancestors must all be SNAPSHOTs.
                    Node ancestor = this.parent;
                    while (ancestor != null) {
                        if (!ancestor.getArtifact().isSnapshot()) {
                            return false;
                        }
                        ancestor = ancestor.parent;
                    }
                } else {
                    // A non-SNAPSHOT node's descendants must all be
                    // non-SNAPSHOTs.
                    final ProjectDependencyGraph pdg = this.graph.session
                            .getProjectDependencyGraph();
                    // Use the PDG as a shortcut to get the list of descendants.
                    for (final MavenProject descendant : pdg
                            .getUpstreamProjects(this, true)) {
                        if (descendant.getArtifact().isSnapshot()) {
                            return false;
                        }
                    }
                }
                // This node is compatible.
                return true;
            }
        }

        private List<String> asString(final int indent) {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
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

        private void destroy() {
            synchronized (ReactorDependencyGraph.this.lock) {
                this.checkForDestruction();
                this.graph = null;
                this.parent = null;
                for (final Node child : this.children) {
                    child.destroy();
                }
                this.children.clear();
                this.children = null;
                this.destroyed = true;
            }
        }

    }

}
