package com.github.ntsee.sheetchef;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Sheet Chef");
		config.setWindowedMode(SheetChefView.WIDTH, SheetChefView.HEIGHT);
		config.setWindowSizeLimits(SheetChefView.WIDTH, SheetChefView.HEIGHT, -1, -1);
		new Lwjgl3Application(new SheetChefApp(), config);
	}
}
