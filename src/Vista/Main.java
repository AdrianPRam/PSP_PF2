package Vista;

import Controlador.LandingController;
import Controlador.UpdateController;

import java.util.Scanner;

public class Main {
    public static LandingController landingController;

    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);
        System.out.print("Cuantas pistas quieres? (MIN 2): ");
        int pistas = input.nextInt();

        System.out.print("Cuantos aviones quieres? (MIN 10): ");
        int aviones = input.nextInt();
        UpdateController updateController = new UpdateController();

        landingController = new LandingController(pistas,aviones);

        Thread updateThread = new Thread(updateController);
        updateThread.start();
    }
}
