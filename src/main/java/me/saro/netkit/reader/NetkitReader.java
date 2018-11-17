package me.saro.netkit.reader;

import me.saro.netkit.NetkitConnection;
import me.saro.netkit.NetkitServer;
import me.saro.netkit.NetkitServerAccepter;

public abstract class NetkitReader {
    
    protected abstract void bind(NetkitServer server, NetkitServerAccepter accepter);
    protected abstract void accept(NetkitConnection connection);
}
