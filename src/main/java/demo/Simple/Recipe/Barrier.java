package demo.Simple.Recipe;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Class for implementing Barrier Pattern
 */
public class Barrier extends SyncPrimitive {
    private int size = 3;
    private String name = "";
    private String path;

    /**
     * Constructor
     * @param hostPort
     * @param root
     * @param size
     */
    public Barrier(String hostPort, String root, int size){
        super(hostPort);
        this.root = root;
        this.size = size;
        if(zk != null){
            try{
                Stat s = zk.exists(root, false);
                if(s == null)
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (InterruptedException e) {
                LOG.info(">> Interrupted Exception: " + e.getMessage());
            } catch (KeeperException e) {
                LOG.info(">> Exception: " + e.getMessage());
            }

            // my node name
            try{
                name = InetAddress.getLocalHost().getCanonicalHostName() ;
                LOG.info(">> Name: " + name);
            } catch (UnknownHostException e){
                LOG.info(">> Exception: " + e.getMessage());
            }
        }

    }

    boolean enter() throws KeeperException, InterruptedException {
        path = zk.create(root + "/"+ name + "-", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        LOG.info(">> Node created at "+ path);
        while(true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() >= size) {
                    LOG.info(">> Entered");
                    return true;
                }
                LOG.info(String.format(">> Enter: [%d/%d] Waiting...", list.size(), size));
                mutex.wait();
            }
        }
    }

    boolean leave() throws KeeperException, InterruptedException {
        zk.delete(path, 0);
        while(true){
            synchronized (mutex){
                List<String> list = zk.getChildren(root, true);
                if(list.size() == 0) {
                    LOG.info(">> Leaved");
                    return true;
                }
                LOG.info(String.format(">> Leave: [%d/%d] Waiting...", list.size(), size));
                mutex.wait();
            }
        }
    }

    public static void main(String args[]) throws Exception {
        LOG.info(">> Barrier Recipe");
        Barrier m = new Barrier("localhost:2181", "/barrier", 3);
        m.enter();
        m.leave();
        m.close();
    }
}
