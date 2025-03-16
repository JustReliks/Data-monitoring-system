package ru.spbstu.rakitin.monitoring_service.engine;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.OrganizationsApi;
import com.influxdb.client.OrganizationsQuery;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.Organization;
import com.influxdb.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfluxDBManager {

    public static final String ORGANIZATION_CREATED = "ORGANIZATION_CREATED";
    public static final String ORGANIZATION = "organization";
    private final InfluxDBClient influxDBClient;
    private final SequentialEngine sequentialEngine;

    public void initiateMonitoringTask(MonitoringTaskConfig monitoringTaskConfig) throws Exception {

        Queue<SequentialTask> taskQueue = new LinkedList<>();

        String organizationName = monitoringTaskConfig.getProject().getProjectName();
        OrganizationsApi organizationsApi = influxDBClient.getOrganizationsApi();
        taskQueue.add(new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws Exception {
                OrganizationsQuery organizationsQuery = new OrganizationsQuery();
                organizationsQuery.setOrg(organizationName);
                Organization organization;
                try {
                    List<Organization> organizations = organizationsApi.findOrganizations(organizationsQuery);
                    context.put(ORGANIZATION_CREATED, false);
                    organization = organizations.stream().findFirst().get();
                } catch (NotFoundException notFoundException) {
                    log.info("Organization not found: {}. Creating a new one.", organizationName);
                    organization = organizationsApi.createOrganization(organizationName);
                    context.put(ORGANIZATION_CREATED, true);
                }

                context.put(ORGANIZATION, organization);

            }

            @Override
            public void rollback(Map<String, Object> context) throws Exception {
                if (Boolean.getBoolean(context.get(ORGANIZATION_CREATED).toString())) {
                    organizationsApi.deleteOrganization(organizationName);
                }
            }
        });

        taskQueue.add(new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws Exception {
                createBucket(monitoringTaskConfig, (Organization) context.get(ORGANIZATION));
            }

            @Override
            public void rollback(Map<String, Object> context) throws Exception {

            }
        });

        sequentialEngine.performSequential(taskQueue);
    }

    private Bucket createBucket(MonitoringTaskConfig monitoringTaskConfig, Organization organization) {
        Bucket bucket = new Bucket();
        bucket.setOrgID(organization.getId());
        bucket.setName(monitoringTaskConfig.getName());
        BucketRetentionRules bucketRetentionRules = new BucketRetentionRules();
        bucketRetentionRules.shardGroupDurationSeconds(monitoringTaskConfig.getShardGroupDurationSeconds());
        bucketRetentionRules.everySeconds(monitoringTaskConfig.getRetentionTimeSeconds());
        bucket.addRetentionRulesItem(bucketRetentionRules);
        return influxDBClient.getBucketsApi().createBucket(bucket);
    }

    public Organization createOrganizationIfNotExists(String organizationName) {
        OrganizationsQuery organizationsQuery = new OrganizationsQuery();
        organizationsQuery.setOrg(organizationName);
        OrganizationsApi organizationsApi = influxDBClient.getOrganizationsApi();
        try {
            List<Organization> organizations = organizationsApi.findOrganizations(organizationsQuery);
            return organizations.stream().findFirst().get();
        } catch (NotFoundException notFoundException) {
            log.info("Organization not found: {}. Creating a new one.", organizationName);
            return organizationsApi.createOrganization(organizationName);
        }
    }


}
