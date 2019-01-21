package org.openmrs.module.sync2.config;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.ui.framework.StandardModuleUiConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class Sync2UiConfigurationInventory {

	@Bean
	public StandardModuleUiConfiguration createSync2UiConfigurationBean() {
		StandardModuleUiConfiguration standardModuleUiConfiguration = new StandardModuleUiConfiguration();
		standardModuleUiConfiguration.setModuleId("sync2");
		return standardModuleUiConfiguration;
	}
}
