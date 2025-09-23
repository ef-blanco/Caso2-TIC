package ClasesOpcionesEjecucion;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Proceso {
    private int id;
    private List<String> referencias;
    private List<Integer> marcosAsignados;
    private Map<Integer, Integer> tablaPaginas;
    private int indiceActual;

    private int hits = 0;
    private int fallosPagina = 0;
    private int accesosSwap = 0;
    private int totalReferencias = 0;
    //Lo agrupo para no tener tantas líneas de código :p
    private int TP, NF, NC, NR, NP;

    public Proceso(int id, int marcosPorProceso) {
        this.id = id;
        this.referencias = new ArrayList<>();
        this.marcosAsignados = new ArrayList<>();
        this.tablaPaginas = new HashMap<>();
        this.indiceActual = 0;
    }

    public void leerArchivoConfiguracion(String rutaArchivo) {
        try {
            Scanner sc = new Scanner(new File(rutaArchivo));

            TP = Integer.parseInt(sc.nextLine().split("=")[1].trim());
            NF = Integer.parseInt(sc.nextLine().split("=")[1].trim());
            NC = Integer.parseInt(sc.nextLine().split("=")[1].trim());
            NR = Integer.parseInt(sc.nextLine().split("=")[1].trim());
            NP = Integer.parseInt(sc.nextLine().split("=")[1].trim());

            System.out.println("PROC " + id + " == Leyendo archivo de configuración ==");
            System.out.println("PROC " + id + " leyendo TP. Tam Páginas: " + TP);
            System.out.println("PROC " + id + " leyendo NF. Num Filas: " + NF);
            System.out.println("PROC " + id + " leyendo NC. Num Cols: " + NC);
            System.out.println("PROC " + id + " leyendo NR. Num Referencias: " + NR);
            System.out.println("PROC " + id + " leyendo NP. Num Paginas: " + NP);
            System.out.println("PROC " + id + " == Terminó de leer archivo de configuración ==");

            referencias.clear();
            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                if (!linea.isEmpty()) referencias.add(linea);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: Archivo " + rutaArchivo + " no encontrado");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: Formato inválido en el archivo " + rutaArchivo);
        } catch (NumberFormatException e) {
            System.err.println("Error: Valor numérico inválido en el archivo " + rutaArchivo);
        }
    }

        public Map<Integer, Integer> getTablaPaginas() { return tablaPaginas; }
        public List<Integer> getMarcosAsignados() { return marcosAsignados; }
        public int getHits() { return hits; }
        public int getFallosPagina() { return fallosPagina; }
        public int getAccesosSwap() { return accesosSwap; }
        public int getTotalReferencias() { return totalReferencias; }
        public int getIndiceActual() { return indiceActual; }
        public void avanzarReferencia() { indiceActual++; }
        public int getId() { return id; }
        public List<String> getReferencias() { return referencias; }

        public void asignarMarco(int m) { marcosAsignados.add(m); }
        public void liberarMarcos() { marcosAsignados.clear(); }

        public void registrarHit() { hits++; totalReferencias++; }
        public void registrarFallo(boolean requiereSwap) {
            fallosPagina++; totalReferencias++;
            if (requiereSwap) accesosSwap++;
        }

        public boolean terminado() { return indiceActual >= referencias.size(); }
}
