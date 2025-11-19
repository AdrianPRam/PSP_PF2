package Controlador;
import static Vista.Main.landingController;
public class UpdateController implements Runnable{

    @Override
    public void run() {
        try {
            while(true){

                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                System.out.println("Pistas Libres : "+landingController.getLandSites());
                System.out.println("//////////////////////////////////////////////////////");
                System.out.println(landingController.getStates());
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
