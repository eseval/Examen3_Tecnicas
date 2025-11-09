package servicio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import modelo.Temperatura;

public class Analizador {
  public Map<String, Double> promediosPorCiudad(
      List<Temperatura> datos, LocalDate inicio, LocalDate fin) {
    System.out.println("Calculando promedios desde " + inicio + " hasta " + fin);
    return datos.stream()
            .filter(t -> !t.getFecha().isBefore(inicio) && !t.getFecha().isAfter(fin))
        .collect(
            Collectors.groupingBy(
                Temperatura::getCiudad, Collectors.averagingDouble(Temperatura::getTemperatura)))
        .entrySet()
        .stream()
//        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Optional<Temperatura> masCalurosa(List<Temperatura> datos, LocalDate fecha) {
    System.out.println("Buscando la temperatura más calurosa para: " + fecha);
    return datos.stream()
        .filter(t -> t.getFecha().equals(fecha))
        .max(Comparator.comparingDouble(Temperatura::getTemperatura));
  }

  public Optional<Temperatura> menosCalurosa(List<Temperatura> datos, LocalDate fecha) {
    System.out.println("Buscando la temperatura menos calurosa para: " + fecha);
    return datos.stream()
        .filter(t -> t.getFecha().equals(fecha))
        .min(Comparator.comparingDouble(Temperatura::getTemperatura));
  }
}
