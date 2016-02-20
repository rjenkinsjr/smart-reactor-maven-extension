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
if (!buildLog.contains('[INFO] Converting reactor projects to releases...')) {
  throw new IllegalStateException('Release was not performed.')
}
if (buildLog.contains('[ERROR] Rollback unsuccessful. Check project filesystem for POM backups and other resources that must be rolled back manually.')) {
  throw new IllegalStateException('Rollback was attempted, but should not have been attempted.')
}
if (!buildLog.contains("[ERROR] Smart Reactor release failure: Can't release project due to non released dependencies :")) {
  throw new IllegalStateException('External SNAPSHOT check failed to find SNAPSHOT dependency.')
}
if (buildLog.contains('[WARNING] External SNAPSHOT artifacts are allowed for this release. Artifacts produced by this build may not behave consistently compared to earlier builds.')) {
  throw new IllegalStateException('Build failed, but external SNAPSHOT check was skipped.')
}

return true
