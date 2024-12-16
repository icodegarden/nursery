package io.github.icodegarden.nursery.springboot;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author Fangfang.Xu
 */
public class NurseryVersion implements Supplier<String> {

	private static final String VERSION_FORMAT = "(v%s)";
	public static final String VERSION = "version";
	public static final String IS_ENTERPRISE = "is-enterprise";
	public static final String FORMATTED_VERSION = "formatted-version";

	public static String key(String name) {
		return "icodegarden.nursery." + name;
	}

	private final String version;
	private final boolean isEnterprise;
	private final String formattedVersion;

	public NurseryVersion() {
		this(NurseryVersion.class.getPackage());
	}

	NurseryVersion(final Package pkg) {
		this.version = Optional.ofNullable(pkg.getImplementationVersion()).map(String::trim).orElse("");
		this.isEnterprise = version.endsWith("-ee");
		this.formattedVersion = String.format(VERSION_FORMAT, version);
	}

	@Override
	public String get() {
		return version;
	}

	public boolean isEnterprise() {
		return isEnterprise;
	}

	public PropertiesPropertySource getPropertiesPropertySource() {
		final Properties props = new Properties();
		props.put(key(VERSION), version);
		props.put(key(IS_ENTERPRISE), isEnterprise);
		props.put(key(FORMATTED_VERSION), formattedVersion);

		return new PropertiesPropertySource(this.getClass().getSimpleName(), props);
	}

}
