/**
 * Created by anhld on 7/17/14.
 */
package demo.Simple;
import demo.ZooRunner;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;


public class Demo2 extends ZooRunner {
    Demo2(String hostPort) {
        super(hostPort);
    }

    AsyncCallback.StringCallback masterCreatedCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int i, String s, Object o, String s2) {
            switch (KeeperException.Code.get(i)){
                case CONNECTIONLOSS:
                    createMasterNode();
                    LOG.warn(">> Connection to Zookeeper lost");
                    break;
                case OK:
                    break;
            }
        }
    };

    void createMasterNode(){
        zk.create("/master2",
                "Shifu 2".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT, masterCreatedCallback, null
        );
    }

    AsyncCallback.DataCallback readMasterCallback = new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
            switch (KeeperException.Code.get(i)){
                case CONNECTIONLOSS:
                    readMasterNode();
                    LOG.warn(">> Connection to Zookeeper lost");
                    break;
                case OK:
                    LOG.info(">> The master node with data = " + new String(bytes));
                    break;
            }
        }
    };

    void readMasterNode(){
        zk.getData("/master2", false, readMasterCallback, null);
    }

    public static void main(String args[]) throws Exception {
        LOG.info(">> Demo 2");
        Demo2 m = new Demo2("localhost:2181");
        m.startZK();
        m.createMasterNode();
        m.readMasterNode();
        Thread.sleep(60000);
        m.stopZK();
    }
}
