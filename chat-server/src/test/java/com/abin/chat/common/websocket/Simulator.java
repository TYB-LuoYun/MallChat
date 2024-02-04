package com.abin.chat.common.websocket;

import cn.hutool.json.JSONUtil;
import com.abin.chat.common.user.domain.enums.WSBaseResp;
import com.abin.chat.common.user.domain.enums.WSRespTypeEnum;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Simulator {
    public static void start() throws InterruptedException {
        String serverIp = "127.0.0.1";
        int serverPort = 8090;
        EventLoopGroup group = new NioEventLoopGroup();
        for (int i = 0; i < 1; i++) {
            WebSocketConnector client = new WebSocketConnector(serverIp, serverPort, group);
            client.doConnect();
            Thread.sleep(5000);

            /**
             * 发送消息
             */
            WSBaseResp<String> wsBaseResp = new WSBaseResp<>();
            wsBaseResp.setType(WSRespTypeEnum.CHANNEL_CODE.getType());
            wsBaseResp.setData("不要回答");
            client.getChannel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
        }

    }

    public static void main(String[] args) throws InterruptedException {
        start();
    }
}
