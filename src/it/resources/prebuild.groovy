import java.io.File;
new AntBuilder().copy(todir: basedir) {
  fileset(dir: itResourcesDir) {
    include(name: ".mvn/*")
  }
}
return true
