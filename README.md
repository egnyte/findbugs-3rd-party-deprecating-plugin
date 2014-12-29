# Findbugs 3rd-party deprecating plugin Readme #

## Description ##

This is a findbugs plugin, which provides third-party classes and methods deprecation. Deprecated classes and methods can be simple defined by an user, so it can be tailored to your needs. The plugin can be also added as a new rule in your Sonar.

## Why you should use our plugin ##

* you can mark 3rd party classes or methods as deprecated, which is not possible using @Deprecated annotation,
* the list with deprecated classes or methods can be configured by the user using an intuitive format; additionally, the user of the plugin can define the reason of method or class deprecation, so an information about deprecated classes or methods may propagates between programmers,
* usage of deprecation is marked as an issue in Sonar, so it is always visible that someone has used some deprecated class or method and it does not disappear in the code.

## Installation ##

To install the plugin in your Sonar you need to put following files into Sonar `extensions` directory (`/extensions/rules/findbugs` for 3.7.x Sonar versions):
* jar file with this plugin, built with Maven,
* rules.xml file attached to this repository,
* and deprecated-list.txt file which contains list of deprecated classes or methods (you can find sample deprecated-list.txt file in this repo or read more in [deprecated list format](#deprecated-list-format) section)

You should remember to activate rules in the quality profile used for your project. To do that, click on Quality Profiles in Sonar main menu and then search for following rules by key:
* DEPRECATED_3RD_PARTY_CLASS - rule for class deprecation,
* DEPRECATED_3RD_PARTY_METHOD - rule for methods and constructors deprecation.

Default severity of these rules is critical, so if you want to change that, you can configure it also in Quality Profiles page.

## Features ##

Our plugin provides methods, constructors, and whole classes deprecation. Furthermore, you can deprecate methods with specified list of parameters. The plugin also supports wildcard parameters matching.

## Deprecated list format ##

To deprecate any usage of specified class, the following format is supported:

```
class [package.name].[ClassName] // the reason of [package.name].[ClassName] deprecation
```

for instance:
```
class org.xml.sax.HandlerBase // This class works with the deprecated DocumentHandler interface. It has been replaced by the SAX2 DefaultHandler class.
```

To deprecate any usage of method or constructor, you should use following format:
```
class [package.name].[ClassName] {
	...
	[methodName or ConstructorName]([parameters]) // the reason of [package.name].[ClassName].[methodName or ConstructorName]([parameters]) method deprecation
	...
}
```
The clause `class [package.name].[ClassName] { ... }` represents methods block definition and you can include in it any number of methods definition.
 
You can deprecate methods or constructors with specified parameters, for instance:
```
class javax.management.MBeanServer {
	deserialize(String, byte[]) // Use MBeanServer.getClassLoaderRepository() to obtain the class loader repository and use it to deserialize.
}
class java.lang.Thread {
	countStackFrames() // The definition of this call depends on Thread.suspend(), which is deprecated. Further, the results of this call were never well-defined.
	stop() // This method is inherently unsafe.
}
class java.sql.Date {
	Date(int, int, int) // Instead use the constructor Date(long date)
}
```

You can also deprecate methods or constructors with any parameter:
```
class java.awt.List {
	addItem(...) // Replaced by add methods
}
```
Please make sure to keep new lines in a similar way to described in above examples.

## Creators ##

* Jakub Wąsikowski (https://github.com/Mostesz)
* Jakub Bocheński (https://github.com/jakub-bochenski)

## Licence ##
Code and documentation copyright 2014 Egnyte, Inc. Code released under the MIT license.
[FindBugs4Deprecating3rdParty](https://github.com/Grundlefleck/FindBugs4Deprecating3rdParty) was the inspiration for this project.
