package com.capgemini.rest.scheduler.job;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import com.capgemini.rest.scheduler.properties.JobDetailProperties;

public class DependentJobBuilder extends JobBuilder {
	private JobDetailProperties prop;
	
	public static DependentJobBuilder newJobBuilder() {
		return new DependentJobBuilder();
	}
	
	public static DependentJobBuilder newJobBuilder(Class <? extends Job> jobClazz) {
		DependentJobBuilder b =  new DependentJobBuilder();
		return (DependentJobBuilder) b.ofType(jobClazz);
	}
	
	public DependentJobBuilder withProperties(JobDetailProperties prop) {
		this.prop = prop;
		return this;
	}
	
	@Override
	public JobDetail build() {
		withIdentity(prop.getJobName(), prop.getJobGroup());
		JobDetail job = super.build();
		DependentJobDetailImpl job2 = new DependentJobDetailImpl();
		
		job2.setJobClass(job.getJobClass());
		job2.setDescription(job.getDescription());
		job2.setKey(job.getKey()); 
//		job2.setDurability(job.isDurable());
		job2.setDurability(true);
		job2.setRequestsRecovery(job.requestsRecovery());
		if(!job.getJobDataMap().isEmpty()) {
            job2.setJobDataMap(job.getJobDataMap());
		}
		job2.setExecutionStatus(prop.getJobExecutionState());
		prop.getActionUri().forEach(uri -> job2.setActionUri(uri));
		job2.setExecutionMode(prop.getExecutionMode());
		job2.setCallbackListener(new DependentJobCallbackListener());
		return job2;
	}
}
