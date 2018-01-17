package com.yytech.ochatclient.tcpconnection;

/**
 * Created by Administrator on 2017/11/27.
 * @author lee
 */

import android.content.Context;
import android.util.Log;

import com.yytech.ochatclient.resolver.DataResolverProxy;
import com.yytech.ochatclient.util.SecurityUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    String tag = "===ClientHandler";
    private Context context;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.d("MyHelloClientHandler", "===channelRead->msg=" + msg);
       /* ByteBuf byteBuf = (ByteBuf) msg;
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        String messageString = new String(req);*/
        Log.i(tag,""+msg.toString());
        DataResolverProxy.getInstance().doAction(SecurityUtil.decode(msg.toString()));

    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("======read complete");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        Log.d("MyHelloClientHandler", "===channelRead0->msg=" + msg);
        System.out.println("===msg decode" + msg);
        DataResolverProxy.getInstance().doAction(SecurityUtil.decode(msg));
        /*Message message = new Message();
        message.what = MainActivity.MSG_REC;
        message.obj = msg;
        MainActivity.mHandler.sendMessage(message);*/
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
     }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("===Client active" + ctx.channel());
        TCPClient.getInstance().setChannel(ctx.channel());
        //ctx.channel().writeAndFlush(Unpooled.copiedBuffer("1", CharsetUtil.UTF_8));
        System.out.println("===active after");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("===Client close ");
        super.channelInactive(ctx);
        TCPClient.getInstance().disConnect();
        TCPClient.getInstance().setChannel(null);
    }

}