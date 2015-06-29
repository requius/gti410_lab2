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
import model.*;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barre Brisebois, Eric Paquette</p>
 * <p>Company: ETS  Ecole de Technologie Superieure</p>
 * @author unascribed
 * @version Revision: 1.12 
 */
public class ImageColorFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageColorFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					System.out.println(getHueThreshold());
					if (floodFill)
						floodFill(ptTransformed.x, ptTransformed.y, currentImage.getPixel(ptTransformed.x, ptTransformed.y));
					else
						boundaryFill(ptTransformed.x, ptTransformed.y);
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void floodFill(int x, int y, Pixel interiorColor) {	
		if (0 <= x && 
			x < currentImage.getImageWidth() &&
			0 <= y &&
			y < currentImage.getImageHeight() &&
			!currentImage.getPixel(x, y).equals(fillColor) &&
			currentImage.getPixel(x, y).equals(interiorColor)
			) {
				
		currentImage.setPixel(x, y, fillColor);
		
		floodFill(x+1, y, interiorColor);
		floodFill(x-1, y, interiorColor);
		floodFill(x, y+1, interiorColor);
		floodFill(x, y-1, interiorColor);			
		}
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void boundaryFill(int x, int y) {
		if (0 <= x && 
			x < currentImage.getImageWidth() &&
			0 <= y &&
			y < currentImage.getImageHeight() &&
			!currentImage.getPixel(x, y).equals(fillColor) &&
			!currentImage.getPixel(x, y).equals(borderColor)
			) {
					
		currentImage.setPixel(x, y, fillColor);
			
		boundaryFill(x+1, y);
		boundaryFill(x-1, y);
		boundaryFill(x, y+1);
		boundaryFill(x, y-1);			
		}	
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean floodfill) {
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

//Cette fonction est inspiré à partir des notes de cours de GTI410,
//la partie HSV to RGB conversion formula
//http://www.rapidtables.com/convert/color/hsv-to-rgb.htm	
	/**
	 * Converti les valeurs RGB en valeur HSV
	 * @param red
	 * @param green
	 * @param blue
	 * @return 
	 */
	public float[] convertRGBtoHSV(int red, int green, int blue) {
		float[] hsvColors = new float[3];

		float r = (float) red / 255;
		float g = (float) green / 255;
		float b = (float) blue / 255;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));

		float hue = 0;
		float value = max;
		float saturation = (value - min) / value;
		
		if (r == max && g == min) {
			hue = 5 + (r - b) / (r - g);
		} else if (r == max && b == min) {
			hue = 1 - (r - g) / (r - b);
		} else if (g == max && b == min) {
			hue = 1 + (g - r) / (g - b);
		} else if (g == max && r == min) {
			hue = 3 - (g - b) / (g - r);
		} else if (b == max && r == min) {
			hue = 3 + (b - g) / (b - r);
		} else if (b == max && g == min) {
			hue = 5 - (b - r) / (b - g);
		}
		hue = hue * 60;

		if (hue < 0)
			hue += 360;

		hsvColors[0] = hue;
		hsvColors[1] = saturation;
		hsvColors[2] = value;
		
		return hsvColors;
	}

//Cette fonction est inspiré à partir du site web ci-dessous,
//la partie HSV to RGB conversion formula
//http://www.rapidtables.com/convert/color/hsv-to-rgb.htm	
	/**
	 * Converti les valeurs HSV en valeurs RGB
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return 
	 */
		public int[] convertHSVtoRGB(float hue, float saturation, float value) {
			int[] rgbColors = new int[3];
			
			float c = value * saturation;
			float h = hue / 60;
			float x = c * (1 - Math.abs(h % 2 - 1));
			float r = 0, g = 0, b = 0;

			if (0 <= h && h <= 1) {
				r = c;
				g = x;
				b = 0;
			} else if (1 <= h && h <= 2) {
				r = x;
				g = c;
				b = 0;
			} else if (1 <= h && h <= 2) {
				r = x;
				g = c;
				b = 0;
			} else if (2 <= h && h <= 3) {
				r = 0;
				g = c;
				b = x;
			} else if (3 <= h && h <= 4) {
				r = 0;
				g = x;
				b = c;
			} else if (4 <= h && h <= 5) {
				r = x;
				g = 0;
				b = c;
			} else if (5 <= h && h <= 6) {
				r = c;
				g = 0;
				b = x;
			}

			float m = (float) (value - c);
			
			rgbColors[0] = (int) ((r + m) * 255);
			rgbColors[1] = (int) ((g + m) * 255);
			rgbColors[2] = (int) ((b + m) * 255);

			return rgbColors;
		}
}
