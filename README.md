#Smart Reactor Maven Extension
##The Maven reactor, reimagined!
Want better Maven builds and releases? Just do one of the following:

+ [Download the extension](http://repo1.maven.org/maven2/info/ronjenkins/smart-reactor-maven-extension/0.1.1/smart-reactor-maven-extension-0.1.1-libext.jar) into your `$M2_HOME/lib/ext` directory.
+ Create the file `.mvn/extensions.xml` parallel to your project's top-level POM, with the following content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>info.ronjenkins</groupId>
    <artifactId>smart-reactor-maven-extension</artifactId>
    <version>0.1.1</version>
  </extension>
</extensions>
```

[Read the official docs for more info.](http://rjenkinsjr.github.io/smart-reactor-maven-extension)
