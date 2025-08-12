package org.vinni.servidor.gui;


import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 * Author: Vinni
 */
public class PrincipalSrv extends JFrame {
    private final int PORT = 12345;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Creates new form Principal1
     */
    public PrincipalSrv() {
        initComponents();
    }

    private void initComponents() {

        this.setTitle("Servidor principal");


        bIniciar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mensajesTxt = new JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        bIniciar.setFont(new java.awt.Font("Segoe UI", 0, 18));
        // NOI18N
        bIniciar.setText("INICIAR SERVIDOR");

        bIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIniciarActionPerformed(evt);
            }
        });

        getContentPane().add(bIniciar);

        bIniciar.setBounds(100, 90, 250, 40);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("SERVIDOR TCP : HOEL");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(150, 10, 160, 17);

        mensajesTxt.setColumns(25);
        mensajesTxt.setRows(5);

        jScrollPane1.setViewportView(mensajesTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 160, 410, 70);

        setSize(new java.awt.Dimension(491, 290));
        setLocationRelativeTo(null);
    }// </editor-fold>

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalSrv().setVisible(true);
            }
        });

    }
    private void bIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        iniciarServidor();
    }

    private void iniciarServidor() {
        JOptionPane.showMessageDialog(this, "Iniciando servidor");
        new Thread(() -> {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                serverSocket = new ServerSocket(PORT);
                mensajesTxt.append("Servidor TCP en ejecución: " + addr + " ,Puerto " + serverSocket.getLocalPort() + "\n");

                while (true) {
                    clientSocket = serverSocket.accept();
                    mensajesTxt.append("Cliente conectado: " + clientSocket.getInetAddress() + "\n");

                    // Usar DataInputStream y DataOutputStream
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    // Leer nombre del archivo
                    int nombreLength = dis.readInt();
                    byte[] nombreBytes = new byte[nombreLength];
                    dis.readFully(nombreBytes);
                    String nombreArchivo = new String(nombreBytes);

                    // Leer tamaño del archivo
                    long tamanio = dis.readLong();

                    // Guardar el archivo recibido
                    File archivoSalida = new File("recibido_" + nombreArchivo);
                    try (FileOutputStream fos = new FileOutputStream(archivoSalida)) {
                        byte[] buffer = new byte[4096];
                        long bytesRestantes = tamanio;
                        int bytesLeidos;
                        while (bytesRestantes > 0 && (bytesLeidos = dis.read(buffer, 0, (int) Math.min(buffer.length, bytesRestantes))) != -1) {
                            fos.write(buffer, 0, bytesLeidos);
                            bytesRestantes -= bytesLeidos;
                        }
                    }

                    mensajesTxt.append("Archivo recibido: " + nombreArchivo + "\n");

                    // Responder al cliente
                    dos.writeBytes("Archivo Recibido ☺\n");

                }
            } catch (IOException ex) {
                ex.printStackTrace();
                mensajesTxt.append("Error en el servidor: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    // Variables declaration - do not modify
    private javax.swing.JButton bIniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JScrollPane jScrollPane1;
}
