package com.study.nettyimplementation;

import com.study.decoder.RequestDecoder;
import com.study.encoder.ResponseDataEncoder;
import com.study.handler.SimpleProcessingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class
NettyImplementationApplication {

	private int port;

	public NettyImplementationApplication(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {

		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8000;

		new NettyImplementationApplication(port).run();
	}

	public void run() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new RequestDecoder(),
									new ResponseDataEncoder(),
									new SimpleProcessingHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					  .childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
