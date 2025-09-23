
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ClasesOpcionesEjecucion.Opcion1GeneradorReferencias;
import ClasesOpcionesEjecucion.Opcion2SimulacionEjecucion;

class SimuladorUsoMemoria 
{
    /*
     * Esta es la clase principal de este proyecto. Esta clase será la encargada de recibir
     * los parámetros para las dos opciones de ejecución descritas en el enunciado. Esta clase
     * procesará las entradas, que serán archivos txt con los formatos específicados en el
     * enunciado, y le pasará los parámetros obtenidos de estas a funciones de las clases
     * Opcion1GeneradorReferencias y Opcion2SimulacionEjecucion para poder a cabo la ejecución
     * de las ocpiones 1 y 2 de ejecución descirtas en el enunciado
     */

    public static void main (String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("BIENVENIDO\n");
        System.out.println("\n");
        System.out.println("Ingrese la opción de ejecuión que desea usar:\n");
        System.out.println("1.Generador de referencias\n");
        System.out.println("2.Simulador de ejecución\n");
        String opcion = scanner.nextLine();


        if(opcion.equals("1"))
        {
            System.out.println("Ingrese la ruta del archivo con los datos de configuración:\n");
            String rutaArchivo = scanner.nextLine();

            try {
    File archivoConfig = new File(rutaArchivo);
    scanner = new Scanner(archivoConfig);
} catch (FileNotFoundException e) {
    System.err.println("Archivo no encontrado: " + rutaArchivo);
    return;
}

            //Se obtienen los datos del archivo de configuración
            String linea = scanner.nextLine();
            int TP = Integer.parseInt(linea.substring(3));
            
            linea = scanner.nextLine();
            int NPROC = Integer.parseInt(linea.substring(6));
            
            linea = scanner.nextLine();
            String tamanios = linea.substring(5);
            //Tamaños de las matrices guardados como [2x2, 4x4, 3x4... ]
            String[] listTamanios = tamanios.split(",");

            /*
             * Para cada proceso obtenemos la dimension de sus matrices y usamos el método generarReferencias
             * de la clase Opcion1GeneradorReferencias para crear el txt con las referencias asociadas a dicho
             * proceso
             */
        
            for(int p = 1; p<=NPROC; p++)
            {
                String dimension = listTamanios[p-1];
                String[] filasYCol = dimension.split("x");
                int filas = Integer.parseInt(filasYCol[0]);
                int columnas = Integer.parseInt(filasYCol[1]);
                Opcion1GeneradorReferencias.generarReferencia(p,TP,filas,columnas);
            }

        }

        else if(opcion.equals("2")) {
            System.out.println("Ingrese el número de procesos:");
            int numProcesos = Integer.parseInt(scanner.nextLine());
            
            System.out.println("Ingrese el número total de marcos:");
            int numMarcos = Integer.parseInt(scanner.nextLine());
            
            if(numMarcos % numProcesos != 0) {
                System.out.println("Error: El número de marcos debe ser múltiplo del número de procesos");
                return;
            }
            
            Opcion2SimulacionEjecucion.simularEjecucion(numProcesos, numMarcos);
}

        scanner.close();
        
    }
}
