package demo.Curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Using Curator in a synchronous way
 */
public class Demo1 {
    protected Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void test() {
        CuratorFramework client = null;
        String path = "/curator/anhld";
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, "Demo1".getBytes());
        } catch (Exception e) {
            LOG.warn(">> Exception when creating " + path + " znode: " + e.getMessage());
        }

        try {
            String data = new String(client.getData().forPath(path));
            LOG.info(">> Data: " + data);
            client.setData().forPath(path, (data + " >> ").getBytes());

        } catch (Exception e) {
            LOG.warn(">> Exception when reading" + path + " znode: " + e.getMessage());
        }
        client.close();
    }

    public static void main(String args[]) {
        Demo1 d = new Demo1();
        d.test();
    }
}
