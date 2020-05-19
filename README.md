# TED Commando
TED Commando is a simple web server application to allow TED 5000, TED Home, and TED Pro systmes to post energy data that can be exported.

## Installation




##Developer Notes

### License

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


###Prerequisites
- Nodejs (https://nodejs.org) - This was originally build using 12.16.3LTS but new versions should be fine.
- Angular CLI (https://angular.io/guide/setup-local)
- OpenJDK 11+ (https://https://openjdk.java.net/)

### Windows Notes

- If developing on Windows, you must pass the following property as a VM option as H2 requires and absolute path.

**_`-Dspring.datasource.url=jdbc:h2:file:C:/opt/data/commando`_**

- If using WSL for Angular/Node, then the maven command line must be run from there as well.
 

### How to Build
- Clone or download the source code and extract it.
- Start in the root Commando directory (same directory as pom.xml)
- Be sure all of the pre-requisits are installed.
- After node is installed, run **_`npm install`_** from the **_`commando/src/main/resources/frontend/command-app`_** directory.
- Run the following from the command prompt: **_` mvn clean package install -DskipTests `_**