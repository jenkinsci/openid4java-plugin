package hudson.plugins.openid4java;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class OpenId4JavaPluginTest {

    @Test
    void pluginAndBundledClassesAreAvailable(JenkinsRule j) throws Exception {
        assertNotNull(j.jenkins.getPlugin("openid4java"));

        assertNotNull(Class.forName("org.openid4java.consumer.ConsumerManager"));
        assertNotNull(Class.forName("org.openid4java.discovery.DiscoveryInformation"));
        assertNotNull(Class.forName("org.openid4java.util.HttpFetcherFactory"));
        assertNotNull(Class.forName("org.openid4java.shaded.apache.hc.client5.http.impl.classic.CloseableHttpClient"));
        assertNotNull(Class.forName("org.openid4java.shaded.google.inject.Guice"));
        assertNotNull(Class.forName("com.cloudbees.openid4java.team.TeamExtensionFactory"));
    }
}
