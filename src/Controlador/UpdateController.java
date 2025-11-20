package Controlador;

import static Vista.Main.landingController;

public class UpdateController implements Runnable {
    private int time =0;
    @Override
    public void run() {
        try {
            while (!landingController.allDone()) {
                System.out.println("\n\n\n\n\n\n");
                System.out.println("Pistas libres: " + landingController.getLandSites());
                System.out.println("==============================================");
                System.out.println(landingController.getStates());
                Thread.sleep(1000);
                time+=1000;
            }
            System.out.println("\n\n\n\n\n\n");
            System.out.println("Simulacion terminada en " + (time) + " milisegundo\n" +
                    "numero de operaciones: " + landingController.getNumOperations());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
