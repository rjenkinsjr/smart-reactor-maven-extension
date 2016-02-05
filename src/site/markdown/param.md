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

| Name | Type | Since |
| :--- | :--- | :---- |
| [rtr.disabled](#rtr.disabled) | `boolean` | `0.1.0` |
| [rtr.allowSinglePomReactor](#rtr.allowSinglePomReactor) | `boolean` | `0.1.0` |
| [rtr.release](#rtr.release) | `boolean` | `0.1.0` |

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
+ [autoVersionSubmodules](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#autoVersionSubmodules)
+ developmentVersion
+ dryRun
+ [projectVersionPolicyId](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#projectVersionPolicyId)
+ providerImplementations???
+ releaseVersion
+ tag
+ tagBase
+ tagNameFormat
+ [updateDependencies](http://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html#updateDependencies)
