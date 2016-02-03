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
if (!buildLog.contains('[ERROR] Smart Reactor sanity check failure: Reactor contains a single POM-packaging project, which is not allowed. If this is intended, set property "rtr.allowSinglePomReactor" to true. -> [Help 1]')) {
  throw new IllegalStateException('Build failed, but single POM-packaging reactor was permitted.')
}

return true
