package ClasesOpcionesEjecucion;

import java.util.*;

public class Proceso {
    private int id;
    private List<String> referencias;
    private Map<Integer, Integer> tablaPaginas; 
    private Set<Integer> marcosAsignados;
    private int fallosPagina;
    private int hits;
    private int accesosSwap;
    private int totalReferencias;
    private int indiceActual;

    public Proceso(int id, List<String> referencias, int marcosIniciales) {
        this.id = id;
        this.referencias = referencias;
        this.tablaPaginas = new HashMap<>();
        this.marcosAsignados = new HashSet<>();
        this.fallosPagina = 0;
        this.hits = 0;
        this.accesosSwap = 0;
        this.totalReferencias = referencias.size();
        this.indiceActual = 0;
    }

    public int getId() { return id; }
    public int getFallosPagina() { return fallosPagina; }
    public int getHits() { return hits; }
    public int getAccesosSwap() { return accesosSwap; }
    public int getTotalReferencias() { return totalReferencias; }
    public int getIndiceActual() { return indiceActual; }
    public List<String> getReferencias() { return referencias; }
    public Map<Integer, Integer> getTablaPaginas() { return tablaPaginas; }
    public Set<Integer> getMarcosAsignados() { return marcosAsignados; }

    public boolean terminado() {
        return indiceActual >= totalReferencias;
    }

    public void avanzarReferencia() {
        indiceActual++;
    }

    public void registrarHit() {
        hits++;
    }

    public void registrarFallo(boolean reemplazo) {
        fallosPagina++;
        if (reemplazo) {
            accesosSwap += 2; 
        } else {
            accesosSwap += 1;
        }
    }

    public void asignarMarco(int marco) {
        marcosAsignados.add(marco);
    }

    public void liberarMarcos() {
        marcosAsignados.clear();
        tablaPaginas.clear();
    }
}
