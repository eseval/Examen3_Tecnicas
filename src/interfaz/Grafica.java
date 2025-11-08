package interfaz;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class Grafica extends JPanel {
  private Map<String, Double> datos;

  public Grafica(Map<String, Double> datos) {
    this.datos = datos;
    setPreferredSize(new Dimension(800, 350));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (datos.isEmpty()) {
      g.drawString("Sin datos", 20, 30);
      return;
    }

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int margen = 50;
    int ancho = getWidth() - 2 * margen;
    int alto = getHeight() - 2 * margen;

    // Encontrar máximo
    double maxTemp = datos.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

    // Dibujar ejes
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawLine(margen, getHeight() - margen, getWidth() - margen, getHeight() - margen);
    g2d.drawLine(margen, margen, margen, getHeight() - margen);

    // Datos
    int numBarras = datos.size();
    int anchoLiga = ancho / (numBarras * 2);
    int espacios = ancho / numBarras;

    g2d.setFont(new Font("Arial", Font.BOLD, 11));

    int indice = 0;
    for (Map.Entry<String, Double> item : datos.entrySet()) {
      String ciudad = item.getKey();
      double temp = item.getValue();

      int x = margen + (indice * espacios) + (espacios - anchoLiga) / 2;
      int altoLiga = (int) ((temp / maxTemp) * (alto - 20));
      int y = getHeight() - margen - altoLiga;

      // Barra
      g2d.setColor(new Color(70, 130, 180));
      g2d.fillRect(x, y, anchoLiga, altoLiga);

      // Borde
      g2d.setColor(Color.BLACK);
      g2d.drawRect(x, y, anchoLiga, altoLiga);

      // Etiqueta temperatura
      g2d.drawString(String.format("%.1f°C", temp), x - 5, y - 5);

      // Etiqueta ciudad
      g2d.drawString(ciudad, x - 10, getHeight() - margen + 20);

      indice++;
    }

    g2d.drawString("Temperatura (°C)", 10, 20);
  }
}
