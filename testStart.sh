sudo service tomcat6 stop;
mvn clean package install;
sudo chown ctcudd /usr/share/tomcat6/webapps/share* -R;
ant clean-deploy;
sudo service tomcat6 start;