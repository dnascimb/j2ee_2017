KSAML
=====
This project contains the endpoints for SAML-related functionality used throughout KDMS. It is based off the following template project, https://github.com/mkyong/spring4-mvc-gradle-annotation-hello-world

### 1. Technologies used
* Required Servlet 3.0+ container, like Tomcat 7 or Jetty 8
* Gradle 2.0
* Spring Spring 4.1.6.RELEASE
* JSTL 1.2
* Logback 1.1.3
* Boostrap 3

### 2. To build this project locally, go to the root of the repository (one level up from ksaml directory) and either run the main project build:
```shell
$ gradle clean build
```
or run just the sub-project build:
```shell
$ gradle :ksaml:clean :ksaml:build
```

### 3. ROOT.xml
This application uses the ROOT.xml file to specify dynamic values for the environment, such as the application install base and the database connection properties. You will need to set these appropriately for your environment when deploying. This information can be obtained from the team, but it is not to be held in this git repository.
* Change the values/paths to fit your local environment in the provided tomcat config example (tomcat_config/Catalina/localhost/ROOT.xml)
* Copy the ROOT.xml to your **$TOMCAT_HOME$/conf/Catalina/localhost** (sometimes $TOMCAT_HOME$/Catalina/localhost) folder
* Restart Tomcat
* Access ```http://localhost:8080/```

### 4. To import this project into Eclipse IDE
```$ gradle eclipse```
* Import into Eclipse via **existing projects into workspace** option.
* Done.


### 5. Logs
This project uses Logback as a logger. If you do nothing, logs will be output to your webserver's logging directory (example: <TOMCAT_HOME>/logs). However, for more specific logging it is recommended that you configure a logback.xml file for use by the application. As mentioned earlier, you must configure a ROOT.xml file. When doing this, pay attention to the value you configure for the "install.folder" property. This value will be the location where the application expects to find some other files it loads at runtime (example: certificates, logging configuration, ect).

If the value of your "install.folder" property is set to "/opt/taurus" then the application will look for a logback.xml file at "/opt/taurus/config/logback.xml". The "config" directory will be created by the application, even if it didn't exist already (you must start tomcat with the install.folder property set first).The contents of this file should be obtained from the *logback-template.xml* file in the ksaml project. 