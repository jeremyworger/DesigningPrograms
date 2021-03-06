import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.pdf.*; 
import geomerative.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GeoLetters_04 extends PApplet {

/*
PLEASE READ INFO TAB

****************
NOTES : 13.08.15
        - Convert String to Char ?
 */

/////////////////////////// GLOBALS ////////////////////////////

int PDF_TIMER;
boolean TIMER = false; // turn on to activate PDF timer
int NUM_FRAMES = 100; // number of frames to record
String PDF_NUM = "A"; // change this for new exports (otherwise it overwrites)

// Freez motion by pressing 'f'
boolean STOPANIME = false;

Letter A;
Letter B;
Letter C;
Letter D;

/////////////////////////// SETUP ////////////////////////////
public void setup() {
  
  background(0);
  
  // constructor takes the following args :
  // Letter(this, char, x, y, nmbPnts, fontsize);
  A = new Letter(this, "P", 150, 150, 5, 230);
  B = new Letter(this, "L", 330, 150, 15, 230);
  C = new Letter(this, "A", 530, 150, 5, 230);
  D = new Letter(this, "Y", 700, 150, 15, 230);

  // TIMED PDF EXPORT - After a certain amount of frames PDF is exported
  // This enables coherent images if exporting variations of a movement.
  if (TIMER ) {
    PDF_TIMER = 0;
    beginRecord(PDF, "EXPORT_"+PDF_NUM+".pdf");
    println("Beginning PDF Export");
  }
}

/////////////////////////// DRAW ////////////////////////////

public void draw() {
  //background(255);

  // First letter
  noStroke();
  fill(255, 25);
  // linear motion method
  A.linearMotion(0, 1.7f);

  // Perlin Points method
  float na = map(mouseX, 0, width, 0.001f, 0.010f);
  float ns = map(mouseY, 0, height, 1, 500);
  //A.perlinPoints(na, ns, 0.7);

  // Methods xAxisWaves, yAxisWaves have the following args :
  // (spin, direction, angleFact, frequency, amplitude)
  // spin is a boolean TRUE / FALSE & direction is -1 or 1
  // angleFact is an int that takes values from 1-10
  A.dia = sin(frameCount*0.005f) * 2.6f;
  A.xAxisWaves(false, 1, 1, 0.015f, 25);
  // finally the display method
  A.display();

  // Second Letter
  B.linearMotion(0, 1.7f);

  // hard coding diameter
  B.dia = sin(frameCount*0.005f) * 2.6f;
  B.xAxisWaves(false, 1, 1, 0.015f, 25);
  B.display();

  C.linearMotion(0, 1.7f);
  C.dia = sin(frameCount*0.005f) * 2.6f;
  C.xAxisWaves(false, 1, 1, 0.015f, 25);
  C.display();
  //C.perlinPoints(sin(frameCount*0.1)*0.01, 7, 0.1);

  D.dia = sin(frameCount*0.005f) * 2.6f;
  D.xAxisWaves(false, 1, 1, 0.015f, 25);
  D.linearMotion(0, 1.7f);
  D.display();

  // UPDATE TIMER & END RECORD
  if (TIMER ) {
    PDF_TIMER++;
    println( PDF_TIMER );
    if ( PDF_TIMER >= NUM_FRAMES) {
      endRecord();
      saveFrame("FrameSaved_"+PDF_NUM+".png");
      println("PDF Export Finito !");
      PDF_TIMER = 0;
    }
  }

}

//////////////////////////////////////////////
public void mousePressed() {
  frameCount = 0;
  setup();
}
///////////////////////////////////////
/*
 ++++++++++++++++++++++++++++++++++++++++++
 DESIGNING PROGRAMS 1.0
 AN INTRODUCTORY COURSE TO CREATIVE 
 CODING FOR GRAPHIC DESIGN STUDENTS.
 
 IMPLEMENTED BY THE FREE ART BUREAU 2012/13 
 www.freeartbureau.org
 ++++++++++++++++++++++++++++++++++++++++++
 LETTER FORMS : GEO LETTERS
 SKETCH : GEO_LETTERS_04
 PARENT SKETCH : GEO_LETTERS_03
 TYPE : AUTONOMOUS
 
 **NB
 
 Experimental sketch that explores various methods for our Letter class :
 
 1). Linear motion on both x & y axis
 2). Perlin Points which adds noise movement to each point of the letter
 3). Oscillation on both x & y axis to create wavey movements
 
 */


///////////////////////////////// KEYS N' THINGS

public void keyPressed() {
  if (key == 'f')
    STOPANIME = !STOPANIME;
  if (STOPANIME) {
    noLoop();
  } 
  else {
    loop();
  }

  if (key == 'q') {
    setup();
  }

  if (key == 's') {
    saveFrame("FrameSaved_###.png");
  }

  /////////////////////// PDF EXPORT

  if (key == 'r') {
    beginRecord(PDF, "EXPORT_###.pdf");
    println("Beginning PDF Export");
  }

  if (key == 'e') {
    endRecord();
    saveFrame("FrameSaved_###.png");
    println("PDF Export Finito !");
  }
}



/*
 REMEMBER THAT EACH POINT OF THE LETTER IS A PVECTOR
 YET WE ALSO NEED X, Y POSITIONS TO PLACE EACH LETTER
 IN SPACE !
 */

class Letter {
  // programme parent
  PApplet applet;
  RFont font;
  RShape s;
  RShape polyshp;
  RPoint[] myPoints;
  int seg;

  String letter;// stored as String as Geomerative doesn't store chars
  int fontSize;

  PVector[] loc;
  PVector vel, acc;
  float accel;
  float dia;

  // MOTION VARS
  float x, y;
  float xDiff, yDiff;
  float angle;
  int dir;


  /* CONSTRUCTOR
   * @param applet calls parent applet to initialise Geomerative
   * @param letter  The letter to display
   * @param xPos, yPos  The x&y positions for letter
   * @param segmentLength  Number of points to display for letter
   * @param fontSize  The font size
   *
   */
  Letter(PApplet applet_, String letter, float xPos, float yPos, int segmentLength, int fontSize) {
    this.applet = applet_;
    RG.init(applet);

    this.x = xPos;
    this.y = yPos;
    angle = 0;
    xDiff = 0;
    yDiff = 0;
    this.fontSize = fontSize;
    this.letter = letter;
    this.seg = segmentLength;
    dia = 1;

    // setup font
    font = new RFont("AlteHaasGroteskBold.ttf", fontSize, CENTER);
    RCommand.setSegmentLength( segmentLength );
    //RGroup myGroup = font.toGroup( letter );
    s = font.toShape( letter );
    myPoints = s.getPoints();
    //myPoints = myGroup.getPoints();

    // NB ALWAYS REMEMBER TO INITIALISE OUR PVECTOR LOC WITH
    // THE ARRAY LENGTH EQUAL TO THE NUMBER OF POINTS !!!!
    loc = new PVector[myPoints.length];
    for (int i=0; i<myPoints.length; i++) {
      loc[i] = new PVector(myPoints[i].x, myPoints[i].y);
    }

    vel = new PVector(0, 0);
    accel = 0.0f;
    acc = new PVector(accel, accel);
  }

  ////////////////////////////////////////////////////////////// METHODS

  /* DISPLAY METHOD
   * displays each point for the letter
   */
  public void display() {
    //polygonize();
    pushMatrix();
    translate(x+xDiff, y+yDiff);
    rotate( angle * dir);

    for (int i=0; i<myPoints.length; i++) {
      pushMatrix();
      translate(loc[i].x, loc[i].y);
      ellipse(0, 0, dia, dia);
      popMatrix();
    }
    popMatrix();
  }

  /* LINEAR MOTION - ANIMATION METHOD
   * animates the whole letter
   * @param xFact Multiplication factor for x axis
   * @param yFact Multiplication factor for y axis
   */
  public void linearMotion(float xFact, float yFact ) {
    // remember this is the x & y position in relation to the letter & not the points
    // that we are modifying and adding to the translate() function in display
    xDiff += 0.3f*xFact;
    yDiff += 0.3f*yFact;
  }

  /* PERLIN POINTS - ANIMATION METHOD
   * animates each point on letter
   * @param noiseAmm Amount of noise
   * @param noiseScale Scale of noise
   * @param nfactor  General overall noise factor
   */
  public void perlinPoints(float noiseAmm, float noiseScale, float nfactor) {
    for (int i=0; i<myPoints.length; i++) {
      float offX = noise(frameCount+(loc[i].x * noiseAmm), frameCount+(loc[i].y * noiseAmm)) * noiseScale;
      loc[i].x += cos(offX) * nfactor;
      loc[i].y += sin(offX) * nfactor;
    }
  }


  //////////////////////////////////////////////////////////////

  /* ANIMATION METHOD
   *  xAxisWaves animates the whole letter with a wave like behavior (oscillation of axis)
   * @param spin true/false for turning on/off rotation
   * @param direction -1/+1 for anticlock/clockwise rotation
   * @param angleFact Multiplication factor for angle/rotation
   * @param freq Frequency of wave oscillation
   * @param amp Amplitude of wave oscillation
   */
  public void xAxisWaves(boolean spin, int direction, int angleFact, float freq, float amp) {
    this.dir = direction;
    xDiff = cos(frameCount * freq) * amp;
    if (spin) {
      angle += angleFact*0.0015f;
    }
  }

  /* ANIMATION METHOD
   *  yAxisWaves animates the whole letter with a wave like behavior (oscillation of axis)
   * @param spin true/false for turning on/off rotation
   * @param direction -1/+1 for anticlock/clockwise rotation
   * @param angleFact Multiplication factor for angle/rotation
   * @param freq Frequency of wave oscillation
   * @param amp Amplitude of wave oscillation
   */
  public void yAxisWaves(boolean spin, int direction, int angleFact, float freq, float amp) {
   this.dir = direction;
    yDiff = sin(frameCount * freq) * amp;
    if (spin) {
      angle += angleFact*0.0015f;
    }
  }

  /* GENERAL UPDATE METHOD (not implemented yet)
   * used for updating all vectors
   */
  public void update() {
    for (int i=0; i<myPoints.length; i++) {
      vel.add(acc);
      loc[i].add(vel);
    }
    acc.limit(1);
    acc.mult(0);
  }


  ////////////////////////////////////////////////////////////// WIP TESTING
  public void test01(int pnt) {

    for (int i=0; i<myPoints.length; i++) {
      float offX = frameCount * (loc[i].x + loc[i].y);
      loc[i].x += sin(offX)*0.05f;
    }
  }

  public void test( float velocityX, float velocityY ) {
    vel = new PVector(velocityX, velocityY);

    for (int i=0; i<myPoints.length; i++) {
      loc[i].add(vel);
    }
  }

  // not working !
  public void polygonize() {
    RCommand.setSegmentLength(seg);
    polyshp = RG.polygonize(s);
    fill(0,0,255);
    RG.shape(polyshp);
    //RPoint[]

  }


}
  public void settings() {  size(900, 640);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GeoLetters_04" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
