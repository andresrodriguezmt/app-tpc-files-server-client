package org.vinni.Handler;

import org.vinni.servidor.gui.ServidorMensPrincipal;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private int clientId;
    private ArrayList<Socket> clientesServ;
    private JTextArea areaMensajes;
    private ArrayList<Integer> idEnUso = new ArrayList<>();

    private ServidorMensPrincipal servidorMensPrincipal;

    private PrintWriter out;
    public ClientHandler(Socket socket, ArrayList<Socket> clientesServ, int clientId, JTextArea areaMensajes, ArrayList<Integer> idEnUso, ServidorMensPrincipal servidorMensPrincipal) {
        this.clientSocket = socket;
        this.clientesServ = clientesServ;
        this.clientId = clientId;
        this.areaMensajes = areaMensajes;
        this.idEnUso = idEnUso;
        this.servidorMensPrincipal = servidorMensPrincipal;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            servidorMensPrincipal.actualizarAClientesLista();

            String linea;
            while ((linea = in.readLine()) != null) {

                if (linea.startsWith("Servidor:")) {

                    String mensaje = linea.substring(9);

                    areaMensajes.append("Cliente " + clientId + ": " + mensaje + "\n");

                    out.println("Servidor recibió tu mensaje \nCliente " + clientId + ": " + mensaje);
                } else if (linea.startsWith("Todos:")) {

                    String mensaje = linea.substring(6);

                    areaMensajes.append("Cliente " + clientId + ": " + mensaje + "\n");
                    out.println("Servidor recibió tu mensaje");

                    servidorMensPrincipal.enviarMensajeTodos("Cliente " + clientId + ": " + mensaje);
                }else if(linea.startsWith("Cliente")){

                    String[] partes = linea.split(":", 2);

                    String numeroClienteStr = partes[0].replace("Cliente ", "").trim();
                    int clienteEnviar = Integer.parseInt(numeroClienteStr);

                    String mensaje = partes[1].trim();

                    areaMensajes.append("Cliente " + clientId + " envia a Cliente " + clienteEnviar + ": " + mensaje + "\n");
                    out.println("Cliente " + clienteEnviar + " recibió tu mensaje");
                    servidorMensPrincipal.enviarMensajeDedicado("Cliente " + clientId + ": " + mensaje, clienteEnviar);

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
            clientesServ.remove(clientSocket);

            areaMensajes.append("Cliente " + clientId + " se desconectó.\n");

            for (int i = 0; i < idEnUso.size(); i++) {
                if (idEnUso.get(i) == clientId) {
                    idEnUso.remove(i);
                }
            }
            servidorMensPrincipal.actualizarCombo();
            servidorMensPrincipal.actualizarAClientesLista();
        }
    }
    public void enviarMensajeCliente(String mensaje){
        if(out != null){
            out.println(mensaje);
        }
    }

    public int getClientId() {
        return clientId;
    }
}
