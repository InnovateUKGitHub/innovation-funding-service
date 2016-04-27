#!/usr/bin/env bash

#if [! -f /vagrant/data/install.txt ];
#then

echo "# Updating package repository"
sudo apt-get -y update

echo "# Installing xvfb"
sudo apt-get -y install xvfb

echo "# Installing python-wxgtk2.8"
sudo apt-get -y install python-wxgtk2.8

echo "# Installing python-pip"
sudo apt-get -y install python-pip

echo "# Installing robotframework"
sudo pip install robotframework

echo "# Installing robotframework-selenium2library"
sudo pip install robotframework-selenium2library

echo "# Installing robotframework-ride"
sudo pip install robotframework-ride

echo "# Installing terminal"
sudo apt-get -y install gnome-terminal

echo "# Installing firefox"
sudo apt-get -y install firefox

echo "# Installing xvfb library"
pip install robotframework-xvfb

echo "# Installing ImapLibrary"
pip install robotframework-imaplibrary

echo "# Installing openpyxl"
pip install openpyxl

echo "# Installing excellibrary"
pip install robotframework-excellibrary

echo "# Installing ftplibrary"
pip install robotframework-ftplibrary

#fi

