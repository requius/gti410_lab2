/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package controller;

import java.awt.event.MouseEvent;
import java.util.List;

import view.Application;
import view.CurvesPanel;
import model.BSplineCurveType;
import model.BezierCurveType;
import model.ControlPoint;
import model.Curve;
import model.CurvesModel;
import model.DocObserver;
import model.Document;
import model.HermiteCurveType;
import model.PolylineCurveType;
import model.Shape;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 Sebastien Bois, Eric Paquette</p>
 * <p>Company: (ETS) - Ecole de Technologie Superieure</p>
 * @author unascribed
 * @version $Revision: 1.9 $
 */
public class Curves extends AbstractTransformer implements DocObserver {
		
	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}	

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		boolean selectionIsACurve = false; 
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}
		
		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}
    
	/**
	 * 
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);
				
		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.LINEAR) {
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.BEZIER) {
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		} else if (string == CurvesModel.BSPLINE) {
			curve.setCurveType(new BSplineCurveType(CurvesModel.BSPLINE));	
		} else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}
	
	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s) && curve.getCurveType() != CurvesModel.LINEAR){
					int controlPointIndex = curve.getShapes().indexOf(s);
					// System.out.println("Try to apply G1 continuity on control point [" + controlPointIndex + "]");
						
					ControlPoint previousCP = (ControlPoint) curve.getShapes().get(controlPointIndex - 1);
					ControlPoint currentCP = (ControlPoint) s;
					ControlPoint nextCP = (ControlPoint) curve.getShapes().get(controlPointIndex + 1);
					
					double currentNextX = Math.abs((currentCP.getCenter().getX() - nextCP.getCenter().getX()));
					double currentNextY = Math.abs((currentCP.getCenter().getY() - nextCP.getCenter().getY()));
					double currentNextDistance = Math.sqrt(Math.pow(currentNextX, 2) + Math.pow(currentNextY, 2));
					
					double previousCurrentX = Math.abs((previousCP.getCenter().getX() - currentCP.getCenter().getX()));
					double previousCurrentY = Math.abs((previousCP.getCenter().getY() - currentCP.getCenter().getY()));
					double previousCurrentDistance = Math.sqrt(Math.pow(previousCurrentX, 2) + Math.pow(previousCurrentY, 2));
					
					/*double slope = (previousCP.getCenter().getX() - currentCP.getCenter().getY()) /
								   (previousCP.getCenter().getY() - currentCP.getCenter().getY());*/
					
					double unitVectorX = previousCurrentX / previousCurrentDistance;
					double unitVectorY = previousCurrentX / previousCurrentDistance;
					
					double directionX = currentNextDistance * unitVectorX;
					double directionY = currentNextDistance * unitVectorY;
					
					double newNextCPX = currentCP.getCenter().getX() + directionX;
					double newNextCPY = currentCP.getCenter().getY() + directionY;
					
					nextCP.getCenter().x = (int) Math.round(newNextCPX);
					nextCP.getCenter().y = (int) Math.round(newNextCPY);
					nextCP.notifyObservers();
				}
			}
		}
	}
	
	public void symetricControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s) && curve.getCurveType() != CurvesModel.LINEAR){
					int controlPointIndex = curve.getShapes().indexOf(s);
					// System.out.println("Try to apply G1 continuity on control point [" + controlPointIndex + "]");
						
					ControlPoint previousCP = (ControlPoint) curve.getShapes().get(controlPointIndex - 1);
					ControlPoint currentCP = (ControlPoint) s;
					ControlPoint nextCP = (ControlPoint) curve.getShapes().get(controlPointIndex + 1);
					
					double currentNextX = Math.abs((previousCP.getCenter().getX() - nextCP.getCenter().getX()));
					double currentNextY = Math.abs((previousCP.getCenter().getY() - nextCP.getCenter().getY()));
					double currentNextDistance = Math.sqrt(Math.pow(currentNextX, 2) + Math.pow(currentNextY, 2));
					
					double previousCurrentX = Math.abs((previousCP.getCenter().getX() - currentCP.getCenter().getX()));
					double previousCurrentY = Math.abs((previousCP.getCenter().getY() - currentCP.getCenter().getY()));
					double previousCurrentDistance = Math.sqrt(Math.pow(previousCurrentX, 2) + Math.pow(previousCurrentY, 2));
					
					/*double slope = (previousCP.getCenter().getX() - currentCP.getCenter().getY()) /
								   (previousCP.getCenter().getY() - currentCP.getCenter().getY());*/
					
					double unitVectorX = previousCurrentX / previousCurrentDistance;
					double unitVectorY = previousCurrentX / previousCurrentDistance;
					
					double directionX = currentNextDistance * unitVectorX;
					double directionY = currentNextDistance * unitVectorY;
					
					double newNextCPX = currentCP.getCenter().getX() + directionX;
					double newNextCPY = currentCP.getCenter().getY() + directionY;
					
					nextCP.getCenter().x = (int) Math.round(newNextCPX);
					nextCP.getCenter().y = (int) Math.round(newNextCPY);
					nextCP.notifyObservers();
				}
			}
		}
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}
	
	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}
	
	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;
}
