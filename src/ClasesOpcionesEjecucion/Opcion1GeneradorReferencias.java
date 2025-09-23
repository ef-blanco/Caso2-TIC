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
    int nr = filas*columnas*3;
    int tamanioMatrices = nr*4;
    int numPag = (tamanioMatrices + tamanioPag - 1)/tamanioPag;

    try{
        String path = "./referencias_procs/proc"+((Integer)numProc).toString()+".txt";
        File proc = new File(path);
        
        //Para evitar errores porque no existe el file c:
        File directorio = new File("./referencias_procs/");
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio creado: " + directorio.getAbsolutePath());
            } else {
                System.out.println("Error creando el directorio");
                return;
            }
        }
        
        if(proc.createNewFile())
        {
            System.out.println("Referencia al proceso "+((Integer)numProc).toString()+" creada");
        }
        else{
            System.out.println("Ya existe un archivo con ese nombre");
        }

            BufferedWriter fWriter = new BufferedWriter(new FileWriter(path));
            //tamaño de páginas
            fWriter.write("TP="+((Integer)tamanioPag).toString());
            fWriter.newLine();
            //filas de las matrices
            fWriter.write("NF="+((Integer)filas).toString());
            fWriter.newLine();
            //columnas de las matrices
            fWriter.write("NC="+((Integer)columnas).toString());
            fWriter.newLine();
            //número de referencias
            fWriter.write("NR="+((Integer)nr).toString());
            fWriter.newLine();
            //número de páginas necesarias para guardar las matrices
            fWriter.write("NP="+((Integer)numPag).toString());
            fWriter.newLine();

            //desplazamiento para llegar hasta la referencia ;)
            int desplazamiento = 0;
            //en donde queda la referencia
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
                        String lineaRef = "M"+numMatriz+":"+"["+((Integer)i).toString()+"-"+((Integer)j).toString()+"],"+((Integer)pagina).toString()+","+((Integer)desplazamiento).toString()+","+accion;
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
