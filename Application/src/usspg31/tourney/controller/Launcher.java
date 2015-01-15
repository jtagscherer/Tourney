package usspg31.tourney.controller;

public class Launcher {

	public static void main(String[] args) {
		// Force the usage of GPU acceleration prior to launching the application
		System.setProperty("prism.forceGPU", "true");
		EntryPoint.main(args);
	}

}
