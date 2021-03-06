package com.indiev.webcam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sarxos.webcam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebcamCache implements WebcamUpdater.DelayCalculator, WebcamListener {

	private static final Logger LOG = LoggerFactory.getLogger(WebcamCache.class);

	/**
	 * How often images are updated on Dasding server.
	 */
	private static final long DELAY = 100;

	/**
	 * Webcams list.
	 */
	private Map<String, Webcam> webcams = new HashMap<String, Webcam>();

	/**
	 * WebSocket handlers.
	 */
	private List<WebcamWebSocketHandler> handlers = new ArrayList<WebcamWebSocketHandler>();

	/**
	 * Static instance to make access easier.
	 */
	private static final WebcamCache CACHE = new WebcamCache();

	public WebcamCache() {
		for (Webcam webcam : Webcam.getWebcams()) {
			webcam.addWebcamListener(this);
//			webcam.setViewSize(new Dimension(640, 480));
			Dimension[] nonStandardResolutions = new Dimension[] {
					WebcamResolution.HD.getSize()
			};
			webcam.setCustomViewSizes(nonStandardResolutions);
			webcam.setViewSize(WebcamResolution.HD.getSize());
			webcam.open(true);
			webcams.put(webcam.getName(), webcam);
		}
	}

	@Override
	public long calculateDelay(long snapshotDuration, double deviceFps) {
		return Math.max(DELAY - snapshotDuration, 0);
	}

	public static BufferedImage getImage(String name) {
		Webcam webcam = CACHE.webcams.get(name);
		try {
			return webcam.getImage();
		} catch (Exception e) {
			LOG.error("Exception when getting image from webcam", e);
		}
		return null;
	}

	public static List<String> getWebcamNames() {
		return new ArrayList<String>(CACHE.webcams.keySet());
	}

	@Override
	public void webcamOpen(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void webcamClosed(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void webcamDisposed(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void webcamImageObtained(WebcamEvent we) {
		for (WebcamWebSocketHandler handler : handlers) {
			handler.newImage(we.getSource(), we.getImage());
		}
	}

	public static void subscribe(WebcamWebSocketHandler handler) {
		CACHE.handlers.add(handler);
	}

	public static void unsubscribe(WebcamWebSocketHandler handler) {
		CACHE.handlers.remove(handler);
	}
}