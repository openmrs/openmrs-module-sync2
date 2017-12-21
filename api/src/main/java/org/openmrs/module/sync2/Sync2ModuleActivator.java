/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.sync2;

import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.sync2.api.scheduler.impl.SyncSchedulerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class Sync2ModuleActivator extends BaseModuleActivator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2ModuleActivator.class);

	/**
	 * @see #started()
	 */
	public void started() {
		Context.getRegisteredComponents(SyncSchedulerServiceImpl.class).get(0).runSyncScheduler();
		LOGGER.info("Started Sync2 Module");
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		Context.getRegisteredComponents(SyncSchedulerServiceImpl.class).get(0).shutdownSyncScheduler();
		LOGGER.info("Shutdown Sync2 Module");
	}
	
}
