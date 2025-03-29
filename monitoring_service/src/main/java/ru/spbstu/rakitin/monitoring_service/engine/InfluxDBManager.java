package ru.spbstu.rakitin.monitoring_service.engine;

import com.influxdb.client.*;
import com.influxdb.client.domain.*;
import com.influxdb.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;
import ru.spbstu.rakitin.monitoring_service.exception.OrganizationNotFoundException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;

import java.util.*;

@Slf4j
@Component
public class InfluxDBManager {

    public static final String ORGANIZATION_CREATED = "ORGANIZATION_CREATED";
    public static final String ORGANIZATION = "organization";
    private final InfluxDBClient influxDBClient;
    private final SequentialEngine sequentialEngine;
    private final OrganizationsApi organizationsApi;
    private final AuthorizationsApi authorizationsApi;
    private final BucketsApi bucketsApi;

    public InfluxDBManager(InfluxDBClient influxDBClient, SequentialEngine sequentialEngine) {
        this.influxDBClient = influxDBClient;
        this.sequentialEngine = sequentialEngine;
        organizationsApi = influxDBClient.getOrganizationsApi();
        authorizationsApi = influxDBClient.getAuthorizationsApi();
        bucketsApi = influxDBClient.getBucketsApi();
    }


    public void initiateMonitoringTask(MonitoringTaskConfig monitoringTaskConfig) throws Exception {

        Queue<SequentialTask> taskQueue = new LinkedList<>();

        String organizationName = monitoringTaskConfig.getProject().getProjectName();
        taskQueue.add(new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws Exception {
                Optional<Organization> organization = findOrganization(organizationName);
                if (organization.isPresent()) {
                    context.put(ORGANIZATION_CREATED, false);
                } else {
                    log.info("Organization not found: {}. Creating a new one.", organizationName);
                    organization = Optional.of(organizationsApi.createOrganization(organizationName));
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

    public String createReadApiKey(String description, String organizationName, List<String> buckets) throws OrganizationNotFoundException {
        Organization organization = findOrganization(organizationName)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("Organization with name %s not found", organizationName)));
        List<Permission> permissions = buckets.stream().map(bucket -> {
            Permission permission = new Permission();
            PermissionResource permissionResource = new PermissionResource();
            permissionResource.orgID(organization.getId())
                    .name(bucket)
                    .id(getBucket(bucket).getId())
                    .type("buckets");
            permission.action(Permission.ActionEnum.READ)
                    .resource(permissionResource);
            permission.resource(permissionResource);

            return permission;

        }).toList();
        Authorization authorization = new Authorization();
        authorization.orgID(organization.getId())
                .permissions(permissions)
                .description(description);
        authorization = authorizationsApi.createAuthorization(authorization);
        return authorization.getToken();
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

    public Optional<Organization> findOrganization(String organizationName) {
        OrganizationsQuery organizationsQuery = new OrganizationsQuery();
        organizationsQuery.setOrg(organizationName);
        try {
            List<Organization> organizations = organizationsApi.findOrganizations(organizationsQuery);
            return organizations.stream().findFirst();
        } catch (NotFoundException notFoundException) {
            return Optional.empty();
        }
    }

    public Organization getOrganizationOrCreate(String organizationName) {
        return findOrganization(organizationName).orElseGet(() ->
                organizationsApi.createOrganization(organizationName));
    }

    public Bucket getBucket(String bucketName) {
        return bucketsApi.findBucketByName(bucketName);
    }


    public void removeMonitoringInstance(MonitoringTaskConfig config) {
        Bucket bucketByName = bucketsApi.findBucketByName(config.getName());
        assert bucketByName != null;
        bucketsApi.deleteBucket(bucketByName);
    }
}
