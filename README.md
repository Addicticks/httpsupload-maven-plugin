# HTTPS Upload Maven Plugin

Uploads file(s) to a remote server using HTTP or HTTPS. Files are uploaded using
`multipart/form-data` encoding. This is what almost all sites that accepts file upload uses.

The plugin is not meant for uploading artifacts to Maven Repositories. Maven does
that brilliantly by itself. The plugin is meant for uploading *any file* (not just project's
artifacts) to *any type of endpoint* that accepts a HTTP/HTTPS file upload.

## Documentation

See [Plugin Documentation](http://addicticks.github.io/httpsupload-maven-plugin/) for more information.


## Availability

The plugin is available from Maven Central.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.addicticks.oss.maven/httpsupload-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.addicticks.oss.maven/httpsupload-maven-plugin "click me for Maven Central details")



### License

Apache License, version 2.0.