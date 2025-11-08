import interfaz.Ventana;
import javax.swing.*;

public class Main {
  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeLater(
        () -> {
          Ventana ventana = new Ventana();
          ventana.setVisible(true);
        });
  }
}
