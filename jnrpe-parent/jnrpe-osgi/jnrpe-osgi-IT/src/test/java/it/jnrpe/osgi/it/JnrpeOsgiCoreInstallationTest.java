package it.jnrpe.osgi.it;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import it.jnrpe.ReturnValue;
import it.jnrpe.client.JNRPEClient;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.google.common.io.Files;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JnrpeOsgiCoreInstallationTest {

	@Inject
	BundleContext context;

	private File confDir;

	private Map<String, String> getInitialConfiguration() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("bind_address", "127.0.0.1:5666");
		res.put("allow_address", "127.0.0.1");
		res.put("command.check_test", "TEST -t $ARG1$");

		return res;
	}

	/**
	 * Creates the JNRPE osgi config file (it.jnrpe.osgi.cfg( inside the
	 * specified directory )
	 * 
	 * @param confDir
	 */
	private void createConfFile(File confDir, Map<String, String> conf)
			throws Exception {

		// Logger root = (Logger)
		// LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		// root.setLevel(Level.ALL);

		Logger log = (Logger) LoggerFactory.getLogger("it.jnrpe.osgi");
		log.setLevel(Level.ALL);

		File confFile = new File(confDir, "it.jnrpe.osgi.cfg");
		confFile.deleteOnExit();

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(confFile);

			for (Entry<String, String> entry : conf.entrySet()) {
				pw.println(entry.getKey() + "=" + entry.getValue());
			}
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private Option[] felix() {
		return options(
				systemProperty("osgi.console").value("6666"),
				systemProperty("felix.fileinstall.dir").value(
						confDir.getAbsolutePath()),
				systemProperty("felix.fileinstall.filter").value(".*\\.cfg"),
				systemProperty("felix.fileinstall.poll").value("1000"),
				systemProperty("felix.fileinstall.noInitialDelay")
						.value("true"),
				mavenBundle("net.sf.jnrpe", "jnrpe-plugins-osgi",
						"2.0.4-SNAPSHOT").startLevel(2),

				mavenBundle("net.sf.jnrpe", "jnrpe-osgi-core", "2.0.3"),

				mavenBundle("org.slf4j", "slf4j-api"),
				mavenBundle("ch.qos.logback", "logback-core"),
				mavenBundle("ch.qos.logback", "logback-classic"),

				// jcheck_nrpe dependencies...
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jcheck_nrpe",
						"2.0.3")),
				wrappedBundle(mavenBundle("commons-lang", "commons-lang", "2.6")),
				wrappedBundle(mavenBundle("org.apache.mahout.commons",
						"commons-cli", "2.0-mahout")),
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jnrpe-lib", "2.0.3")),
				// end of jcheck_nrpe dependencies

				junitBundles(),

				mavenBundle("org.apache.felix", "org.apache.felix.configadmin",
						"1.8.0").startLevel(1),
				mavenBundle("org.apache.felix", "org.apache.felix.fileinstall",
						"3.2.8").startLevel(2)

		);
	}

	private Option[] equinox() {
		return options(
				systemProperty("osgi.console").value("6666"),
				mavenBundle("net.sf.jnrpe", "jnrpe-plugins-osgi",
						"2.0.4-SNAPSHOT").startLevel(2),

				mavenBundle("net.sf.jnrpe", "jnrpe-osgi-core", "2.0.4-SNAPSHOT"),

				mavenBundle("org.slf4j", "slf4j-api"),
				mavenBundle("ch.qos.logback", "logback-core"),
				mavenBundle("ch.qos.logback", "logback-classic"),

				// jcheck_nrpe dependencies...
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jcheck_nrpe",
						"2.0.3")),
				wrappedBundle(mavenBundle("commons-lang", "commons-lang", "2.6")),
				wrappedBundle(mavenBundle("org.apache.mahout.commons",
						"commons-cli", "2.0-mahout")),
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jnrpe-lib", "2.0.3")),
				// end of jcheck_nrpe dependencies

				junitBundles());
	}

	@Configuration
	public Option[] config() throws Exception {

		confDir = Files.createTempDir();

		createConfFile(confDir, getInitialConfiguration());

		return felix();
	}

	@Test
	public void checkInject() {
		Assert.assertNotNull(context);
	}

	@Test
	public void checkReloadConfiguration() throws Exception {
		Map<String, String> conf = getInitialConfiguration();
		conf.put("allow_address", "127.0.0.1,10.10.10.1");
		createConfFile(confDir, conf);

		// Wait for JNRPE to reload the configuration...
		Thread.sleep(3000);

		conf.put("allow_address", "127.0.0.1,10.10.10.1");
		createConfFile(confDir, conf);

		// Wait for JNRPE to reload the configuration...
		Thread.sleep(3000);

		Assert.assertTrue("Configuration reload failed...",
				isActive("jnrpe-osgi-core"));
	}

	/**
	 * This test checks that the plugins bundle has been correctly loaded. To
	 * perform the test, the chec_test command is invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkPluginsLoaded() throws Exception {

		JNRPEClient client = new JNRPEClient("127.0.0.1", 5666, false);
		ReturnValue rv = client.sendCommand("check_test", "OSGI test");

		Assert.assertEquals("TEST : OSGI test", rv.getMessage());
	}

	private boolean isActive(String bundleName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle != null) {
				if (bundle.getSymbolicName().equals(bundleName)) {
					if (bundle.getState() == Bundle.ACTIVE) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private void checkBundle(String bundleName) {
		Boolean found = false;
		Boolean active = false;
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle != null) {
				if (bundle.getSymbolicName().equals(bundleName)) {
					found = true;
					if (bundle.getState() == Bundle.ACTIVE) {
						active = true;
					}
				}
			}
		}
		Assert.assertTrue(bundleName + " not found", found);
		Assert.assertTrue(bundleName + " not active", active);
	}

	/**
	 * This test checksthat the core bundle is active.
	 */
	@Test
	public void checkJnrpeOsgiCoreBundle() {
		checkBundle("jnrpe-osgi-core");
	}

	/**
	 * This test checks that the plugin bundle is active.
	 */
	@Test
	public void checkJnrpePluginsOsgiBundle() {
		checkBundle("jnrpe-plugins-osgi");
	}

}