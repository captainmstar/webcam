package com.indiev.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamStorage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebcamApplication {

//	static {
//		Webcam.setDriver(new IpCamDriver(new IpCamStorage("src/main/resources/cameras.xml")));
//	}

	private static final Logger LOG = LoggerFactory.getLogger(WebcamApplication.class);


	public static void main(String[] args) throws Exception {

		SpringApplication.run(WebcamApplication.class, args);

		for (String name : WebcamCache.getWebcamNames()) {
			LOG.info("Will read webcam {}", name);
		}

		Server server = new Server(80);
		WebSocketHandler wsHandler = new WebSocketHandler() {

			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(WebcamWebSocketHandler.class);
			}
		};

		server.setHandler(wsHandler);
		server.start();
		server.join();
	}

}
