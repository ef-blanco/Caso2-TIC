package ClasesOpcionesEjecucion;

import java.io.*;
import java.util.*;

public class Opcion2SimulacionEjecucion {

    public static void simularEjecucion(int numProcesos, int totalMarcos) {
        List<Proceso> procesos = new ArrayList<>();
        int marcosPorProceso = totalMarcos / numProcesos;

        for (int i = 0; i < numProcesos; i++) {
            List<String> referencias = cargarReferenciasProceso(i);
            Proceso p = new Proceso(i, referencias, marcosPorProceso);

            for (int j = 0; j < marcosPorProceso; j++) {
                p.asignarMarco(i * marcosPorProceso + j);
                System.out.println("Proceso " + i + ": recibe marco " + (i * marcosPorProceso + j));
            }
            procesos.add(p);
        }

        Map<Integer, Integer> marcosProceso = new HashMap<>();
        Map<Integer, Long> ultimoAccesoMarco = new HashMap<>();
        for (int i = 0; i < totalMarcos; i++) {
            marcosProceso.put(i, -1);
            ultimoAccesoMarco.put(i, -1L);
        }

        simularRoundRobin(procesos, marcosProceso, ultimoAccesoMarco, totalMarcos);

        mostrarEstadisticas(procesos);
    }

    private static List<String> cargarReferenciasProceso(int idProceso) {
        List<String> referencias = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("referencias_procs/proc" +( idProceso+1) + ".txt"));
            for (int i = 0; i < 5; i++) scanner.nextLine(); 
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty()) referencias.add(linea);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: proc" + (idProceso+1) + ".txt no encontrado");
        }
        return referencias;
    }

    private static void simularRoundRobin(List<Proceso> procesos,Map<Integer, Integer> marcosProceso,Map<Integer, Long> ultimoAccesoMarco,int totalMarcos) {

        Queue<Proceso> cola = new LinkedList<>(procesos);
        long tiempo = 0;

        while (!cola.isEmpty()) {
            Proceso p = cola.poll();

            if (p.terminado()) {
                System.out.println("========================");
                System.out.println("Termino proc: " + p.getId());
                System.out.println("========================");
                liberarYReasignar(p, procesos);
                continue;
            }

            String ref = p.getReferencias().get(p.getIndiceActual());
            System.out.println("Turno proc: " + p.getId() + " - analizando linea_: " + p.getIndiceActual());

            boolean fallo = procesarReferencia(p, ref, marcosProceso, ultimoAccesoMarco, tiempo, totalMarcos);

            if (!fallo) {
                p.registrarHit();
                p.avanzarReferencia();
                System.out.println("PROC " + p.getId() + " hits: " + p.getHits());
            } else {
                System.out.println("PROC " + p.getId() + " falla de pag: " + p.getFallosPagina());
                // no avanza, se reintenta la misma referencia
            }

            if (!p.terminado()) cola.add(p);
            tiempo++;
        }
    }

    private static boolean procesarReferencia(Proceso p, String referencia,Map<Integer, Integer> marcosProceso,Map<Integer, Long> ultimoAccesoMarco,long tiempo, int totalMarcos) {
        String[] partes = referencia.split(",");
        int paginaVirtual = Integer.parseInt(partes[1]);

        if (p.getTablaPaginas().containsKey(paginaVirtual)) {
            int marco = p.getTablaPaginas().get(paginaVirtual);
            ultimoAccesoMarco.put(marco, tiempo);
            return false; 
        }

        boolean reemplazo = false;
        int marcoAsignar = -1;

        for (int marco : p.getMarcosAsignados()) {
            if (!p.getTablaPaginas().containsValue(marco)) {
                marcoAsignar = marco;
                break;
            }
        }

        if (marcoAsignar == -1) {
            marcoAsignar = encontrarMarcoLRU(p, marcosProceso, ultimoAccesoMarco);
            int paginaVieja = obtenerPaginaPorMarco(p.getTablaPaginas(), marcoAsignar);
            if (paginaVieja != -1) p.getTablaPaginas().remove(paginaVieja);
            reemplazo = true;
        }
        //Aquí se guarda el fallo 
        p.registrarFallo(reemplazo);

        marcosProceso.put(marcoAsignar, p.getId());
        p.getTablaPaginas().put(paginaVirtual, marcoAsignar);
        ultimoAccesoMarco.put(marcoAsignar, tiempo);

        return true; 
    }

    private static int encontrarMarcoLRU(Proceso p, Map<Integer, Integer> marcosProceso,Map<Integer, Long> ultimoAccesoMarco) {
        int marcoLRU = -1;
        long menorTiempo = Long.MAX_VALUE;

        for (int marco : p.getMarcosAsignados()) {
            long t = ultimoAccesoMarco.getOrDefault(marco, -1L);
            if (t < menorTiempo) {
                menorTiempo = t;
                marcoLRU = marco;
            }
        }
        return marcoLRU;
    }

    private static int obtenerPaginaPorMarco(Map<Integer, Integer> tabla, int marco) {
        for (Map.Entry<Integer, Integer> e : tabla.entrySet()) {
            if (e.getValue() == marco) return e.getKey();
        }
        return -1;
    }

    private static void liberarYReasignar(Proceso terminado, List<Proceso> procesos) {
        Set<Integer> marcosLiberados = new HashSet<>(terminado.getMarcosAsignados());
        terminado.liberarMarcos();

        Proceso candidato = null;
        for (Proceso p : procesos) {
            if (!p.terminado()) {
                if (candidato == null || p.getFallosPagina() > candidato.getFallosPagina()) {
                    candidato = p;
                }
            }
        }

        if (candidato != null) {
            for (int m : marcosLiberados) {
                candidato.asignarMarco(m);
                System.out.println("PROC " + candidato.getId() + " asignando marco nuevo " + m);
            }
        }
    }

    private static void mostrarEstadisticas(List<Proceso> procesos) {
        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        for (Proceso p : procesos) {
            double tasaFallos = (double) p.getFallosPagina() / p.getTotalReferencias();
            double tasaExito = (double) p.getHits() / p.getTotalReferencias();
            System.out.println("\nProceso " + p.getId() + ":");
            System.out.println("  Total referencias: " + p.getTotalReferencias());
            System.out.println("  Fallos de página: " + p.getFallosPagina());
            System.out.println("  Accesos a SWAP: " + p.getAccesosSwap());
            System.out.printf("  Tasa de fallos: %.4f\n", tasaFallos);
            System.out.printf("  Tasa de éxito: %.4f\n", tasaExito);
        }
    }
}
