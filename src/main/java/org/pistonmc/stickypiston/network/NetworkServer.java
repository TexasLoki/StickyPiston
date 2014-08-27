package org.pistonmc.stickypiston.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NetworkServer extends Thread {

    private InetSocketAddress address;
    private ChannelFuture channel;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public NetworkServer(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void run() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new StickyChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Binding to " + address.getHostName() + ":" + address.getPort() + "...");
            ChannelFuture future = b.bind(address).sync();
            channel = future.channel().closeFuture();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForClose() {
        try {
            channel.sync();
            close();
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}
