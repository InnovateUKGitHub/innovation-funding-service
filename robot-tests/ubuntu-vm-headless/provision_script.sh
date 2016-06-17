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

echo "# Installing version 2.53.0 of selenium"
sudo pip install selenium==2.53.0

echo "# Installing robotframework-selenium2library version 1.7.4"
sudo pip install robotframework-selenium2library==1.7.4

echo "# Installing robotframework-ride"
sudo pip install robotframework-ride

echo "# Installing terminal"
sudo apt-get -y install gnome-terminal

echo "# Installing firefox version build_46.0.1-0ubuntu1_amd64"
wget http://vorboss.dl.sourceforge.net/project/ubuntuzilla/mozilla/apt/pool/main/f/firefox-mozilla-build/firefox-mozilla-build_46.0.1-0ubuntu1_amd64.deb
sudo dpkg -i firefox-mozilla-build_46.0.1-0ubuntu1_amd64.deb

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

