package ClasesOpcionesEjecucion;

import java.util.*;

public class Opcion2SimulacionEjecucion {

public static void simularEjecucion(int numProcesos, int totalMarcos) {

    if (totalMarcos % numProcesos != 0) {
        System.err.println("Error: El número de marcos debe ser múltiplo del número de procesos");
        return;
    }

    List<Proceso> procesos = new ArrayList<>();
    int marcosPorProceso = totalMarcos / numProcesos;

    for (int i = 0; i < numProcesos; i++) {
        Proceso p = new Proceso(i, marcosPorProceso);
        p.leerArchivoConfiguracion("referencias_procs/proc" + (i + 1) + ".txt");
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
        simularColasProcesos(procesos, marcosProceso, ultimoAccesoMarco, totalMarcos);

        mostrarEstadisticas(procesos);
    }


    private static void simularColasProcesos(List<Proceso> procesos, Map<Integer, Integer> mapaMarcos, Map<Integer, Long> tiempoUltimoUsoMarco, int totalMarcos) {
        Queue<Proceso> colaListos = new LinkedList<>(procesos);
        Queue<Proceso> colaDisco = new LinkedList<>();
        long tiempo = 0;

        while (!colaListos.isEmpty() || !colaDisco.isEmpty()) {

            Proceso p = !colaListos.isEmpty() ? colaListos.poll() : colaDisco.poll();

            if (p.terminado()) {
                System.out.println("========================");
                System.out.println("Termino proc: " + p.getId());
                System.out.println("========================");
                Set<Integer> marcosLiberados = new HashSet<>(p.getMarcosAsignados());
                for (int m : marcosLiberados)
                    System.out.println("PROC " + p.getId() + " removiendo marco: " + m);
                p.liberarMarcos();

                Proceso candidato = null;
                for (Proceso q : procesos)
                    if (!q.terminado())
                        if (candidato == null || q.getFallosPagina() > candidato.getFallosPagina())
                            candidato = q;

                if (candidato != null) {
                    for (int m : marcosLiberados) {
                        candidato.asignarMarco(m);
                        System.out.println("PROC " + candidato.getId() + " asignando marco nuevo " + m);
                    }
                }
                continue;
            }

            int lineaActual = p.getIndiceActual();
            String referencia = p.getReferencias().get(lineaActual);

            System.out.println("Turno proc: " + p.getId());
            System.out.println("PROC " + p.getId() + " analizando linea_: " + lineaActual);

            boolean falloPagina = procesarReferencia(p, referencia, mapaMarcos, tiempoUltimoUsoMarco, tiempo, totalMarcos);

            if (!falloPagina)
                System.out.println("PROC " + p.getId() + " hits: " + p.getHits());
            else
                System.out.println("PROC " + p.getId() + " falla de pag: " + p.getFallosPagina());

            System.out.println("PROC " + p.getId() + " envejecimiento");

            p.avanzarReferencia();
            if (!falloPagina)
                colaListos.add(p);
            else
                colaDisco.add(p);

            tiempo++;
        }
    }

    private static boolean procesarReferencia(Proceso p, String referencia, Map<Integer, Integer> marcosProceso, Map<Integer, Long> ultimoAccesoMarco, long tiempo, int totalMarcos) {

        String[] partes = referencia.split(",");
        int paginaVirtual = Integer.parseInt(partes[1]);
        if (p.getTablaPaginas().containsKey(paginaVirtual)) {
            int marco = p.getTablaPaginas().get(paginaVirtual);
            ultimoAccesoMarco.put(marco, tiempo);
            p.registrarHit(); 
            return false; 
            }

            boolean reemplazo = false;
            int marcoAsignar = -1;


        for (int marco : p.getMarcosAsignados()) {
            if (!p.getTablaPaginas().containsValue(marco)) {
                marcoAsignar = marco;
                break;}}

            

        if (marcoAsignar == -1) {
            marcoAsignar = encontrarMarcoLRU(p, marcosProceso, ultimoAccesoMarco);
            int paginaVieja = obtenerPaginaPorMarco(p.getTablaPaginas(), marcoAsignar);
            if (paginaVieja != -1) p.getTablaPaginas().remove(paginaVieja);
            reemplazo = true;
        }

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

    private static void mostrarEstadisticas(List<Proceso> procesos) {
        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        for (Proceso p : procesos) {
            double tasaFallos = (double) p.getFallosPagina() / p.getTotalReferencias();
            double tasaExito = (double) p.getHits() / p.getTotalReferencias();
            System.out.println("\nProceso " + p.getId() + ":");
            System.out.println("  Num referencias: " + p.getTotalReferencias());
            System.out.println("  Fallas: " + p.getFallosPagina());
            System.out.println("  Hits: " + p.getHits());
            System.out.println("  SWAP: " + p.getAccesosSwap());
            System.out.printf("  Tasa fallas: %.4f\n", tasaFallos);
            System.out.printf("  Tasa éxito: %.4f\n", tasaExito);
        }
    }

}
