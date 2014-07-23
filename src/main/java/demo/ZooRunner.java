package demo; /**
 * Created by anhld on 7/17/14.
 */
import org.apache.zookeeper.*;
import org.slf4j.*;


public class ZooRunner implements Watcher{
    private final String hostPort;
    protected ZooKeeper zk;
    protected static Logger LOG = LoggerFactory.getLogger(ZooRunner.class);

    public ZooRunner(String hostPort) {
        this.hostPort = hostPort;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        LOG.info(watchedEvent.toString());
    }

    public void startZK(){
        while(true){
            try{
                zk = new ZooKeeper(hostPort, 15000, this);
                break;
            } catch(Exception e){
                LOG.warn(">>  "+ e.toString());
            }
        }

    }

    public void stopZK() throws Exception {
        while(true){
            try{
                zk.close();
                break;
            } catch(Exception e){
                LOG.warn(e.toString());
            }
        }
    }
}
