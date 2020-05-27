#!/bin/bash
[ `whoami` = root ] || { sudo -s "$0" "$@"; exit $?; }

echo "Setting up server for Commando installation"

#create directories
mkdir -p /opt/commando/{data,data/backup,lib}

#install pre-requisites.
apt-get update
apt-get -y --purge remove nginx
apt-get -y install apache2 openjdk-11-jre-headless dos2unix

#download jar file
rm -rf /opt/commando/lib/*.jar
rm -rf /opt/commando/lib/*.properties
wget https://raw.githubusercontent.com/theenergydetective/commando/master/builds/commando-1.0.jar -O /opt/commando/lib/commando-1.0.jar
echo "USE_START_STOP_DAEMON=false" > /opt/commando/lib/commando-1.0.conf
chmod 755 /opt/commando/lib/commando-1.0.jar

#Set up apache as a proxy
a2enmod proxy
a2enmod proxy_http
a2enmod headers
a2enmod rewrite
rm -rf /etc/apache2/commando.apache.conf
wget https://raw.githubusercontent.com/theenergydetective/commando/master/scripts/commando.apache.conf -O /etc/apache2/conf-enabled/commando.apache.conf

#Set up the keepalive script
wget https://raw.githubusercontent.com/theenergydetective/commando/master/scripts/keep_alive.sh -O /opt/commando/lib/keep_alive.sh
chmod 755 keep_alive.sh
dos2unix keep_alive.sh
sudo crontab -l > /tmp/mycron
grep 'keep_alive' /tmp/mycron || echo '*/5 * * * * /opt/commando/lib/keep_alive.sh' >> /tmp/mycron
sudo crontab /tmp/mycron
rm /tmp/mycron

#restart services
rm -rf /etc/init.d/commando
ln -s /opt/commando/lib/commando-1.0.jar /etc/init.d/commando
/bin/systemctl daemon-reload
update-rc.d commando defaults

service commando restart
service apache2 restart
