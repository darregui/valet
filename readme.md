Valet is a simple Java API to interact AWS's [Route53](http://aws.amazon.com/route53/) DNS service.

The AWS Java SDK does not currently support Route53; this simple binding may help if you want to use Java to modify Route53 zone data.

View [ValetExample](https://github.com/Widen/valet/blob/master/src/main/java/com/widen/valet/examples/ValetExample.java) for typical usage of Valet API.

Includes [automation classes](https://github.com/Widen/valet/blob/master/src/main/java/com/widen/valet/importer) to import/one-way-sync existing Windows DNS server files.

Eclipse users use `gradle eclipse` to build `.classpath`.

IntelliJ users use `gradle idea` to build `.iml` file.