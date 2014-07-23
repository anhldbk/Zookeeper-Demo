package demo.Simple.Recipe;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class SyncPrimitive implements Watcher, Closeable{
    static ZooKeeper zk = null;
    protected static Logger LOG = LoggerFactory.getLogger(SyncPrimitive.class);
    final static Integer mutex = -1;
    String root;

    SyncPrimitive(String hostPort){
        if(zk== null){
            try{
                LOG.info(">> Connecting...");
                zk = new ZooKeeper(hostPort, 15000, this);
                LOG.info(">> Connected");
            } catch(IOException e){
                LOG.info(">> Exception: " + e.getMessage());
                zk = null;
            }
        }
    }

    @Override
    public void close(){
        try{
            zk.close();
        } catch (InterruptedException e) {
        }
    }

    @Override
    synchronized public void process(WatchedEvent watchedEvent) {
        synchronized (mutex){
            mutex.notify();
            LOG.info(">> "+ watchedEvent.toString());
        }
    }
}
