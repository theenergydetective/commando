#!/bin/bash

if pgrep -x "java" > /dev/null
then
        echo "Running"
else
        echo "Stopped"
        sudo service commando restart
fi
