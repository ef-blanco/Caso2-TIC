package ClasesOpcionesEjecucion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Opcion1GeneradorReferencias 
{
    /*
     * Los métodos en esta clase serán los responsables de generar los archivos de referencia que
     * el proyecto debe crear como respuesta de la primera opción de ejecución del simulador de uso
     * de memoria
     */

     public void generarReferencia(int numProc, int tamanioPag, int filas, int columnas)
     {
        //Cálculo del número de referencias y el número de páginas
        int nr = filas*columnas*3;
        int tamanioMatrices = nr*4;
        int numPag = (int) Math.ceilDiv(tamanioMatrices, nr);

        try{
            String path = "proc"+((Integer)numProc).toString()+".txt";
            File proc = new File(path);
            if(proc.createNewFile())
            {
                System.out.println("Referencia al proceso "+((Integer)numProc).toString()+" creada como");
            }
            else{
                System.out.println("Ya existe un archivo con ese nombre");
            }

            BufferedWriter fWriter = new BufferedWriter(new FileWriter(path));
            fWriter.write("TP="+((Integer)tamanioPag).toString());
            fWriter.write("NF="+((Integer)filas).toString());
            fWriter.write("NC="+((Integer)columnas).toString());
            fWriter.write("NR="+((Integer)nr).toString());
            fWriter.write("NP="+((Integer)numPag).toString());

            //Se escriben en el archivo las referencias de las distintas posiciones de las matrices
            int desplazamiento = 0;
            int pagina = 0;
            for(int k = 0; k<3; k++)
            {
                for(int i = 0; i<filas; i++)
                {
                    for(int j = 0; j<columnas; j++)
                    {
                        String numMatriz = ((Integer) (1+k)).toString();
                        if (desplazamiento == tamanioPag)
                        {
                            desplazamiento = 0;
                            pagina++;
                        } 
                        String accion = "r";
                        if(k==2)
                        {
                            accion = "w";
                        }
                        //Linea en formato MX: [i-j],pagina,desplazamiento,accion
                        String lineaRef = "M"+numMatriz+": "+"["+((Integer)i).toString()+"-"+((Integer)j).toString()+"],"+((Integer)pagina).toString()+","+((Integer)desplazamiento).toString()+","+accion;
                        fWriter.write(lineaRef);
                    }
                }
            }

            fWriter.close();
        }
        catch(IOException e){
            System.out.println("Hubo un error");
            e.printStackTrace();
        }
        

     }
}
