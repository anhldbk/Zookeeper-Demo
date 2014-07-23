package demo.Curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/*
    Using Curator with Watch
 */
public class Demo2 {
    protected Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void test() {
        final CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181"
                                            , new ExponentialBackoffRetry(1000, 3));
        final String path = "/curator/group";

        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    List<String> children = client.getChildren().usingWatcher(this).forPath(path);
                    LOG.info(">> Group members: " + children);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        client.start();

        try {
            // Ensure the group node exists
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, new byte[0]);
        } catch (Exception e) {
            LOG.warn(">> Node for " + path + " exists");
        }

        try {
            List<String> children = client.getChildren()
                                    .usingWatcher(watcher).forPath(path);
            LOG.info(">> Group members: " + children);

        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.info("Press any key to continue...");
        System.console().readLine();
        client.close();
    }

    public static void main(String args[]) {
        Demo2 d = new Demo2();
        d.test();
    }
}
