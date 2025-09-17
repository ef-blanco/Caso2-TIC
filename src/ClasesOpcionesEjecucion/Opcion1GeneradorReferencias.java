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

     public static void generarReferencia(int numProc, int tamanioPag, int filas, int columnas)
     {
        //Cálculo del número de referencias y el número de páginas
        int nr = filas*columnas*3;
        //Se multiplica el número de referencias por el tamaño de un entero, 4B, para obtener el espacio total que requieren las matrices
        int tamanioMatrices = nr*4;
        //ceilDiv hace la división común y corriente y luego aproxima el resultado al entero más cercano
        int numPag = (int) Math.ceilDiv(tamanioMatrices, tamanioPag);

        try{
            String path = "./referencias_procs/proc"+((Integer)numProc).toString()+".txt";
            File proc = new File(path);
            //Si se crea correctamente el archivo de referencias aparecerá un mensaje por consola que confirme su creación
            if(proc.createNewFile())
            {
                System.out.println("Referencia al proceso "+((Integer)numProc).toString()+" creada");
            }
            else{
                System.out.println("Ya existe un archivo con ese nombre");
            }

            BufferedWriter fWriter = new BufferedWriter(new FileWriter(path));
            //Escribe en el archivo de referencias las primeras líneas correspondientes al:
            //TP:tamaño de página
            fWriter.write("TP="+((Integer)tamanioPag).toString());
            fWriter.newLine();
            //NF: número de filas de las matrices
            fWriter.write("NF="+((Integer)filas).toString());
            fWriter.newLine();
            //NC: número de columnas de las matrices
            fWriter.write("NC="+((Integer)columnas).toString());
            fWriter.newLine();
            //NR: número de referencias
            fWriter.write("NR="+((Integer)nr).toString());
            fWriter.newLine();
            //NP: número de páginas necesarias para guardar las matrices
            fWriter.write("NP="+((Integer)numPag).toString());
            fWriter.newLine();

            //Se escriben en el archivo las referencias de las distintas posiciones de las matrices

            //desplazamiento indica el desplazamiento dentro de la página para llegar a la referencia
            int desplazamiento = 0;
            //pagina indica la página en la que se situará la referencia
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
                        //Linea en formato MnumMatriz: [i-j],pagina,desplazamiento,accion
                        String lineaRef = "M"+numMatriz+": "+"["+((Integer)i).toString()+"-"+((Integer)j).toString()+"],"+((Integer)pagina).toString()+","+((Integer)desplazamiento).toString()+","+accion;
                        fWriter.write(lineaRef);
                        fWriter.newLine();
                        
                        desplazamiento = desplazamiento+4;
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
