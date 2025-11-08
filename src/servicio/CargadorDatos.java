package servicio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import modelo.Temperatura;

public class CargadorDatos {

  public List<Temperatura> cargar(String archivo) throws IOException {
    List<Temperatura> datos = new ArrayList<>();

    DateTimeFormatter formatoCSV = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
      br.readLine(); // Saltar encabezado

      String linea;
      while ((linea = br.readLine()) != null) {
        String[] partes = linea.split(",");
        if (partes.length == 3) {
          try {
            String ciudad = partes[0].trim();
            String fechaTexto = partes[1].trim();
            double temp = Double.parseDouble(partes[2].trim());

            LocalDate fecha = LocalDate.parse(fechaTexto, formatoCSV);
            datos.add(new Temperatura(ciudad, fecha, temp));
          } catch (Exception e) {
            System.err.println("Error en línea: " + linea);
          }
        }
      }
    }

    return datos;
  }
}
