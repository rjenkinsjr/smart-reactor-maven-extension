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
${project.description}
---

Want better Maven builds and releases? Just do one of the following:

+ [Download the extension](${libext.download.url}) into your `$M2_HOME/lib/ext` directory.
+ Create the file `.mvn/extensions.xml` parallel to your project's top-level POM, with the following content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
  </extension>
</extensions>
```
Why bother? Because you can probably identify with one or more of these Maven issues...

$h3 Didn't change it? *Don't build it!*

+ Modules in the reactor whose versions are non-SNAPSHOTs are dropped; only your SNAPSHOT-versioned modules are built. This forces your build to retrieve your previously-built module artifacts from your remote Maven repository. **You properly reuse the code that you've already released!**
+ The build will fail-fast if a non-SNAPSHOT module in the reactor depends upon another reactor module that is a SNAPSHOT. This forces the upstream modules to be re-versioned when you alter downstream modules. **No more forgetting which modules are impacted!**

$h3 Perform *atomic* releases on *your* terms!

+ POM transformation now occurs when the reactor is built, at the very beginning of the build. It uses the same transformation logic as the Maven Release Plugin, just without all of the committing/tagging in SCM (but the `<scm>` info still gets transformed). **Let your development workflow decide when and how your release POMs get committed!**
+ No more build-within-a-build - once the POMs are transformed, the reactor is reconstructed from the release POMs. This lets you perform release builds with the exact same command as your non-release builds (if that's what you want), and makes Maven release builds more compatible with external tools. **Leave those redundant builds behind!**
+ Because releases are just a special kind of build, they too only include SNAPSHOT modules in the reactor. As a result, only the modules that you changed get released. **Stop chewing up version numbers for unchanged modules!**
+ If the release build fails, the backup POM files are automatically restored on disk. **Make POM juggling a thing of the past!**

$h3 Encourage better versioning habits!

+ After performing a Smart Reactor release and committing the subsequent changes back to SCM, all of your module POMs will be non-SNAPSHOTs. The Smart Reactor won't build a project unless it can find a valid collection of SNAPSHOT modules in the reactor. **Never accidentally re-release a version again!**
+ The Smart Reactor will build any SNAPSHOT module, regardless of what its version number actually is. This lets you control the version numbers of your modules at whatever granularity level you choose, because setting version numbers isn't part of the release process anymore. You can use manual POM edits, `mvn versions:set` or whatever works for you. **Gain complete control over your module versions!**

Requirements
---

The extension requires Maven 3.3.1 or higher; any Maven project compatible with Maven 3.3.1 or higher will work with the extension.

Installation and Usage
---

Either drop the [extension JAR file](${libext.download.url}) into your Maven installation's `lib/ext` directory, or declare it in your project's `.mvn/extensions.xml` file. **Do not declare the extension in your POM; it won't work correctly.**

The Smart Reactor is enabled by default. To disable it, enable the property [rtr.disabled](param.html#rtr.disabled). 

License
---

The extension is licensed under the Apache License, Version 2.0.

The extension integrates source code fragments from the Maven Release Manager and the Maven Release Plugin, both of which are also released under the Apache License, Version 2.0. See the `LICENSE` and `THIRDPARTY` files in the JAR file or source repository for more information.
