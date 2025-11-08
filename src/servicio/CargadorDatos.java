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
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
      br.readLine();
      String linea;
      while ((linea = br.readLine()) != null) {
        String[] partes = linea.split(",");
        if (partes.length == 3) {
          String ciudad = partes[0];
          LocalDate fecha = LocalDate.parse(partes[1], formato);
          double temperatura = Double.parseDouble(partes[2]);
          datos.add(new Temperatura(ciudad, fecha, temperatura));
        }
      }
    }
    return datos;
  }
}
