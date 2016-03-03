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
${project.name}
===
${project.description}
---
Want better Maven builds and releases? Just do one of the following:

+ [Download the extension](${libext.download.url}) into your `M2_HOME/lib/ext` directory.
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

[Read the official docs for more info.](${project.url})
