
# TED Commando

TED Commando is a simple web server application to allow TED 5000, TED Home, and TED Pro systems to post energy data that can be exported into simple billing reports. This is intended to allow users to replace the Billing Export functionality of TED Commander by hosting this on their own server. 

## Getting Started

### Hosting Requirements
For cloud hosting the commando server, we suggest using  [Digital Ocean](https://www.digitalocean.com)  as a host.  For most installations, the following [Droplet Configuration](https://www.digitalocean.com/docs/droplets/how-to/create/)  should work: 
 -  Basic Plan  ($5/mo as of May 2020)
 -  1GB/1CPU
 - 25 GB SSD Disk
 - 1000 GB Transfer
 - Operating System: Ubuntu 18.04.xx LTS 
  
Once the droplet is created you should assign a [floating ip](https://www.digitalocean.com/docs/networking/floating-ips/) to the server so that it never changes in case the droplet needs to be recreated. This will pevent 

While the service can be accessed via an IP address, we highly recommend you [assign a domain name ](https://www.digitalocean.com/community/tutorials/how-to-point-to-digitalocean-nameservers-from-common-domain-registrars)to the server.  **This is required if you plan on using HTTPS to access your server.**

We also recommend you configure the droplet to take [Nightly Snapshots](https://www.digitalocean.com/community/tutorials/how-to-use-digitalocean-snapshots-to-automatically-backup-your-droplets)  so the server can be recovered in case something goes wrong.

If setting up a [Firewall](https://www.digitalocean.com/docs/networking/firewalls/), you will need to allow the following ports to pass through: 

 - Port 80: TCP
 - Port: 443: TCP  (*only required if you are setting up https access)*

If possible, we recommend configuring the firewall to restict the IPs only the computers and TED devices that will be accessing the server. If this can't be done, then global access is fine although we do recommend configuring the server for HTTPS/TLS in that case.

##### Other Hosts
Other hosts, such as [Amazon Web Services](https://aws.amazon.com/) , [Microsoft Azure](https://azure.microsoft.com/), or self hosting can also be used. The same hardware/software specifications for Digial Ocean can be used when provisioning with the other services.

### Installation Instructions
*These instructions assume that we are starting with a clean install of Ubuntu 18.04LTS and that no other packages have been installed.* 

To install a basic server (no-HTTPS/TLS), you can run the following commands:

 - **_`wget https://raw.githubusercontent.com/theenergydetective/commando/master/scriups/install.sh`_**
 - **_`chmod 777 install.sh`_**
 - **_`sudo ./install.sh`_**

Once complete, the server will be configured and you will be able to access it via: `http://<ip of server>`

#### TLS Configuration
To configure your server for HTTPS (TLS) we suggest [configuring the server to issue a LetsEncrypt](https://www.digitalocean.com/community/tutorials/how-to-secure-apache-with-let-s-encrypt-on-ubuntu-18-04). 

**NOTE: You must NOT redirect all HTTP traffic to HTTPS. TED systems can only post data over HTTP.**
When running certbot following the instructions in the above guide, be sure to chose option 1 when presented with the following prompt:
```
Output
Please choose whether or not to redirect HTTP traffic to HTTPS, removing HTTP access.
-------------------------------------------------------------------------------
1: No redirect - Make no further changes to the webserver configuration.
2: Redirect - Make all requests redirect to secure HTTPS access. Choose this for
new sites, or if you're confident your site works on HTTPS. You can undo this
change by editing your web server's configuration.
-------------------------------------------------------------------------------
Select the appropriate number [1-2] then [enter] (press 'c' to cancel):
```

#### First Time Running

When you first access the web page, you will be presented with the following screen.

![enter image description here](https://raw.githubusercontent.com/theenergydetective/commando/master/graphics/screenshots/firsttimesetup.png)

For the **Domain**, this can either be the fully qualified domain name (**FQDN**) of the server, or the public IP address if you have a floating IP assigned. If operating the server on a LAN, this can be the LAN IP address of the server.

The **Activation Key** is optional. Specifying this key will allow you to restrict which TED units can post to this server. If it is left blank, any TED device can use 3rd party activation to post to this server.  Please refer to the product manuals for instructions on how to configure the TED device to use 3rd Party posting. 

#### Application Properties

There are several properties that can be overridden from the defaults. **DO THIS AT YOUR OWN RISK.** 
Create or edit the file named 'application.properties'  in the same directory as the  _commando-1.0.jar_  file (/opt/lib/ by default)

#### Database Path (spring.datasource.url)

To change the path of the database, define the  _spring.datasource.url_  property. e.g.  **_`spring.datasource.url=jdbc:h2:file:/opt/data/commando`_**  If running under Windows, the drive letter must be included. e.g.  **_`spring.datasource.url=jdbc:h2:file:C:/opt/data/commando`_**  The default database path is '/opt/data/commando'

#### Default Port (server.port)

To change the default port of the server, use the following option:  **_`server.port=<port>`_**  e.g.  **_`server.port=80`_**  The default port is 8080

#### Backup Directory (spring.datasource.backup.directory)

The server will create a rolling backup of the database every night at 1:00am of the server's local time. To change the location of this directory, use the option:  **_`spring.datasource.backup.directory=<path to database>`_**  
e.g.  **_`spring.datasource.backup.directory=/opt/data/backup`_**

#### Backup Directory (spring.datasource.backup.count)

The server will create a rolling backup of the database every night at 1:00am of the server's local time. To change number of backups it will maintain use the option  **_`spring.datasource.backup.count=<count>`_**  
e.g.  **_`spring.datasource.backup.count=7`_**  By default, 7 backup files will be created.

##### Sample application properties

```
spring.datasource.url=jdbc:h2:file:/opt/data/commando
spring.datasource.backup.path=/opt/data/backup
spring.datasource.backup.count=7
server.port=8080

```
## Developer Notes

### License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

### Prerequisites

-   Nodejs ([https://nodejs.org](https://nodejs.org/)) - This was originally build using 12.16.3LTS but new versions should be fine.
-   Angular CLI ([https://angular.io/guide/setup-local](https://angular.io/guide/setup-local))
-   OpenJDK 11+ ([https://https](https://https/)://openjdk.java.net/)

### Windows Notes

-   If developing on Windows, you must pass the following property as a VM option as H2 requires and absolute path.

**_`-Dspring.datasource.url=jdbc:h2:file:C:/opt/data/commando`_**

-   If using WSL for Angular/Node, then the maven command line must be run from there as well.

### How to Build

-   Clone or download the source code and extract it.
-   Start in the root Commando directory (same directory as pom.xml)
-   Be sure all of the pre-requisits are installed.
-   After node is installed, run  **_`npm install`_**  from the  **_`commando/src/main/resources/frontend/command-app`_**  directory.
-   Run the following from the command prompt:  **_`mvn clean package install -DskipTests`_**