Valet is a simple Java API to interact AWS's [Route53](http://aws.amazon.com/route53/) DNS service.

The AWS Java SDK does not currently support Route53; this simple binding may help if you want to use Java to modify Route53 zone data.

View [ValetExample](https://github.com/Widen/valet/blob/master/src/main/java/com/widen/valet/examples/ValetExample.java) for typical usage of Valet API.

Includes [automation classes](https://github.com/Widen/valet/blob/master/src/main/java/com/widen/valet/importer) to import/one-way-sync existing Windows DNS server files.

Available via Maven:

      server: http://widen.artifactoryonline.com/widen/libs-widen-public
       group: widen
    artifact: valet
     version: 0.1

Or browse the repo directly at [https://widen.artifactoryonline.com/widen/libs-widen-public](https://widen.artifactoryonline.com/widen/libs-widen-public/widen/valet/)

For new Route53 feature support, view the `develop` branch.

Contact Uriah Carpenter (uriah at widen.com) with questions.

Licensed under Apache, Version 2.0.