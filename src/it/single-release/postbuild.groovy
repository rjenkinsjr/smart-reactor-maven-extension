// Copyright (C) 2016 Ronald Jack Jenkins Jr.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
def buildLog = new java.io.File(basedir, 'build.log').readLines('UTF-8')

if (!buildLog.contains('[INFO] Assembling smart reactor...')) {
  throw new IllegalStateException('Extension was not executed.')
}
def topLevelRegex = ~/\[ERROR\] Top-level project MavenProject: it:single-release:0\.1\.0 @ .+ is not a SNAPSHOT\./
def topLevelNonSnapshotWasPermitted = true
buildLog.each {
  if (topLevelNonSnapshotWasPermitted) {
    topLevelNonSnapshotWasPermitted = !(it =~ topLevelRegex)
  }
}
if (topLevelNonSnapshotWasPermitted) {
  throw new IllegalStateException('Build failed, but top-level non-SNAPSHOT project was permitted.')
}

return true
