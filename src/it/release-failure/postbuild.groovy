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
  throw new IllegalStateException('Release was not attempted.')
}
if (!buildLog.contains('[INFO] Building release-failure 0.1.0')) {
  throw new IllegalStateException('Project was not transformed correctly.')
}
if (buildLog.contains('[ERROR] Rollback unsuccessful. Check project filesystem for POM backups and other resources that must be rolled back manually.')) {
  throw new IllegalStateException('Rollback failed.')
}
if (new java.io.File(basedir, 'pom.xml.releaseBackup').exists()) {
  throw new IllegalStateException('POM backup still exists on disk.')
}

return true
