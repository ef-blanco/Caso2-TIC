package ClasesOpcionesEjecucion;

import java.io.File;
import java.io.IOException;

public class Opcion1GeneradorReferencias 
{
    /*
     * Los métodos en esta clase serán los responsables de generar los archivos de referencia que
     * el proyecto debe crear como respuesta de la primera opción de ejecución del simulador de uso
     * de memoria
     */

     public void generarReferencia(int tamanioPag, int filas, int columnas)
     {
        int nr = filas*columnas*3;
        int tamanioMatrices = nr*4;
        int numPag = (int) Math.ceilDiv(tamanioMatrices, nr);

        try{
            File proc = new File("procX.txt");
            if(proc.createNewFile())
            {
                System.out.println("Referencia al proceso X creada como");
            }
            else{
                System.out.println("Ya existe un archivo con ese nombre");
            }
        }
        catch(IOException e){
            System.out.println("Hubo un error");
            e.printStackTrace();
        }
        


     }
}
