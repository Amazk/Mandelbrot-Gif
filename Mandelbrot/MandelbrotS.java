// -*- coding: utf-8 -*-

import java.awt.Color;

public class MandelbrotS extends Thread {
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 19_000 ;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel
    private static int count = 1;               // Compteur d'image
    private final static int maxPNG = 100;          // Nombre maximum d'image créé
    private static boolean isFinished = false;          // Etat de l'image : Mis a Vrai quand l'image est finis


    public void run(){
        while (!isFinished) {
            synchronized (this) {
                try {
                    wait(80);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(count == maxPNG-1) return;
            image.save("myGif/pic"+(count<10 ? "00"+count : "0"+count)+".png");
            count++;
        }
    }

    public static void main(String[] args) {
        image.save("myGif/pic000"+".png");
        new MandelbrotS().start();
        final long debut = System.nanoTime();
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) colorierPixel(i,j);
            image.show();
        }
        isFinished = true;
        image.save("myGif/pic"+(count<10 ? "00"+count : "0"+count)+".png");
        long fin = System.nanoTime();
        System.out.println("Durée de calcul: " + (fin - debut)/1_000_000 + "ms.");
        System.out.println("Enregistrement terminé: "+(count+1)+" fichiers PNG créés.");
    }

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(90, 90, 90) ;
        final Color blanc = new Color(255, 255, 255) ;
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
        if (mandelbrot(a, b, max)) image.set(i, j, gris) ;
        else image.set(i, j, blanc) ;
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
