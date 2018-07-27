package com.capgemini.rest.scheduler.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.rest.scheduler.job.DependentJobBuilder;
import com.capgemini.rest.scheduler.job.DependentJobDetailImpl;
import com.capgemini.rest.scheduler.job.DependentJobImpl;
import com.capgemini.rest.scheduler.job.ExecutionMode;
import com.capgemini.rest.scheduler.job.ExecutionState;
import com.capgemini.rest.scheduler.properties.JobDetailProperties;
import com.capgemini.rest.scheduler.properties.SchedulerProperties;

@RestController
@RequestMapping(path = "/schedule")
public class SchedulingController {
	private static final Logger logger = LogManager.getLogger(SchedulingController.class);

	private Scheduler scheduler;

	@Autowired
	private SchedulerProperties schedulerProperties;

	public void setSchedulerProperties(SchedulerProperties schedulerProperties) {
		this.schedulerProperties = schedulerProperties;
	}

	public void init() {
		schedulerProperties.getJobList();

		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			scheduler = sf.getScheduler();

			scheduler.getListenerManager().addJobListener(new JobListener() {

				@Override
				public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
					JobDetail detail = context.getJobDetail();
					if (detail instanceof DependentJobDetailImpl) {
						try {
							DependentJobDetailImpl jobImpl = (DependentJobDetailImpl)detail;
							if (jobImpl.getExecutionMode() == ExecutionMode.SYNCHRONIZED) {
								jobImpl.setExecutionStatus(ExecutionState.EXECUTED);
							}
							
							jobImpl.getCallbackListener().forEach(callbackListener -> {
								synchronized (jobImpl.getLock(callbackListener)) {
									if (jobImpl.getExecutionState() == ExecutionState.TO_BE_EXECUTED) {
										try {
											jobImpl.getLock(callbackListener).wait();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}								
								}
							});

							logger.info(String.format("%s:%s jobWasExecuted", jobImpl.getGroup(), jobImpl.getName()));
							context.getScheduler().deleteJob(detail.getKey());

							Set<JobKey> jobkeys = context.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(detail.getKey().getGroup()));
							List<DependentJobDetailImpl> jobList =  jobkeys.stream().map(jobKey -> {
								try {
									return context.getScheduler().getJobDetail(jobKey);
								} catch (SchedulerException e) {
									e.printStackTrace();
								}
								return null;
							}).filter(job -> job != null && job instanceof DependentJobDetailImpl).map(job -> (DependentJobDetailImpl)job).collect(Collectors.toList());
							
							if (CollectionUtils.isEmpty(jobList)) {
								logger.info("No more jobs");
							}

							jobList.stream().map(job -> {
								job.getDependentJobDetail().remove(detail);
								return job;
							}).filter(job -> job.getDependentJobDetail().isEmpty() && job.getExecutionState() == ExecutionState.NOT_YET_EXECUTED).forEach(j -> {
								try {
									logger.info(String.format("%s:%s shows empty dependency", j.getGroup(), j.getName()));
									synchronized (scheduler) {
										if (j.getExecutionState() == ExecutionState.NOT_YET_EXECUTED) {
											logger.info(String.format("%s:%s will be triggered", j.getGroup(), j.getName()));
											j.setExecutionStatus(ExecutionState.TO_BE_EXECUTED);
											scheduler.triggerJob(j.getKey());
										} else {
											logger.info(String.format("%s:%s is skipped", j.getGroup(), j.getName()));
										}
									}
								} catch (SchedulerException e) {
									e.printStackTrace();
								}
							});
						} catch (SchedulerException e1) {
							e1.printStackTrace();
						}
					}
				}

				@Override
				public void jobToBeExecuted(JobExecutionContext context) {
				}

				@Override
				public void jobExecutionVetoed(JobExecutionContext context) {
				}

				@Override
				public String getName() {
					return "ChainJobListener";
				}
			});
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(path = {"/load", "/load/{delayInMills}"})
	public String load(@PathVariable Optional<Long> delayInMills) {
		List<DependentJobDetailImpl> list = schedulerProperties.getJobList().stream().map(prop -> {
			logger.info(String.format("Config->%s", prop.toString()));
			return (DependentJobDetailImpl)DependentJobBuilder.newJobBuilder(DependentJobImpl.class).withProperties(prop).build();
		}).collect(Collectors.toList());

		list.stream().forEach(job -> {
			Optional<JobDetailProperties> o = schedulerProperties.getJobList().stream().filter(prop -> {
				return job.getKey().getGroup().equals(prop.getJobGroup()) && job.getKey().getName().equals(prop.getJobName());
			}).findFirst();
			if (o.isPresent()) {
				JobDetailProperties prop = o.get();
				if (CollectionUtils.isEmpty(prop.getDependentJobDetailMap()) == false) {
					prop.getDependentJobDetailMap().values().forEach(map ->
					{
						String group = map.get(JobDetailProperties.GROUP);
						String name = map.get(JobDetailProperties.NAME);
						for (DependentJobDetailImpl jobDetail : list) {
							if (jobDetail.getKey().getGroup().equals(group) && jobDetail.getKey().getName().equals(name)) {
								job.getDependentJobDetail().add(jobDetail);
							}
						}
					});
				}
			}
		});

		init();
		list.stream().forEach(job -> {
			try {
				logger.info(String.format("G-%s,N-%s,D-%s", job.getKey().getGroup(), job.getKey().getName(), job.getDependentJobDetail()));
				scheduler.addJob(job, false);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		});

		try {
			list.stream()
				//.filter(j -> j.getKey().getName().equals("starter"))
				.filter(j -> j.getExecutionState() == ExecutionState.TO_BE_EXECUTED)
				.forEach(j -> {
					try {
						Trigger trigger = null;
						
						if (delayInMills.isPresent()) {
							Calendar cal = Calendar.getInstance();
							cal.setTimeInMillis(System.currentTimeMillis() + delayInMills.get().longValue());
							trigger = TriggerBuilder.newTrigger().forJob((JobDetail)j).startAt(cal.getTime()).build();
						} else {
							trigger = TriggerBuilder.newTrigger().forJob((JobDetail)j).startNow().build();
						}
						
						scheduler.scheduleJob(trigger);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				});
			if (CollectionUtils.isEmpty(list) == false) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return String.format("%s", list);
	}

	@RequestMapping(path="/start")
	public String startLocal() {
		try {
			init();

			DependentJobDetailImpl starterJob = (DependentJobDetailImpl)DependentJobBuilder.newJobBuilder(DependentJobImpl.class).withIdentity("starter", "group").build();

			List<DependentJobDetailImpl> jobList = IntStream.range(0, 5).mapToObj(i -> {
				DependentJobDetailImpl job = (DependentJobDetailImpl)DependentJobBuilder.newJobBuilder(DependentJobImpl.class).withIdentity("job-" + i, "group").build();
				job.getDependentJobDetail().add(starterJob);
				return job;
			}).collect(Collectors.toList());

			for (DependentJobDetailImpl job : jobList) {
				if (Integer.valueOf(job.getName().replaceAll("job-", "")).intValue() % 2 == 0) {
					jobList.stream()
					.filter(j -> Integer.valueOf(j.getName().replaceAll("job-", "")).intValue() < Integer.valueOf(job.getName().replaceAll("job-", "")).intValue())
					.forEach(j -> job.getDependentJobDetail().add(j));
				} else {
					jobList.stream()
					.filter(j -> Integer.valueOf(j.getName().replaceAll("job-", "")).intValue() < Integer.valueOf(job.getName().replaceAll("job-", "")).intValue()-2)
					.forEach(j -> job.getDependentJobDetail().add(j));
				}
			}
			jobList.forEach(j -> {
				try {
					logger.info(String.format("Adding job %s:%s", j.getName(), j.getGroup()));
					scheduler.addJob(j, false);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});

			try {
				Trigger trigger = TriggerBuilder.newTrigger().forJob((JobDetail)starterJob).startNow().build();
				scheduler.scheduleJob((JobDetail) starterJob, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}

			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}


		return "Schedule triggered";
	}
}
