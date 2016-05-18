package gui;

import java.awt.*;
import javax.swing.*;

import model.Insectes;
import sim.display.*;
import sim.engine.*;
import sim.util.*;
import sim.portrayal.inspector.*;

public class Inspector extends PropertyInspector {
	private static final long serialVersionUID = 1L;
	public static String name() { return "Stream to System.out"; }
        
    public Inspector(Properties properties, int index, Frame parent, GUIState simulation) {
		super(properties,index,parent,simulation);
		this.setValidInspector(true);
		add(new JLabel("Streaming " + properties.getName(index) + " to System.out"));
    }

    public void updateInspector() {
    	Insectes i = (Insectes) simulation.state;
		System.out.println("nb of objects : " + i.getNbInsectes());
	}
}