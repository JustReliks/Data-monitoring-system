package ru.spbstu.rakitin.management.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.management.dto.JmxConnectorProperties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.net.MalformedURLException;
import java.util.Optional;

@Slf4j
@Service
public class JmxConnectorService {

    public <T> Optional<T> getMetricValueFromJmx(JmxConnectorProperties properties, String objectName, String attributeName, Class<T> type) {
        try {
            JMXServiceURL jmxServiceURL = createJmxServiceURL(properties.getHost(), properties.getPort());
            try (JMXConnector connect = JMXConnectorFactory.connect(jmxServiceURL)) {
                MBeanServerConnection mBeanServerConnection = connect.getMBeanServerConnection();
                Object metric = mBeanServerConnection.getAttribute(new ObjectName(objectName), attributeName);
                return Optional.ofNullable(type.cast(metric));
            }
        } catch (Exception e) {
            log.error("Unable to get jmx [{}] [{}] metric with properties [{}]", objectName, attributeName, properties, e);
        }
        return Optional.empty();
    }

    private JMXServiceURL createJmxServiceURL(String host, int port) throws MalformedURLException {
        return new JMXServiceURL("rmi", host, port, String.format("/jndi/rmi://%s:%s/jmxrmi", host, port));
    }

}
