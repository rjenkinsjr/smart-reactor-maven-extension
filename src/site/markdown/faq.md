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
Frequently Asked Questions
---

$h4 Where are the goals documented?

There are no goals; this is an extension, not a plugin. It is enabled by default once it is installed or included in your project. [Parameters](param.html) are used to control how the extension behaves.

$h4 My project is just a single POM that I use as a parent for many unrelated projects, so it's in a separate SCM repository. When I try to use this extension with my parent POM project, I get an error. What's wrong?

By default, the Smart Reactor will fail if it contains exactly one SNAPSHOT project whose packaging is `pom`. This check exists to prevent pointless builds of aggregator POMs, usually caused by forgetting to version one or more modules. When developing a standalone parent POM, however, this check prevents the extension from otherwise working as intended. To disable this check, enable the property [rtr.allowSinglePomReactor](param.html#rtr.allowSinglePomReactor) in the POM (recommended) or at the command line. 

$h4 How do I perform a release?

Enable the [rtr.release](param.html#rtr.release) parameter. If you already have a profile in your project dedicated to releases, just add this parameter as a profile-specific property.

1. All SNAPSHOT projects that are selected for release will have their POMs transformed on disk.
1. The transformed POMs are reloaded into the reactor.
1. The build is performed, using whatever other parameters/profiles/goals/phases you have specified.

$h4 How do I get my release POMs checked back into my SCM tool?

The extension will not push changes back into SCM; your CI engine is ideal for this. Users of Git should find this trivial to implement regardless of the CI engine being used.

$h4 You assert that one of the downsides of the Maven Release Plugin is that a failed release results in an SCM tag that must be manually rolled back...but if your CI release build fails before creating the SCM tag and after pushing the release artifacts to the Maven repository, now you have released artifacts that must be manually rolled back. Haven't I just traded one problem for another?

Technically speaking - yes, you've traded the "dangling SCM tag" problem for the "dangling release artifacts" problem. There will always be some breakdown of atomicity when integrating many disparate tools to build and release Maven projects, and this is a good example of what can happen as a result. With that said:

1. It is unlikely that your CI build would be able to check out code from SCM, build a Maven project and push to the Maven repository, but then fail when committing/tagging back to SCM. You've already checked out from SCM, so clearly your SCM system is working at the time the build starts. It's always possible that your SCM system could crash while your build is working, but that window of opportunity is only as large as the duration of your CI build (which should be small).
1. If you have post-Maven-deploy verification steps in your CI build that cannot be performed prior to the Maven build (e.g. SonarQube analysis), you have the same atomicity problem regardless of whether you used the Smart Reactor or the Maven Release Plugin. In fact, this problem would be worse with the Maven Release Plugin because you would have to roll back the Maven artifacts *and* the SCM tag, whereas with the Smart Reactor you would only have to roll back the Maven artifacts.
1. Rollback of improperly-released Maven artifacts could be mitigated by deploying to a "staging" Maven repository from your CI build, or by scripting a rollback procedure in the event of a CI build failure. Nexus, Artifactory and Archiva all have REST APIs for deleting artifacts.

$h4 Suppose I perform a release and commit my release POMs to trunk/master/[insert name of my SCM tool's concept of mainline here]. How can I perform post-release analytics (e.g. SonarQube analysis) on the code that was released, when the Smart Reactor refuses to build already-released code?

[Disable the Smart Reactor](param.html#rtr.disabled) when performing builds on already-released projects.

Make sure that you prevent these analytics from accidentally publishing to your Maven repository. Running `mvn verify` instead of `mvn install` is a good place to start.

$h4 What does "rtr" mean?

The first name I came up with for this extension was "Selective Release", or "selrel" for short. As I started developing it, I realized that the logic impacted both release and non-release builds. The name "Selective Build and Release" was too verbose, so I had to come up with something else. When I realized that I was making a "smarter" Maven reactor, I settled on the name "Smart Reactor". When you put the two words together, you get "smaRTReactor".
