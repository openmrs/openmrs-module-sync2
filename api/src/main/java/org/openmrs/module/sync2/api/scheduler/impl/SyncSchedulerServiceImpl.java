package org.openmrs.module.sync2.api.scheduler.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service("sync2.syncSchedulerServiceImpl")
public class SyncSchedulerServiceImpl extends BaseOpenmrsService implements SyncSchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSchedulerServiceImpl.class);

    private static final String PULL_TASK_NAME = "Sync 2.0 Pull";
    private static final String PUSH_TASK_NAME = "Sync 2.0 Push";

    private static final String PULL_TASK_CLASS = "org.openmrs.module.sync2.api.scheduler.SyncPullTask";
    private static final String PUSH_TASK_CLASS = "org.openmrs.module.sync2.api.scheduler.SyncPushTask";

    private static final String PULL_TASK_DESCRIPTION = "Task for Sync 2.0 pulling data from parent instance.";
    private static final String PUSH_TASK_DESCRIPTION = "Task for Sync 2.0 pushing data to the parent instance.";

    @Autowired
    private SyncConfigurationService syncConfigurationService;


    public void runSyncScheduler() {
        if (isPullEnabled()) {
            schedulePullTask();
        } else {
            stopSyncTask(PULL_TASK_NAME);
        }

        if (isPushEnabled()) {
            schedulePushTask();
        } else {
            stopSyncTask(PUSH_TASK_NAME);
        }
    }

    private void schedulePullTask() {
        TaskDefinition pullTask = createTask(PULL_TASK_NAME, PULL_TASK_DESCRIPTION, PULL_TASK_CLASS,
                Long.valueOf(getPullIntervalInSeconds()));

        try {
            if (!Context.getSchedulerService().getScheduledTasks().contains(pullTask)) {
                Context.getSchedulerService().scheduleTask(pullTask);
            } else {
                Context.getSchedulerService().rescheduleTask(pullTask);
            }
        } catch(SchedulerException e) {
            LOGGER.error("Error during starting Sync 2.0 Pull task:", e);
        }
    }

    private TaskDefinition schedulePushTask() {
        TaskDefinition pushTask = createTask(PUSH_TASK_NAME, PUSH_TASK_DESCRIPTION, PUSH_TASK_CLASS,
                Long.valueOf(getPushIntervalInSeconds()));

        try {
            if (!Context.getSchedulerService().getScheduledTasks().contains(pushTask)) {
                Context.getSchedulerService().scheduleTask(pushTask);
            } else {
                Context.getSchedulerService().rescheduleTask(pushTask);
            }
            return pushTask;
        } catch(SchedulerException e) {
            LOGGER.error("Error during starting Sync 2.0 Push task:", e);
            return null;
        }
    }

    private boolean isPullEnabled() {
        return syncConfigurationService.getSyncConfiguration().getPull().isEnabled();
    }

    private boolean isPushEnabled() {
        return syncConfigurationService.getSyncConfiguration().getPush().isEnabled();
    }

    private Integer getPullIntervalInSeconds() {
        return syncConfigurationService.getSyncConfiguration().getPull().getSchedule();
    }

    private Integer getPushIntervalInSeconds() {
        return syncConfigurationService.getSyncConfiguration().getPush().getSchedule();
    }

    private TaskDefinition createTask(String taskName, String taskDescription, String taskClass, Long interval) {
        TaskDefinition result = Context.getSchedulerService().getTaskByName(taskName);

        if (result == null) {
            result = new TaskDefinition();
        }

        result.setName(taskName);
        result.setDescription(taskDescription);
        result.setTaskClass(taskClass);
        result.setRepeatInterval(interval);
        result.setStartTime(new Timestamp(System.currentTimeMillis()));
        result.setStartOnStartup(true);

        try {
            Context.getSchedulerService().saveTaskDefinition(result);
        } catch(SyncException e) {
            LOGGER.error("Error during save Sync task definition: ", e);
        }

        return Context.getSchedulerService().getTaskByName(taskName);
    }

    private void stopSyncTask(String taskName) {
        try {
            if (Context.getSchedulerService().getTaskByName(taskName) != null) {
                Context.getSchedulerService().shutdownTask(Context.getSchedulerService().getTaskByName(taskName));
            }
        } catch (SchedulerException e) {
            LOGGER.error("Error during stopping Sync 2.0 task:", e);
        }
    }

}
