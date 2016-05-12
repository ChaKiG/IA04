package main;
import sim.display.Console;
import gui.SimulationUI;
import model.Insectes;

public class SimulationMain {
	public static void main(String[] args) {
        runUI();
	}
	public static void runUI() {
		Insectes model = new Insectes(System.currentTimeMillis());
		SimulationUI gui = new SimulationUI(model);
		Console console = new Console(gui);
		console.setVisible(true);
	}
}
