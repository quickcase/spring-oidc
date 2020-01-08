# QuickCase Spring Security
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build status](https://github.com/quickcase/quickcase-spring-security/workflows/Check%20master/badge.svg)](https://github.com/quickcase/quickcase-spring-security/actions)

Specialise Spring Security for needs of QuickCase APIs with implementations for the different IDAM providers supported.

## How to publish

This library is published to QuickCase's Maven repository on AWS S3.

### AWS Credentials

Publishing is done using AWS access key and secret for AWS user `quickcase-maven` associated to profile `qcmaven`.
This profile must be defined in `~/.aws/credentials` as:

```
[qcmaven]
aws_access_key_id     = <key>
aws_secret_access_key = <secret>
```

### Publish

With the AWS profile configured, all artifacts can be published using:
```bash
AWS_PROFILE=qcmaven ./gradlew clean build publish
```
