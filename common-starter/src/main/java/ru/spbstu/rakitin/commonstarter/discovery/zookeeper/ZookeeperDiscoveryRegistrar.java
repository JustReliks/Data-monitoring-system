package ru.spbstu.rakitin.commonstarter.discovery.zookeeper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.spbstu.rakitin.dto.ServiceName;

@Slf4j
@RequiredArgsConstructor
public class ZookeeperDiscoveryRegistrar {

    private final ServiceName serviceName;
    private final ZooKeeper zooKeeper;
    private final String basePath;
    private final String connectString;

    @EventListener(ContextRefreshedEvent.class)
    public void registerService() {
        RegistrationTask registrationTask = new RegistrationTask(zooKeeper, basePath + "/" + serviceName.name(), connectString);
        Thread thread = new Thread(registrationTask);
        thread.setDaemon(true);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                registrationTask.unregister();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @RequiredArgsConstructor
    private static class RegistrationTask implements Runnable {

        private final ZooKeeper zooKeeper;
        private final String path;
        private final String address;
        private String createdNodePath;

        @SneakyThrows
        @Override
        public void run() {
            log.info("Registering Zookeeper service");
            createRootPath();
            String currentNodePath = path + "/address_";
            createdNodePath = zooKeeper.create(currentNodePath, address.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("Zookeeper service registered");
            Thread.sleep(Long.MAX_VALUE);
        }

        public void unregister() throws InterruptedException {
            try {
                if (createdNodePath != null) {
                    log.info("Unregistering Zookeeper service");
                    zooKeeper.delete(createdNodePath, -1);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } finally {
                zooKeeper.close();
            }
        }


        private void createRootPath() {
            try {
                Stat stat = zooKeeper.exists(path, false);
                if (stat == null) {
                    zooKeeper.create(path,
                            new byte[0],
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (KeeperException.NodeExistsException ignored) {
                // Нода уже существует - это нормально
            } catch (InterruptedException | KeeperException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
