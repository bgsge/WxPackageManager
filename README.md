## WxPackageManager

webMethods package to provide an online central registry for looking up packages and obtaining the necessary git repository info in order to download the package locally.

The package provides a portal to allow packages to be browsed, registered and downloaded here

http://localhost:5555/WxPackageManager

The package also provides an API to implement remote use for third party client tools such as the webMethods wpm cli command.

e.g.

List available packages (in default registry)
```
$ curl "http://localhost:5555/rad/wx.packages:manager/packages?registry=default" \
 -H 'Accept: application/json'
 {"packages":[{"name":"JcPublicTools","category":"tools","description":"Bunch of utility services for general use when building webMethods Integrations","registeredDate":"24/01/2022","searchTags":["\"utility\"","\"general\""],"totalDownloads":"0"}]}
 ```
 
* Get download information for a package
 ```
 curl "http://localhost:5555/rad/wx.packages:manager/package/JcPublicTools" \
  -H 'Accept: application/json'
 {"sourceUrl":"https://github.com/johnpcarter/JcPublicTools.git","sourceUserId":null,"sourceToken":null,"trustLevel":null,"isSigned":false,"reason":null,"isValid":false}         
 ```
 
Full API documentation is available here online 
 
http://localhost:5555/WxPackageManager/api

Usage guide here

http://localhost:5555/WxPackageManager/help
 
## Set up
Simply install the package (WxPackageManager) into your webMethods Integration Server. 
This package also requires the package JcPublicTools
e.g.
 
```
 $ cd ${SAG_HOME}/IntegrationServer/instances/default/packages
 $ git clone https://github.com/johnpcarter/JcPublicTools.git
 $ git clone https://github.com/johnpcarter/WxPackageManager.git
```
 
 Then restart your server.
 
# Database setup
 
You will need to provide a mysql database and the configure the connection parameters in your webMethods runtime. You can do this after starting up your server via the adapters -> jdbc adapter -> connections -> 'wx.packages.manager._priv.jdbc:conn'

Alternatively you can copy the connection properties file application.properties in the packages WxPackageManager resources directory to the root folder or your Integration Server/MSR and edit the connection properties to point to your database before starting up the server.
 
First, create the database and default user e.g.
```
 CREATE DATABASE wxpm;
 USE wxpm;

 CREATE USER 'wxpm'@'%' IDENTIFIED BY 'manage';
 GRANT ALL PRIVILEGES ON *.* TO 'wxpm'@'%';
 GRANT SUPER ON *.* TO 'wxpm'@'%';
  ```
 
 You will have to reload the package after setting the database connection if the server is already started. The necessary database tables will then be created dynamically when the package starts. A default public registry is also created automatically.
 