package org.xodia.civ.ui.custom;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.xodia.civ.ui.BasicUserInterface;
import org.xodia.civ.ui.Label;
import org.xodia.civ.ui.layout.VerticalLayout;
import org.xodia.civ.units.Town;

public class TownStat extends BasicUserInterface{

	private Label 	defenseLabel,
					populationLabel,
					incomeLabel,
					taxLabel,
					happinessLabel,
					foodLabel;
	
	public TownStat(Town t, float x, float y) {
		super(x, y, 220, 150);
		
		setLayout(new VerticalLayout());
		
		if(t != null){
			defenseLabel = new Label("Defense: " + t.getCurrentDefense(), 0, 0, 220, 25);
			populationLabel = new Label("Population: " + t.getCurrentPopulation(), 0, 0, 220, 25);
			incomeLabel = new Label("Income: " + t.getCurrentIncome(), 0, 0, 220, 25);
			taxLabel = new Label("Tax %: " + (t.getTaxPercentage() * 100), 0, 0, 220, 25);
			happinessLabel = new Label("Happiness: " + t.getCurrentHappiness(), 0, 0, 220, 25);
			foodLabel = new Label("Food: " + t.getCurrentFood(), 0, 0, 0, 25);
		}else{
			defenseLabel = new Label("", 0, 0, 220, 25);
			populationLabel = new Label("", 0, 0, 220, 25);
			incomeLabel = new Label("", 0, 0, 220, 25);
			taxLabel = new Label("", 0, 0, 220, 25);
			happinessLabel = new Label("", 0, 0, 220, 25);
			foodLabel = new Label("", 0, 0, 220, 25);
		}
		
		addChild(defenseLabel);
		addChild(populationLabel);
		addChild(incomeLabel);
		addChild(taxLabel);
		addChild(happinessLabel);
		addChild(foodLabel);
	}
	
	public void setTown(Town t){
		defenseLabel.setText("Defense: " + t.getCurrentDefense());
		populationLabel.setText("Population: " + t.getCurrentPopulation());
		incomeLabel.setText("Income: " + t.getCurrentIncome());
		taxLabel.setText("Tax %: " + (t.getTaxPercentage() * 100));
		happinessLabel.setText("Happiness: " + t.getCurrentHappiness());
		foodLabel.setText("Food: " + t.getCurrentFood());
	}

	public void render(Graphics g) {
		g.setColor(Color.darkGray);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		
		super.render(g);
	}
	
}
