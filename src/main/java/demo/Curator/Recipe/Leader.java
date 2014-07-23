package demo.Curator.Recipe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Using Curator with Leader Latch Recipe
 */
public class Leader {
    protected Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void test() {
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString("localhost:2181").
                retryPolicy(new RetryOneTime(1)).
                build();
        client.start();

        String path= "/demo3/master";

        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, "Demo3".getBytes());
        } catch (Exception e) {
            LOG.warn(">> Exception when creating " + path + " znode: " + e.getMessage());
        }

        LeaderLatch leaderLatch = new LeaderLatch(client, path);
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                LOG.info(">> I am the LEADER now");
            }

            @Override
            public void notLeader() {
                LOG.info(">> I am NOT the LEADER");
            }
        });
        try {
            leaderLatch.start();
            LOG.info(">> Press any key to continue...");
            System.console().readLine();
            leaderLatch.close();
        } catch (Exception e) {
            LOG.warn(">> Exception when starting the latch");
        }

        client.close();
    }

    public static void main(String args[]) {
        Leader d = new Leader();
        d.test();
    }
}
