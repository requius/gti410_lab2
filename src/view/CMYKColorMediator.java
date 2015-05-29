/******************************************************
Cours : GTI410
Session : E2015
Groupe : 01
Projet : Laboratoire #1
�tudiant(e)(s) : Andrew Leong
Domgaing Moyo David Francis
Code(s) perm. :  LEOS26099000
DJOD14058001
Charg� de cours : Francis Cardinal
Charg�s de labo : Francis Cardinal
Nom du fichier : CMYKColorMediator.java
Date cr�� : 2015/05/11
Date dern. modif. 2015/05/24
 *******************************************************/

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	int cyan;
	int magenta;
	int yellow;
	int black;
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;

	/**
	 * Constructeur
	 * @param result
	 * @param imagesWidth
	 * @param imagesHeight
	 */
	CMYKColorMediator(ColorDialogResult result, int imagesWidth,
			int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		int red = result.getPixel().getRed();
		int green = result.getPixel().getGreen();
		int blue = result.getPixel().getBlue();

		int[] cmykColors = convertRGBtoCMYK(red, green, blue);

		this.cyan = cmykColors[0];
		this.magenta = cmykColors[1];
		this.yellow = cmykColors[2];
		this.black = cmykColors[3];

		this.result = result;
		result.addObserver(this);

		cyanImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);

		computeCyanImage(this.cyan, this.magenta, this.yellow, this.black);
		computeMagentaImage(this.cyan, this.magenta, this.yellow, this.black);
		computeYellowImage(this.cyan, this.magenta, this.yellow, this.black);
		computeBlackImage(this.cyan, this.magenta, this.yellow, this.black);
	}

	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider cs, int v) {
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		if (cs == cyanCS && v != cyan) {
			cyan = v;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (cs == magentaCS && v != magenta) {
			magenta = v;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (cs == yellowCS && v != yellow) {
			yellow = v;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (cs == blackCS && v != black) {
			black = v;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		if (updateCyan) {
			computeCyanImage(cyan, magenta, yellow, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, yellow, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, magenta, yellow, black);
		}
		if (updateBlack) {
			computeBlackImage(cyan, magenta, yellow, black);
		}

		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel pixel = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);

		result.setPixel(pixel);
	}
	
	/**
	 * Calcule les valeurs de l'image de la couleur Cyan
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 */
	public void computeCyanImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setRed(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}
	
	/**
	 * Calcule les valeurs de l'image de la couleur Magenta
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 */
	public void computeMagentaImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setGreen(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}

	/**
	 * Calcule les valeurs de l'image de la couleur Yellow
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 */
	public void computeYellowImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setBlue(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}

	/**
	 * Calcule les valeurs de l'image de la couleur Black
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 */
	public void computeBlackImage(int cyan, int magenta, int yellow, int black) {
		int blackTemp;

		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			blackTemp = (int) Math
					.round((((double) i / (double) imagesWidth) * (255.0)));

			rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, blackTemp);

			p.setRed(rgbColors[0]);
			p.setGreen(rgbColors[1]);
			p.setBlue(rgbColors[2]);

			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @return
	 */
	public int getCyan() {
		return (int) Math.round(this.cyan);
	}

	/**
	 * @return
	 */
	public int getMagenta() {
		return (int) Math.round(this.magenta);
	}

	/**
	 * @return
	 */
	public int getYellow() {
		return (int) Math.round(this.yellow);
	}

	/**
	 * @return
	 */
	public int getBlack() {
		return (int) Math.round(this.black);
	}
	
//Cette fonction est inspiré à partir du site web ci-dessous,
//la partie RGB to CMYK conversion formula
//http://www.rapidtables.com/convert/color/rgb-to-cmyk.htm	
	/**
	 * Converti les valeurs RGB en valeurs CMYK
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public int[] convertRGBtoCMYK(int red, int green, int blue) {
		int[] cmykColors = new int[4];

		float cyan = (float) red / 255;
		float magenta = (float) green / 255;
		float yellow = (float) blue / 255;
		float black = 1 - Math.max(cyan, Math.max(magenta, yellow));

		cmykColors[0] = (int) Math.round((255 * (1 - cyan - black) / (1 - black)));
		cmykColors[1] = (int) Math.round((255 * (1 - magenta - black) / (1 - black)));
		cmykColors[2] = (int) Math.round((255 * (1 - yellow - black) / (1 - black)));
		cmykColors[3] = (int) Math.round((255 * black));
		
		return cmykColors;
	}
	
//Cette fonction est inspiré à partir du site web ci-dessous,
//la partie CMYK to RGB conversion formula
//http://www.rapidtables.com/convert/color/cmyk-to-rgb.htm
	/**
	 * Converti les valeurs CMYK en valeurs RGB
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 * @return
	 */
	public int[] convertCMYKtoRGB(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = new int[3];

		rgbColors[0] = Math.round((255 - cyan) * (255 - black) / 255);
		rgbColors[1] = Math.round((255 - magenta) * (255 - black) / 255);
		rgbColors[2] = Math.round((255 - yellow) * (255 - black) / 255);

		return rgbColors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);

		int red = rgbColors[0];
		int green = rgbColors[1];
		int blue = rgbColors[2];

		Pixel currentColor = new Pixel(red, green, blue, 255);

		if (currentColor.getARGB() == result.getPixel().getARGB())
			return;

		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();

		int[] cmykColors = convertRGBtoCMYK(red, green, blue);

		cyan = cmykColors[0];
		magenta = cmykColors[1];
		yellow = cmykColors[2];
		black = cmykColors[3];

		cyanCS.setValue(cyan);
		magentaCS.setValue(magenta);
		yellowCS.setValue(yellow);
		blackCS.setValue(black);

		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);

		// Efficiency issue: When the color is adjusted on a tab in the
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the
		// other tabs (mediators) should be notified when there is a tab
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
