# OpenID4Java API plugin

[![Jenkins plugin](https://img.shields.io/jenkins/plugin/v/openid4java.svg)](https://plugins.jenkins.io/openid4java/)

This is a **library plugin**: it provides no user-facing Jenkins functionality
of its own. It packages the
[OpenID4Java](https://github.com/amarula/openid4java) library so that
other Jenkins plugins can share one copy of the classes instead of each
bundling their own. Sharing a single copy avoids duplicate classes and
classloader conflicts between plugins that talk to OpenID 2.0 providers, such
as [jenkinsci/openid-plugin](https://github.com/jenkinsci/openid-plugin).

## Bundled libraries

| Coordinates | Packages | Purpose |
| ----------- | -------- | ------- |
| `com.amarulasolutions:openid4java-shaded:2.0.0` | `org.openid4java.*` | OpenID consumer/provider implementation, with Guice, Guava, HttpClient 5, jsoup and the injection APIs relocated under `org.openid4java.shaded.*` |
| `com.cloudbees:openid4java-team-extension:1.0` | `com.cloudbees.openid4java.team.*` | CloudBees team extension for OpenID |

The shaded JAR contains the OpenID4Java classes and their relocated
third-party dependencies. `commons-codec` is bundled separately;
`commons-logging` and `slf4j-api` are provided by Jenkins. The plugin's
`WEB-INF/lib` contains only the shaded JAR, the team extension,
`commons-codec`, and the plugin's own JAR; `hpi.strictBundledArtifacts` keeps
that set exactly as declared in the POM.

This replaces the previous CloudBees `openid4java-shaded:1.0.0.0` (and the
older `0.9.8.0`). The bundled classes are the Amarula OpenID4Java 2.0.0
modernization: Java 17 bytecode, HttpClient 5, Ehcache 3 and jsoup. The
InfoCard and local XRI modules and the NekoHTML parsers were removed, and the
public HTTP/Ehcache types changed, so consuming Jenkins plugins must be
recompiled and tested against this release. The CloudBees team extension only
uses the stable `org.openid4java.message.*` API and remains compatible.

## Using it from another plugin

Add this plugin as a dependency and pin the version explicitly. It is **not**
managed by the Jenkins plugin BOM:

```xml
<dependency>
  <groupId>org.jenkins-ci.plugins</groupId>
  <artifactId>openid4java</artifactId>
  <version>8.v97c390826b_59</version> <!-- use the latest release -->
</dependency>
```

No `<type>` element is needed. The Maven HPI plugin recognizes the dependency
as a plugin and records it in the consuming plugin's manifest instead of
bundling it:

```
Plugin-Dependencies: ...,openid4java:8.v97c390826b_59
```

At runtime Jenkins installs that version as a plugin dependency and exposes
`org.openid4java.*` and `com.cloudbees.openid4java.team.*` to the consuming
plugin. Do not package another copy of OpenID4Java: this plugin already
excludes the old `org.openid4java:openid4java-nodeps` from the team extension.

The artifacts are hosted on `repo.jenkins-ci.org`. The Jenkins parent POM
declares that repository once it has been resolved, but builds that resolve
the parent with a bare `settings.xml` (for example the CodeQL autobuild used by
the Jenkins Security Scan) also need it declared in the consuming POM:

```xml
<repositories>
  <repository>
    <id>repo.jenkins-ci.org</id>
    <url>https://repo.jenkins-ci.org/public/</url>
  </repository>
</repositories>
```

### Shaded artifact repository

The Amarula shaded artifact is published to GitHub Packages:

```
https://maven.pkg.github.com/amarula/openid4java
```

Clean builds need a `github` server in `~/.m2/settings.xml` with a
`read:packages` token, and the `github-packages` Maven profile declared in the
POM:

```bash
mvn -Pgithub-packages verify
```

For the public Jenkins plugin release, mirror the shaded artifact to Maven
Central or `repo.jenkins-ci.org`; the public Jenkins CI does not have GitHub
Packages credentials.

## Compatibility

| Component | Requirement |
| --------- | ----------- |
| Jenkins | 2.479.3 or newer (baseline 2.479) |
| Bytecode | Java 17 |
| Build JDKs | 17, 21 or 25 (enforced by the parent POM) |
| Maven | 3.9.6 or newer |

## Building and testing

The plugin uses the standard Jenkins plugin parent POM:

```bash
mvn verify
```

If the shaded artifact is not already in the local Maven repository, use
`mvn -Pgithub-packages verify` after configuring the `github` server.

`mvn verify` runs `OpenId4JavaPluginTest`, which starts an in-process Jenkins,
checks that the plugin is loaded and verifies that representative classes from
both bundled libraries are resolvable: `org.openid4java.consumer.ConsumerManager`,
`org.openid4java.discovery.DiscoveryInformation`,
`org.openid4java.util.HttpFetcherFactory`,
`org.openid4java.shaded.apache.hc.client5.http.impl.classic.CloseableHttpClient`,
`org.openid4java.shaded.google.inject.Guice` and
`com.cloudbees.openid4java.team.TeamExtensionFactory`.

## Releasing

The version is derived from Git rather than edited by hand:
`.mvn/maven.config` sets `-Dchangelist.format=%d.v%s`, and the
[git-changelist extension](https://github.com/jenkinsci/incrementals-tools)
expands it to `<number of commits>.v<abbreviated commit hash>`, for example
`8.v97c390826b_59`.

The `cd` workflow deploys the release to
`https://repo.jenkins-ci.org/releases/` with the Jenkins Maven CD action,
creates the matching Git tag and GitHub release, and the Jenkins update center
picks it up from there. Consumers should bump their pinned version and be
tested against the new release before adopting it.

## Continuous integration

- `Jenkins Security Scan` runs CodeQL with the Jenkins query pack on every push
  and pull request.
- `cd` performs continuous delivery as described above.
- `Jenkinsfile` builds on Linux with JDK 25 and on Windows with JDK 21.

## License

MIT. See the POM for details.

The bundled OpenID4Java 2.0.0 is Apache-2.0; its `LICENSE` and `NOTICE` are
included under `META-INF/` in the shaded JAR.
