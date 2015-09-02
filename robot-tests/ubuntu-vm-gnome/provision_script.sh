#!/usr/bin/env bash

#if [! -f /vagrant/data/install.txt ];
#then
sudo apt-get -y update
sudo apt-get -y install gdm
sudo dpkg-reconfigure gdm
sudo apt-get -y install python-wxgtk2.8
sudo apt-get -y install python-pip
sudo pip install robotframework
sudo pip install robotframework-selenium2library
sudo pip install robotframework-ride
sudo apt-get -y install gnome-terminal
sudo apt-get -y install firefox
sudo reboot

	#fi

