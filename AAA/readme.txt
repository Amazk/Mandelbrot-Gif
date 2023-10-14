Creation d'un gif de mandelbrot à partir d'un groupe d'image généré.

Auteur : Garcia Andy

Compilation : 
	javac *.java
	java Mandelbrot

Exécution du programme :
	-on créé l'image 'vide'
	-on créé un thread en parralèle qui toute les 100ms (j'ai mis 80ms dans le wait() car sinon il était trop lent, 
		sûrement car il prends du temps avant de redémaré) va enregistrer l'image courante du mandelbrot (limiter a 100 images)
	-on créé le mandelbrot qui va colorier colonne par colonne en affichant l'image courante
	-quand l'image est finis on stop la génération d'image
	-on enregistre l'image final
	-puis on affiche la durée et le nombre de png créé du programme
	-il reste à créer le gif grâce à la commande linux : convert pic*.png m.gif

En sortie : 
	durée : 7503ms
	nombres png : 75