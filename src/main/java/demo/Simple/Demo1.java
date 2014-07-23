package demo.Simple;

import demo.ZooRunner;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * Created by anhld on 7/17/14.
 */
public class Demo1 extends ZooRunner {
    Demo1(String hostPort) {
        super(hostPort);
    }
    void createMasterNode(){
        while(true){
            try{
                zk.create("/master",
                        "Shifu".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT
                );
                break;
            } catch(KeeperException.NodeExistsException e){
                LOG.warn(">> The master node already exists");
                break;
            } catch (Exception e){
                LOG.warn(">> Unknown exception. Trying again.");
            }

        }
    }

    void readMasterNode(){
        while (true) {
            try {
                Stat stat = new Stat();
                byte data[] = zk.getData("/master", false, stat);
                LOG.info(">> The master node with data = " + new String(data));
                break;
            } catch (KeeperException.NoNodeException e) {
                LOG.warn(">> The master node does NOT exist");
            } catch (Exception e) {
                LOG.warn(">> Unknown exception");
            }

        }
    }

    public static void main(String args[]) throws Exception {
        LOG.info(">> Demo 1");
        Demo1 m = new Demo1("localhost:2181");
        m.startZK();
        m.createMasterNode();
        LOG.info(">> Press Enter to continue");
        System.console().readLine();
        m.readMasterNode();
        m.stopZK();
    }

}
