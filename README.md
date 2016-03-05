<!---
  Copyright (C) 2016 Ronald Jack Jenkins Jr.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
Smart Reactor Maven Extension
===
The Maven reactor, reimagined!
---
Want better Maven builds and releases? Just do one of the following:

+ [Download the extension](http://repo1.maven.org/maven2/info/ronjenkins/smart-reactor-maven-extension/0.1.4/smart-reactor-maven-extension-0.1.4-libext.jar) into your `M2_HOME/lib/ext` directory.
+ Create the file `.mvn/extensions.xml` parallel to your project's top-level POM, with the following content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>info.ronjenkins</groupId>
    <artifactId>smart-reactor-maven-extension</artifactId>
    <version>0.1.4</version>
  </extension>
</extensions>
```

[Read the official docs for more info.](http://rjenkinsjr.github.io/smart-reactor-maven-extension)
