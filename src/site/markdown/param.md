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
Parameters
---

Parameters for the Smart Reactor can be defined as project `<properties>` or as JVM properties with the `-D` command line switch.

$h3 Core Parameters

| Name | Type | Default | Since |
| :--- | :--- | :---- | :---- |
| [rtr.disabled](#rtr.disabled) | `boolean` | `false` | `0.1.0` |
| [rtr.allowSinglePomReactor](#rtr.allowSinglePomReactor) | `boolean` | `false`  | `0.1.0` |
| [rtr.allowExternalSnapshots](#rtr.allowExternalSnapshots) | `boolean` | `false`  | `0.1.0` |
| [rtr.release](#rtr.release) | `boolean` | `false`  | `0.1.0` |

$h4 Parameter Details

$h5 rtr.disabled
Disables the extension, restoring default Maven behavior as if the extension was not installed or declared.

+ **Type**: `boolean`
+ **Since**: `0.1.0`
+ **Required**: `no`
+ **User Property**: `rtr.disabled`
+ **Default**: `false`

$h5 rtr.allowSinglePomReactor
Whether or not to allow a build consisting of exactly one reactor project whose packaging is `pom`.

+ **Type**: `boolean`
+ **Since**: `0.1.0`
+ **Required**: `no`
+ **User Property**: `rtr.allowSinglePomReactor`
+ **Default**: `false`

$h5 rtr.allowExternalSnapshots
Whether or not to allow a release build when the reactor contains any modules that declare any SNAPSHOT dependencies, plugins, parents or reports that are not also in the reactor. Irrelevant if `rtr.release` is `false`.

+ **Type**: `boolean`
+ **Since**: `0.1.0`
+ **Required**: `no`
+ **User Property**: `rtr.allowExternalSnapshots`
+ **Default**: `false`

$h5 rtr.release
Transforms the POMs of SNAPSHOT modules to non-SNAPSHOT versions, then rebuilds the reactor with the release POMs.

+ **Type**: `boolean`
+ **Since**: `0.1.0`
+ **Required**: `no`
+ **User Property**: `rtr.release`
+ **Default**: `false`

$h3 Release Parameters

When release POM transformation is enabled, the following parameters from the `release:prepare` goal of the Maven Release Plugin are supported. Unless otherwise stated, these parameters behave exactly as [documented](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html).

+ [addSchema](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#addSchema)
+ [allowTimestampedSnapshots](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#allowTimestampedSnapshots)
    + According to the source code of the Maven Release Manager, setting `allowTimestampedSnapshots` to `true` actually allows *all* SNAPSHOT artifacts rather than all *timestamped* SNAPSHOT artifacts. This mismatch between documentation and behavior is likely a bug, hence why the `rtr.allowSnapshotDependencies` parameter exists. You should use the `rtr.allowSnapshotDependencies` parameter instead of the `allowTimestampedSnapshots` parameter.
+ [autoVersionSubmodules](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#autoVersionSubmodules)
+ [projectVersionPolicyId](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#projectVersionPolicyId)
+ [releaseVersion](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#releaseVersion)
+ [tag](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#tag)
+ [tagBase](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#tagBase)
+ [tagNameFormat](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#tagNameFormat)
