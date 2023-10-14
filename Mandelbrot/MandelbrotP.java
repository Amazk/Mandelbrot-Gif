// -*- coding: utf-8 -*-

import java.awt.Color;

public class MandelbrotP extends Thread {
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 20_000 ;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel
    final static int nbThread = 4;
    private static volatile int ligne = 0;

    private int incr() {
        int ligneEnCours;
        synchronized (this) {
            ligneEnCours = ligne;
            ligne++;
        }
        return ligneEnCours;
    }

    public void run() {
        final long debut = System.nanoTime();
        int ligneEnCours;
        while(ligne < taille) {
            ligneEnCours = incr();
            for (int i = 0; i < taille; i++) colorierPixel(i, ligneEnCours);
            synchronized (image) {image.show();}
        }

        final long fin = System.nanoTime();
        final long duree = (fin - debut) / 1_000_000;
        System.out.println("Durée du " +getName()+" = "+ (double) duree / 1000 + " s.");
    }
    
    public static void main(String[] args) throws Exception {
        final long debut = System.nanoTime();

        MandelbrotP[] mandelbrotPS = new MandelbrotP[nbThread];
        for(int i=0;i<nbThread;i++) {
            mandelbrotPS[i] = new MandelbrotP();
            mandelbrotPS[i].start();
        }

        for(int i=0; i<nbThread; i++) mandelbrotPS[i].join();

        final long fin = System.nanoTime();
        final long duree = (fin - debut) / 1_000_000;
        System.out.println("Durée = " + (double) duree / 1000 + " s.");
        image.show();
    }    

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public void colorierPixel(int i, int j) {
        final double xc = -.5 ;
        final double yc = 0 ; // Le point (xc,yc) est le centre de l'image
        final double region = 2 ;
        /*
          La région du plan considérée est un carré de côté égal à 2.
          Elle s'étend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region/2 + region*i/taille ;
        double b = yc - region/2 + region*j/taille ;
        // Le pixel (i,j) correspond au point (a,b)
        if (mandelbrot(a, b, max)) image.set(i, j, colorThread()) ;
        else image.set(i, j, Color.white) ;
    }
    private Color colorThread() {
        return getName().endsWith("0") ? Color.blue : getName().endsWith("1") ? Color.green :
                getName().endsWith("2") ? Color.red : Color.pink;
    }

    // La fonction mandelbrot(a, b, max) détermine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0 ;
        double y = 0 ;
        for (int t = 0; t < max; t++) {
            if (x*x + y*y > 4.0) return false ; // Le point (a,b) est blanc
            double nx = x*x - y*y + a ;
            double ny = 2*x*y + b ;
            x = nx ;
            y = ny ;
        }
        return true ; // Le point (a,b) est gris
    }
}


/* 
   $ make
   javac *.java 
   jar cvmf MANIFEST.MF Mandelbrot.jar *.class 
   manifeste ajouté
   ajout : Mandelbrot.class(entrée = 1697) (sortie = 1066)(compression : 37 %)
   ajout : Picture.class(entrée = 5689) (sortie = 3039)(compression : 46 %)
   rm *.class 
   $ java -version
   java version "11.0.3" 2019-04-16 LTS
   Java(TM) SE Runtime Environment 18.9 (build 11.0.3+12-LTS)
   Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.3+12-LTS, mixed mode)
   $ java -jar Mandelbrot.jar
   Durée = 35.851 s.
   ^C
*/
