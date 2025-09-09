package org.vinni.Handler;


import org.vinni.servidor.gui.ServidorPrincipalFinal;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandlerFinal extends Thread {
    private Socket clientSocket;
    private int clientId;
    private ArrayList<Socket> clientesServ;
    private JTextArea areaMensajes;
    private ArrayList<Integer> idEnUso = new ArrayList<>();

    private ServidorPrincipalFinal servidorPrincipalFinal;

    private DataOutputStream dos;
    private DataInputStream dis;

    public ClientHandlerFinal(Socket socket, ArrayList<Socket> clientesServ, int clientId, JTextArea areaMensajes, ArrayList<Integer> idEnUso, ServidorPrincipalFinal servidorPrincipalFinal) {
        this.clientSocket = socket;
        this.clientesServ = clientesServ;
        this.clientId = clientId;
        this.areaMensajes = areaMensajes;
        this.idEnUso = idEnUso;
        this.servidorPrincipalFinal = servidorPrincipalFinal;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());

            servidorPrincipalFinal.actualizarAClientesLista();

            String linea;
            while (true) {
                linea = dis.readUTF();

                if (linea.startsWith("Mensaje:")) {
                    String mensaje = linea.substring(8);
                    mensajes(mensaje);

                } else if (linea.startsWith("Archivo:")) {
                    String instruccion = linea.substring(8);
                    archivos(instruccion, dis, dos);
                }
            }

        } catch (IOException e) {
            areaMensajes.setText("");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cerrarConexion();

            clientesServ.remove(clientSocket);

            servidorPrincipalFinal.removerCliente(this);

            areaMensajes.append("Cliente " + clientId + " se desconectó.\n");

            for (int i = 0; i < idEnUso.size(); i++) {
                if (idEnUso.get(i) == clientId) {
                    idEnUso.remove(i);
                }
            }
            servidorPrincipalFinal.actualizarCombo();
            servidorPrincipalFinal.actualizarAClientesLista();
        }
    }
    public void enviarMensajeCliente(String mensaje) {
        try {
            dos.writeUTF("Mensaje:" + mensaje);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mensajes(String linea) {
        if (linea.startsWith("Servidor:")) {
            String mensaje = linea.substring(9);

            areaMensajes.append("Cliente " + clientId + ": " + mensaje + "\n");

            try {
                dos.writeUTF("Mensaje:Servidor recibió tu mensaje \nCliente " + clientId + ": " + mensaje);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (linea.startsWith("Todos:")) {
            String mensaje = linea.substring(6);

            areaMensajes.append("Cliente " + clientId + ": " + mensaje + "\n");
            try {
                dos.writeUTF("Mensaje:Todos recibieron el mensaje");
            } catch (IOException e) {
                e.printStackTrace();
            }

            servidorPrincipalFinal.enviarMensajeTodos("Cliente " + clientId + ": " + mensaje);

        } else if (linea.startsWith("Cliente")) {
            String[] partes = linea.split(":", 2);

            String numeroClienteStr = partes[0].replace("Cliente ", "").trim();
            int clienteEnviar = Integer.parseInt(numeroClienteStr);

            String mensaje = partes[1].trim();

            areaMensajes.append("Cliente " + clientId + " envia a Cliente " + clienteEnviar + ": " + mensaje + "\n");
            try {
                dos.writeUTF("Mensaje:Cliente " + clienteEnviar + " recibió tu mensaje");
            } catch (IOException e) {
                e.printStackTrace();
            }

            servidorPrincipalFinal.enviarMensajeDedicado("Cliente " + clientId + ": " + mensaje, clienteEnviar);
        }
    }

    public void archivos(String linea, DataInputStream dis, DataOutputStream dos) throws IOException {
        // Leer nombre del archivo
        int nombreLength = dis.readInt();
        byte[] nombreBytes = new byte[nombreLength];
        dis.readFully(nombreBytes);

        String nombreArchivo = new String(nombreBytes);

        // Leer tamaño del archivo
        long tamanio = dis.readLong();

        // Leer contenido del archivo
        byte[] contenido = new byte[(int) tamanio];
        dis.readFully(contenido);

        if (linea.startsWith("Servidor:")) {

            File archivoRecibido = new File("Archivos/Servidor", nombreArchivo);
            try (FileOutputStream fos = new FileOutputStream(archivoRecibido)) {
                fos.write(contenido);
            }

            areaMensajes.append("Cliente " + clientId + " envió archivo: " + nombreArchivo +"\n");

            dos.writeUTF("Servidor recibió tu archivo \nCliente " + clientId + ": " + nombreArchivo);

        } else if (linea.startsWith("Todos:")) {

            areaMensajes.append("Cliente " + clientId + " envía a todos: " + nombreArchivo + "\n");

            servidorPrincipalFinal.enviarArchivoTodos(nombreArchivo, tamanio, contenido, clientId);

        } else if (linea.startsWith("Cliente")) {
            String[] partes = linea.split(":", 2);

            String numeroClienteStr = partes[0].replace("Cliente ", "").trim();
            int clienteEnviar = Integer.parseInt(numeroClienteStr);

            areaMensajes.append("Cliente " + clientId + " envía a Cliente " + clienteEnviar + ": " + nombreArchivo + "\n");

            servidorPrincipalFinal.enviarArchivoDedicado(nombreArchivo, tamanio, contenido, clienteEnviar, clientId);
        }
    }

    public void enviarArchivoCliente(String nombreArchivo, long tamanio, byte[] contenido, String mensaje) {
        try {

            dos.writeUTF("Archivo:"+ mensaje);

            byte[] bytesNombre = nombreArchivo.getBytes();
            dos.writeInt(bytesNombre.length);
            dos.write(bytesNombre);

            // Enviar tamaño
            dos.writeLong(tamanio);

            // Enviar contenido
            dos.write(contenido);
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClientId() {
        return clientId;
    }

    public void cerrarConexion() {
        try {
            if (dis != null) {
                dis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

