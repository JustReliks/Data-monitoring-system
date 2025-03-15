package ru.spbstu.rakitin.monitoring_service.engine;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.OrganizationsApi;
import com.influxdb.client.OrganizationsQuery;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InfluxDBManager {

    private final InfluxDBClient influxDBClient;

    public void initiateMonitoringTask(MonitoringTaskConfig monitoringTaskConfig) {
        Organization organization = createOrganizationIfNotExists(monitoringTaskConfig.getProject().getProjectName());
        createBucket(monitoringTaskConfig.getName(), organization);
    }

    private Bucket createBucket(String taskName, Organization organization) {
        return influxDBClient.getBucketsApi().createBucket(taskName, organization);
    }

    public Organization createOrganizationIfNotExists(String organizationName) {
        OrganizationsQuery organizationsQuery = new OrganizationsQuery();
        organizationsQuery.setOrg(organizationName);
        OrganizationsApi organizationsApi = influxDBClient.getOrganizationsApi();
        List<Organization> organizations = organizationsApi.findOrganizations(organizationsQuery);
        if (organizations.isEmpty()) {
            return organizationsApi.createOrganization(organizationName);
        } else {
            return organizations.stream().findFirst().get();
        }
    }


}
