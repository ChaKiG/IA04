package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import model.Insectes;
import model.Insecte;
import model.Food;

public class SimulationUI extends GUIState {
	public static int FRAME_SIZE = 600;
	public Display2D display;
	public JFrame displayFrame;
	SparseGridPortrayal2D yardPortrayal = new SparseGridPortrayal2D();
	
	public SimulationUI(SimState state) {
		super(state);
	}
	public static String getName() {
		return "Simulation d'insectes"; 
	}
	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals() {
		Insectes beings = (Insectes) state;	
	  yardPortrayal.setField(beings.yard );
	  yardPortrayal.setPortrayalForClass(Insecte.class, getInsectePortrayal());
	  yardPortrayal.setPortrayalForClass(Food.class, getFoodPortrayal());
	  display.reset();
	  display.setBackdrop(Color.orange);
	  display.repaint();
	}
	private OvalPortrayal2D getInsectePortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.BLACK;
		r.filled = true;
		return r;
	}
	private OvalPortrayal2D getFoodPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.RED;
		r.filled = true;
		return r;
	}
	public void init(Controller c) {
		  super.init(c);
		  display = new Display2D(FRAME_SIZE,FRAME_SIZE,this);
		  display.setClipping(false);
		  displayFrame = display.createFrame();
		  displayFrame.setTitle("Beings");
		  c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		  displayFrame.setVisible(true);
		  display.attach( yardPortrayal, "Yard" );
	}
	
	public  Object  getSimulationInspectedObject()  {  return  state;  }
	
	public  Inspector  getInspector() {
		Inspector  i  =  super.getInspector();
		i.setVolatile(true);
		return  i;
	}
}
