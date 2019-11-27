package mediadorchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class Mediator {

    public ServerSocket server;
    public int puerto = 9000;
    public static List<Conexion> conexiones = new ArrayList<Conexion>();

    public void init() {
        Socket socket;
        try {
            server = new ServerSocket(puerto);
            System.out.println("Esperando peticiones por el puerto " + puerto);
            while (true) {
                socket = server.accept();
                DataInputStream buffEntrada = new DataInputStream(
                                            socket.getInputStream());
                DataOutputStream buffSalida = new DataOutputStream(
                                            socket.getOutputStream());
                String mensaje = buffEntrada.readUTF();
                StringTokenizer st = new StringTokenizer(mensaje);
                int numtok = st.countTokens();
                if (numtok == 1) {
                    if (ValidateUser(mensaje)) {
                        boolean flag = true;
                        for (Conexion conexion : conexiones) {
                            if (conexion.username.compareTo(mensaje) == 0) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            buffSalida.writeUTF("Bienvenido");
                            Conexion conexion = new Conexion(
                                    socket, buffEntrada, buffSalida, mensaje);
                            conexion.start();
                            conexiones.add(conexion);
                        } else {
                            buffSalida.writeUTF("Ya esta conectado");
                        }
                    } else {
                        buffSalida.writeUTF("Usuario no registrado");
                    }
                } else if (mensaje.contains("registra")) {
                    String array[] = mensaje.split(" ");
                    if (ValidateUser(array[1])) {
                        buffSalida.writeUTF("Usuario existente");
                    } else {
                        CreateUser(array[1]);
                        buffSalida.writeUTF("Creado");
                    }
                }
 
            }
        } catch (IOException ex) {
            System.out.println(ex);
        };
    }

    public boolean ValidateUser(String username) {
        boolean valido = false;
        try {
            File archivo = archivo = new File("C:\\Users\\jesus\\Documents\\DSIV\\Usuarios.txt");
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.equals(username)) {
                    valido = true;
                    break;
                }
            }
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (IOException e2) {
                System.out.println(e2);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return valido;
    }

    public boolean CreateUser(String username) {
        String path = "C:\\Users\\jesus\\Documents\\DSIV\\Usuarios.txt";
        File fichero = new File(path);
        boolean creado = false;
        //Escribir en el .dat
        BufferedWriter bw;
        if (fichero.exists()) {
            try {
                bw = new BufferedWriter(new FileWriter(fichero, true));
                bw.write(username + "\n");
                bw.close();
                creado = true;
            } catch (IOException ioex) {
                System.out.println(ioex);
            }
        } else {
            try {
                bw = new BufferedWriter(new FileWriter(fichero));
                bw.write(username);
                bw.close();
                creado = true;
            } catch (IOException ioex) {
                System.out.println(ioex);
            }
        }
        return creado;
    }
}