/**
 * Javier Abell?n. 18 Mar 2006
 *
 * Programa de ejemplo de como transmitir un fichero por un socket.
 * Esta es el main con el cliente, que piede un fichero, lo muestra en
 * pantalla y lo escribe cambiando el nombre.
 */
package TransferenciaArchivos;

import TransferenciaArchivos.MensajeDameFichero;
import TransferenciaArchivos.MensajeTomaFichero;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JFileChooser;

public class ClienteFichero {

    String name;
    String path;
    String ip;
    int puerto = 35557;

    public ClienteFichero(String name, String path, String ip) {
        this.name = name;
        this.path = path;
        this.ip = ip;
    }

    public static void main(String[] args) {
        // Se crea el cliente y se le manda pedir el fichero.
        //ClienteFichero cf = new ClienteFichero();
        //cf.pide("C:\\Users\\jesus\\Documents\\DSIV\\Usuarios.txt", "localhost", 35557);
    }

    /**
     * Establece comunicaci?n con el servidor en el puerto indicado. Pide el
     * fichero. Cuando llega, lo escribe en pantalla y en disco duro.
     *
     * @param fichero path completo del fichero que se quiere
     * @param servidor host donde est? el servidor
     * @param puerto Puerto de conexi?n
     */
    public boolean pide() {
        try {
            // Se abre el socket.
            Socket socket = new Socket(ip, puerto);

            // Se env?a un mensaje de petici?n de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket
                    .getOutputStream());
            MensajeDameFichero mensaje = new MensajeDameFichero();
            mensaje.nombreFichero = path;
            oos.writeObject(mensaje);

            //Se abre un FileChooser para elegir la ubicaci?n del nuevo archivo
            //String filePath = "";// = path + name;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int seleccion = fileChooser.showSaveDialog(fileChooser);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                String ruta = fileChooser.getSelectedFile().toPath().toString();
                System.out.println(ruta);
                mensaje.nombreFichero = ruta + "\\Descarga_" + name;
            }

            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(mensaje.nombreFichero);

            // Se crea un ObjectInputStream del socket para leer los mensajes
            // que contienen el fichero.
            ObjectInputStream ois = new ObjectInputStream(socket
                    .getInputStream());
            MensajeTomaFichero mensajeRecibido;
            Object mensajeAux;
            do {
                // Se lee el mensaje en una variabla auxiliar
                mensajeAux = ois.readObject();

                // Si es del tipo esperado, se trata
                if (mensajeAux instanceof MensajeTomaFichero) {
                    mensajeRecibido = (MensajeTomaFichero) mensajeAux;
                    // Se escribe en pantalla y en el fichero
                    System.out.print(new String(
                            mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos));
                    fos.write(mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos);
                } else {
                    // Si no es del tipo esperado, se marca error y se termina
                    // el bucle
                    System.err.println("Mensaje no esperado "
                            + mensajeAux.getClass().getName());
                    break;
                }
            } while (!mensajeRecibido.ultimoMensaje);

            // Se cierra socket y fichero
            fos.close();
            ois.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
