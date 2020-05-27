#!/bin/bash
[ `whoami` = root ] || { sudo -s "$0" "$@"; exit $?; }

echo "Upgrading server for Commando installation"

service commando stop

rm -rf /opt/commando/lib/commando-1.0.1.jar
wget https://raw.githubusercontent.com/theenergydetective/commando/master/builds/commando-1.0.1.jar -O /opt/commando/lib/commando-1.0.1.jar
echo "USE_START_STOP_DAEMON=false" > /opt/commando/lib/commando-1.0.conf
chmod 755 /opt/commando/lib/commando-1.0.1.jar

rm -rf /etc/init.d/commando
ln -s /opt/commando/lib/commando-1.0.1.jar /etc/init.d/commando
/bin/systemctl daemon-reload
update-rc.d commando defaults

service commando restart
