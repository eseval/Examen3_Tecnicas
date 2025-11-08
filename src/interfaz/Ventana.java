package interfaz;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import modelo.Temperatura;
import servicio.Analizador;
import servicio.CargadorDatos;

public class Ventana extends JFrame {
  private Analizador analizador;
  private List<Temperatura> datos;
  private DateTimeFormatter formatoVista = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  public Ventana() {
    setTitle("Análisis de Temperaturas");
    setSize(900, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    analizador = new Analizador();

    // Cargar datos
    try {
      CargadorDatos cargador = new CargadorDatos();
      datos = cargador.cargar("src/temperaturas.csv");

      if (datos.size() > 0) {
        System.out.println(".csv cargado con " + datos.size() + " registros.");
        JOptionPane.showMessageDialog(this, "Datos cargados: " + datos.size() + " registros");
        crearTabs();
      } else {
        JOptionPane.showMessageDialog(this, "No hay datos");
      }
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
    entrada.add(new JLabel("Desde (dd-MM-yyyy):"));
    JTextField desde = new JTextField("01-01-2025", 12);
    entrada.add(desde);

    entrada.add(new JLabel("Hasta (dd-MM-yyyy):"));
    JTextField hasta = new JTextField("31-01-2025", 12);
    entrada.add(hasta);

    JButton btnGenerar = new JButton("Generar");
    btnGenerar.addActionListener(
        e -> {
          try {
            LocalDate inicio = LocalDate.parse(desde.getText(), formatoVista);
            LocalDate fin = LocalDate.parse(hasta.getText(), formatoVista);

            if (inicio.isAfter(fin)){
                JOptionPane.showMessageDialog(this, "'Desde' debe ser anterior a 'Hasta'");
                return;
            }

            // Llamar método funcional
            Map<String, Double> promedios = analizador.promediosPorCiudad(datos, inicio, fin);

            if (promedios.isEmpty()) {
              JOptionPane.showMessageDialog(this, "Sin datos en ese rango");
              return;
            }

            if (panel.getComponentCount() > 1) {
              panel.remove(1); // Remover gráfica anterior
            }
            panel.add(new Grafica(promedios), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida (dd-MM-yyyy)");
          }
        });
    entrada.add(btnGenerar);

    JButton btnLimpiar = new JButton("Limpiar");
    btnLimpiar.addActionListener(
        e -> {
          desde.setText("01-01-2025");
          hasta.setText("31-01-2025");
          if (panel.getComponentCount() > 1) {
            panel.remove(1);
          }
          panel.add(new JPanel(), BorderLayout.CENTER);
          panel.revalidate();
          panel.repaint();
        });
    entrada.add(btnLimpiar);

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
    entrada.add(new JLabel("Fecha (dd-MM-yyyy):"));
    JTextField fecha = new JTextField("01-01-2025", 12);
    entrada.add(fecha);

    JButton btnBuscar = new JButton("Buscar");
    JTextArea resultado = new JTextArea(10, 50);
    resultado.setEditable(false);
    resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));

    btnBuscar.addActionListener(
        e -> {
          try {
            LocalDate f = LocalDate.parse(fecha.getText(), formatoVista);

            // Llamar métodos funcionales
            var masCalurosa = analizador.masCalurosa(datos, f);
            var menosCalurosa = analizador.menosCalurosa(datos, f);

            String texto = "FECHA: " + f.format(formatoVista) + "\n\n";

            if (masCalurosa.isPresent()) {
              Temperatura t = masCalurosa.get();
              texto += "CIUDAD MÁS CALUROSA:\n";
              texto +=
                  "  "
                      + t.getCiudad()
                      + " con "
                      + String.format("%.1f°C", t.getTemperatura())
                      + "\n\n";
            } else {
              texto += "Sin datos en esa fecha\n\n";
            }

            if (menosCalurosa.isPresent()) {
              Temperatura t = menosCalurosa.get();
              texto += "CIUDAD MENOS CALUROSA:\n";
              texto +=
                  "  "
                      + t.getCiudad()
                      + " con "
                      + String.format("%.1f°C", t.getTemperatura())
                      + "\n";
            }

            resultado.setText(texto);

          } catch (Exception ex) {
            resultado.setText("Error: Fecha inválida (dd-MM-yyyy)");
          }
        });

    entrada.add(btnBuscar);

    JButton btnLimpiar = new JButton("Limpiar");
    btnLimpiar.addActionListener(
        e -> {
          fecha.setText("01-01-2025");
          resultado.setText("");
        });
    entrada.add(btnLimpiar);

    panel.add(entrada);
    panel.add(new JScrollPane(resultado));

    return panel;
  }
}
