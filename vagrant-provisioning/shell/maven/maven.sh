#!/usr/bin/env bash

wget -q http://ftp.unicamp.br/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
sudo tar -zxf apache-maven-3.3.9-bin.tar.gz -C /usr/local
sudo ln -s /usr/local/apache-maven-3.3.9/ /usr/local/maven

rm apache-maven-3.3.9-bin.tar.gz

sudo echo "export M2_HOME=/usr/local/maven" >> /home/vagrant/.bash_profile
sudo echo "export PATH=/usr/local/maven/bin:${PATH}" >> /home/vagrant/.bash_profile
