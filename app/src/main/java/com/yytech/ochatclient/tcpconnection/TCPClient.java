package com.yytech.ochatclient.tcpconnection;


import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.util.GsonUtil;
import com.yytech.ochatclient.util.SecurityUtil;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author Lee
 * @date 2017/12/1
 */
public class TCPClient {

    private static Logger logger = Logger.getLogger("TCPClient");

    private static TCPClient INSTANCE = new TCPClient();

    private TCPClient(){}

    public static TCPClient getInstance(){
        return INSTANCE;
    }

    private NioEventLoopGroup group;

    private int port = Const.PORT;

    private String ip = Const.IP;

    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
        System.out.println("===setChannel" + this.channel);
    }

    public void connect() {
        new Thread() {
            @Override
            public void run() {
                group = new NioEventLoopGroup();
                try {
                    // Client服务启动器 3.x的ClientBootstrap
                    // 改为Bootstrap，且构造函数变化很大，这里用无参构造。
                    Bootstrap bootstrap = new Bootstrap();
                    // 指定EventLoopGroup
                    bootstrap.group(group);
                    // 指定channel类型
                    bootstrap.channel(NioSocketChannel.class);
                    // 指定Handler
                    bootstrap
                            .handler(new ClientInitializer());
                    //如果没有数据，这个可以注释看看
                    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);
                    // 连接到本地的7878端口的服务端
                    ChannelFuture channelFuture  = bootstrap.connect(new InetSocketAddress(ip, port));

                    System.out.println("====!!!!!!");
                    channelFuture.channel().closeFuture().sync();//!!!!!!!!!!!!!
                    //channel = channelFuture.sync().channel();
                    System.out.println("====" + channel);
                } catch (Exception e) {
                    logger.warning(e.getMessage() + "====");
                } finally {
                    group.shutdownGracefully();
                }
            }
        }.start();
    }

    public void disConnect(){
        group.shutdownGracefully();
        channel = null;
    }

    public void sendMessage(MessageDTO messageDTO){
        System.out.println("===channel in sendMsg" + channel);
        channel.writeAndFlush(Unpooled.copiedBuffer(SecurityUtil.encode(GsonUtil.getInstance().toJson(messageDTO)), CharsetUtil.UTF_8));
    }

    public Channel getChannel() {
        return channel;
    }
}
