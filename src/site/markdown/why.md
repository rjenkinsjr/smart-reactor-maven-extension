## Copyright (C) 2016 Ronald Jack Jenkins Jr.
## 
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
## 
## http://www.apache.org/licenses/LICENSE-2.0
## 
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
#set($h1 = '#')
#set($h2 = '##')
#set($h3 = '###')
#set($h4 = '####')
#set($h5 = '#####')
#set($h6 = '######')
Why Use The Smart Reactor?
---

The Smart Reactor Maven Extension is a simple addition to any Maven build/installation that solves many challenges with two behavioral changes - *only build SNAPSHOTs* and *perform release transformation before the build begins*. This page walks through how this extension came to be, and describes how it solves common difficulties using Maven.

$h3 Maven Imposes Needless Building and Versioning of Modules

While working on a fork of [dhutils](https://github.com/desht/dhutils), I noticed that the root POM, as well as the "API" and "Fallback" modules, had dummy `<version>` values. It appears that the original author's motivation was to avoid having to re-version these infrequently-changing and non-public-facing modules.

I believe that *every* Maven module should have a version that concisely communicates how the code's API has evolved as a result of the changes introduced into the code. I think this holds true even if a module exists solely to facilitate the development of other modules in the same codebase, which is clearly the case with the **dhutils** codebase.

The problem is that Maven's history of selective module builds has been sketchy at best. The [Maven Reactor Plugin](http://maven.apache.org/plugins/maven-reactor-plugin/) has been retired for quite some time, and Maven's [advanced reactor options](http://blog.sonatype.com/2009/10/maven-tips-and-tricks-advanced-reactor-options/#.VrPrSdw4FD8) require one to specify which projects should be built. The latter fact prevents Maven from making any sort of selective module building behavior as its default behavior. When one considers that Maven's concept of SNAPSHOTs concisely identifies "code under development", the simplest default behavior would be to select every SNAPSHOT project in the reactor.

While some people might try to engineer a CLI-based solution to this problem, most people will follow the path of least resistance and just re-release unchanged modules...but this violates common sense - *if you didn't make any changes, why bump the version number?* Bumping the version number with no changes actually violates [SemVer](http://semver.org/) as well.

Maven's repository design also betrays the approach of rebuilding an entire multi-module project. Once a Maven project gets installed/deployed to a local/remote repository, it is accessible as a `<dependency>` for anyone, even if it's intended only as a `<dependency>` for another module in the same reactor. Because previously-released modules can be found in a remote repository, it makes perfect sense for those modules to be reused by the same project from which they originated - the alternative is to rebuild code that has not changed, which wastes time and opens the door for human error.

$h3 Maven Releases Have an Unhealthy Obsession With SCM

When using Jenkins CI, SCM credentials are managed by the Jenkins Credentials Plugin. When performing a Maven release using the Maven Release Plugin, however, the command-line SCM client also needs credentials for authentication. There are several ways to do this:

1. Use the [Jenkins Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin) to pass the credentials to the command line.
1. Cache the credentials with the SCM client before attempting any releases.
1. [Modify settings.xml and pom.xml to hardcode credentials inside settings.xml.](http://maven.apache.org/maven-release/maven-release-plugin/faq.html#credentials)

These solutions are either [not DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself), not secure or unacceptably brittle. These challenges exist regardless of which CI engine is used, because the demands of the Maven Release Plugin do not change.

The real issue is that *it doesn't make sense for a build tool to be performing SCM activity when CI engines are already performing SCM activity*. If any commits need to be pushed back to SCM, the CI engine should be responsible for that. Adopting this approach simplifies the management of SCM credentials, as well as the architecture of the software development pipeline.

$h3 Maven Releases Are Clunky and Inelegant

Because it is impractical for a plugin to modify the reactor - the build has already started by the time any plugin is being invoked - the only option that the Maven Release Plugin has is to trigger *another* build after it has transformed the POM files. This has two negative effects:

1. The command line for performing a Maven release build is wildly different from a non-release build.
1. The *true* release build - the build-within-a-build - isn't readily visible from the perspective of outside tools that might inspect Maven's behavior, such as the [Jenkins Artifactory Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Artifactory+Plugin).

$h3 Maven Releases Are Non-Atomic, and Failures Have Side Effects 

Another strange design choice in the Maven Release Plugin is the tagging of code in SCM *prior to* the release build occurring. This is undesirable for two reasons:

1. Releases in Maven now require *two* commands - `mvn release:prepare` and `mvn release:perform`.
1. Code is tagged in SCM as being "released" *even if the release build fails*, which again defies common sense. `mvn release:rollback` exists to handle release build failures, but [it doesn't actually rollback the created tag in SCM, and no one seems interested in fixing this functionality gap](https://issues.apache.org/jira/browse/MRELEASE-229), forcing manual SCM cleanup in the event of a release failure.

$h3 Putting It All Together

Maven doesn't have any default behavior for building only what has changed in a multi-module project. This makes code management of such projects difficult. The two primary solutions are:

1. Re-release unmodified modules with new version numbers, which violates common sense and SemVer.
1. Move infrequently-changed modules out of the multi-module project into their own SCM repository, which can cause unnecessary proliferation of repositories and makes the remainder of the multi-module project harder to understand, because the relationships between the "modules" now extend outside the boundaries of the multi-module project.

Additionally, the design of the Maven Release Plugin is highly presumptuous - it implies that Maven is the "center of the universe" in the software development pipeline. The reality is that Maven is far too deep in the build process to be orchestrating the entire process. It also precludes the possibility of augmenting the behavior of a release build *outside* the context of Maven, which may be impractical or impossible depending on what types of testing have to be performed during a release build.

The Smart Reactor Maven Extension solves all of these issues in two ways:

1. Once the reactor is assembled, all non-SNAPSHOT projects in the reactor are dropped. Only SNAPSHOTs are built, because only SNAPSHOTs are considered "under development" in the Maven ecosystem.
1. The release POM transformation process occurs after all non-SNAPSHOTs are dropped from the reactor, but before the build begins.

These two simple changes have multiple benefits:

1. Only code that is being changed gets built, which reduces build/test times and prevents unnecessary version churn.
1. Previously-released modules that are not being changed are sourced from the remote repository, reducing variability in the build process.
1. Releases no longer attempt to interact with SCM, leaving that responsibility to CI or other better-suited tools in the pipeline.
1. Because the extension is *not* a plugin, there are no special goals to invoke. The command line used for SNAPSHOT builds can now be identical to the command line used for release builds.
1. The release build is the same Maven execution as any non-release build, making integrations between Maven and other tools easier.
1. Release failures no longer have any side effects, except the creation of backup POMs during transformation.
    + When using a CI engine to perform releases, the backup POMs likely do not need to be restored because the code will be checked out again from SCM. Regardless, as a matter of completeness, the backup POMs are restored on disk if the release fails.