package interfaz;

import modelo.Temperatura;
import servicio.Analizador;
import servicio.CargadorDatos;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Ventana extends JFrame {
  private Analizador analizador;
  private List<Temperatura> datos;

  public Ventana() {
    setTitle("Análisis de Temperaturas");
    setSize(900, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    analizador = new Analizador();

    // Cargar datos
    try {
      CargadorDatos cargador = new CargadorDatos();
      datos = cargador.cargar("/home/eseval/IdeaProjects/Examen3_Tecnicas/src/temperaturas.csv");

      if (datos.size() > 0) {
        JOptionPane.showMessageDialog(this,
                "Datos cargados: " + datos.size() + " registros");
        crearTabs();
      } else {
        JOptionPane.showMessageDialog(this, "No hay datos");
      }
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private void crearTabs() {
    JTabbedPane tabs = new JTabbedPane();

    // Tab 1: Gráfica de promedios
    tabs.addTab("Promedios", crearTabPromedios());

    // Tab 2: Extremos
    tabs.addTab("Extremos", crearTabExtremos());

    add(tabs);
  }

  private JPanel crearTabPromedios() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Panel entrada
    JPanel entrada = new JPanel();
    entrada.add(new JLabel("Desde:"));
    JTextField desde = new JTextField("2023-01-01", 12);
    entrada.add(desde);

    entrada.add(new JLabel("Hasta:"));
    JTextField hasta = new JTextField("2023-01-05", 12);
    entrada.add(hasta);

    JButton btnGenerar = new JButton("Generar");
    btnGenerar.addActionListener(e -> {
      try {
        LocalDate inicio = LocalDate.parse(desde.getText());
        LocalDate fin = LocalDate.parse(hasta.getText());

        // Llamar método funcional
        Map<String, Double> promedios = analizador
                .promediosPorCiudad(datos, inicio, fin);

        if (promedios.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Sin datos en ese rango");
          return;
        }

        panel.remove(1); // Remover gráfica anterior
        panel.add(new Grafica(promedios), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Fecha inválida");
      }
    });
    entrada.add(btnGenerar);

    panel.add(entrada, BorderLayout.NORTH);
    panel.add(new JPanel(), BorderLayout.CENTER);

    return panel;
  }

  private JPanel crearTabExtremos() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Entrada
    JPanel entrada = new JPanel();
    entrada.add(new JLabel("Fecha (yyyy-MM-dd):"));
    JTextField fecha = new JTextField("2023-01-01", 12);
    entrada.add(fecha);

    JButton btnBuscar = new JButton("Buscar");
    JTextArea resultado = new JTextArea(10, 50);
    resultado.setEditable(false);
    resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));

    btnBuscar.addActionListener(e -> {
      try {
        LocalDate f = LocalDate.parse(fecha.getText());

        // Llamar métodos funcionales
        var masCalurosa = analizador.masCalurosa(datos, f);
        var menosCalurosa = analizador.menosCalurosa(datos, f);

        String texto = "=== FECHA: " + f + " ===\n\n";

        if (masCalurosa.isPresent()) {
          Temperatura t = masCalurosa.get();
          texto += "CIUDAD MÁS CALUROSA:\n";
          texto += "  " + t.getCiudad() + " → " +
                  String.format("%.1f°C", t.getTemperatura()) + "\n\n";
        } else {
          texto += "Sin datos en esa fecha\n\n";
        }

        if (menosCalurosa.isPresent()) {
          Temperatura t = menosCalurosa.get();
          texto += "CIUDAD MENOS CALUROSA:\n";
          texto += "  " + t.getCiudad() + " → " +
                  String.format("%.1f°C", t.getTemperatura()) + "\n";
        }

        resultado.setText(texto);

      } catch (Exception ex) {
        resultado.setText("Error: Fecha inválida (yyyy-MM-dd)");
      }
    });

    entrada.add(btnBuscar);

    panel.add(entrada);
    panel.add(new JScrollPane(resultado));

    return panel;
  }
}
