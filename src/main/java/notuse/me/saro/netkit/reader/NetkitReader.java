package notuse.me.saro.netkit.reader;

import notuse.me.saro.netkit.NetkitConnection;
import notuse.me.saro.netkit.NetkitServer;
import notuse.me.saro.netkit.NetkitServerAccepter;

public abstract class NetkitReader {
    
    protected abstract void bind(NetkitServer server, NetkitServerAccepter accepter);
    protected abstract void accept(NetkitConnection connection);
}
