# OpenID4Java API plugin

This plugin packages the [openid4java](https://github.com/cloudbees/openid4java-shaded)
library as a shared Jenkins plugin dependency, so that other plugins can use
the same classes without bundling their own copy.

## Bundled artifacts

- `com.cloudbees:openid4java-shaded:1.0.0.0`
- `com.cloudbees:openid4java-team-extension:1.0`

Both are bundled in the plugin's `WEB-INF/lib` and are available to plugins
that declare a dependency on `org.jenkins-ci.plugins:openid4java`.

## Using it from another plugin

Add the dependency to the consuming plugin's `pom.xml`:

```xml
<dependency>
  <groupId>org.jenkins-ci.plugins</groupId>
  <artifactId>openid4java</artifactId>
</dependency>
```

The version is normally supplied by the Jenkins plugin BOM. If you need a
newer release before the BOM is updated, pin it explicitly.

## Building

The plugin is built with the standard Jenkins plugin parent POM:

```bash
mvn verify
```

The build requires Java 17 or newer and Maven 3.9.6 or newer.

## Releasing

Releases are produced with the standard Jenkins plugin release process and
the reusable CD workflow. Consumers (for example
[jenkinsci/openid-plugin](https://github.com/jenkinsci/openid-plugin)) should
be tested against a new release before it is adopted.
