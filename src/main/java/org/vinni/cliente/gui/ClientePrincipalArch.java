package org.vinni.cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ClientePrincipalArch extends JFrame implements ActionListener{

    private final String titulo = "Cliente principal";
    
    private final String letra = "Arial";
    private final String error = "Error";
    private volatile boolean ejecucion = false;

    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;

    private final ArrayList<String> listaArchivos = new ArrayList<>();
    private File[] archivo = new File[1];

    private JLabel labelIconoCliente;
    private JLabel tituloCliente;
    private JLabel tituloServidores;
    private JLabel tituloArchivos;
    private JLabel tituloMensajes;

    private JTextField textoServidor;
    private JTextField textoArchivo;

    private JButton botonConexSer;
    private JButton botonDesconexSer;
    private JButton botonSelecArch;
    private JButton botonEnviarArch;

    private JFileChooser escogerArchivo;

    private JScrollPane barraAreaMensajes;
    private JTextArea areaMensajes;


    private ImageIcon imagenCliente;

    public ClientePrincipalArch(){
        iniciarlizarComponentes();
        dimensionar();
        adicionar();
        visualizar();
        accionar();
    }

    public void iniciarlizarComponentes(){

        this.labelIconoCliente = new JLabel("", JLabel.CENTER);
        this.tituloCliente = new JLabel("Cliente TCP: DFRACK");
        this.tituloServidores = new JLabel("Servidores");
        this.tituloArchivos = new JLabel("Seleccionar Archivos");
        this.tituloMensajes = new JLabel("Mensajes");

        this.textoServidor = new JTextField();

        this.textoArchivo = new JTextField();
        textoArchivo.setEditable(false);

        this.botonConexSer = new JButton("Conectar ▲");
        this.botonConexSer.setBackground(Color.decode("#DFEDDD"));
        this.botonConexSer.setFont(new Font(letra, Font.BOLD, 12));

        this.botonDesconexSer = new JButton("Desconectar ▼");
        this.botonDesconexSer.setBackground(Color.decode("#EDDDDD"));
        this.botonDesconexSer.setFont(new Font(letra, Font.BOLD, 12));

        this.botonSelecArch = new JButton("Seleccionar ♦");
        this.botonSelecArch.setBackground(Color.decode("#B4B6CC"));
        this.botonSelecArch.setFont(new Font(letra, Font.BOLD, 12));

        this.botonEnviarArch = new JButton("Enviar ►");
        this.botonEnviarArch.setBackground(Color.decode("#B0C2BF"));
        this.botonEnviarArch.setFont(new Font(letra, Font.BOLD, 12));

        this.areaMensajes = new JTextArea("");

        this.imagenCliente = new ImageIcon("iconos/iconoCliente.png");
        this.labelIconoCliente.setIcon(this.imagenCliente);

        this.areaMensajes = new JTextArea(20,30);
        this.areaMensajes.setEnabled(false);

        this.barraAreaMensajes = new JScrollPane();
        this.barraAreaMensajes.setViewportView(this.areaMensajes);

        this.escogerArchivo = new JFileChooser();
        escogerArchivo.setDialogTitle("Escoger un archivo a enviar");
    }

    public void dimensionar(){

        setLayout(null);

        this.labelIconoCliente.setBounds(136,20,64,64);
        this.tituloCliente.setBounds(106,100, 140,10);

        this.tituloServidores.setBounds(30,130,140,10);
        this.textoServidor.setBounds(30,150,140,25);
        this.botonConexSer.setBounds(40,190, 110,30);
        this.botonDesconexSer.setBounds(170,190, 125,30);

        this.tituloArchivos.setBounds(30, 260, 140, 10);
        this.textoArchivo.setBounds(30,280, 250, 25);
        this.botonSelecArch.setBounds(45, 320, 120, 30);
        this.botonEnviarArch.setBounds(190,320,90,30);

        this.tituloMensajes.setBounds(30,380, 100,10);
        this.barraAreaMensajes.setBounds(30, 405, 275, 130);
    }

    public void adicionar(){

        this.add(labelIconoCliente);
        this.add(tituloCliente);
        this.add(tituloServidores);
        this.add(tituloArchivos);
        this.add(tituloMensajes);

        this.add(textoServidor);
        this.add(textoArchivo);

        this.add(botonConexSer);
        this.add(botonDesconexSer);
        this.add(botonSelecArch);
        this.add(botonEnviarArch);

        this.add(barraAreaMensajes);
    }

    public void visualizar(){
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(350,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonConexSer.addActionListener(this);
        this.botonDesconexSer.addActionListener(this);
        this.botonSelecArch.addActionListener(this);
        this.botonEnviarArch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.botonConexSer){
            int capturaPort = capturarPuerto();
            if (capturaPort != 0) conectar(capturaPort);
        }
        else if(e.getSource() == this.botonDesconexSer){
            desconectar();
        }
        else if(e.getSource() == this.botonSelecArch){
            seleccionarArchivo();
        }
        else if(e.getSource() == this.botonEnviarArch){
            enviarArchivos();
        }
    }

    private int capturarPuerto(){

        String capturaPuerto = textoServidor.getText();

        if (!capturaPuerto.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El dato debe ser numérico.", error, JOptionPane.ERROR_MESSAGE);
            textoServidor.setText("");
            return 0;
        }else{
            int puerto = Integer.parseInt(capturaPuerto);
            if(puerto < 0 || puerto > 65535){
                JOptionPane.showMessageDialog(this, "El puerto especificado no es válido (debe estar entre 0 y 65535)..", error, JOptionPane.ERROR_MESSAGE);
                textoServidor.setText("");
                return 0;
            }else{
                return puerto;
            }
        }
    }

    public void conectar(int puerto){
        JOptionPane.showMessageDialog(this, "Conectando con servidor", "Verificando ...",  JOptionPane.INFORMATION_MESSAGE);

        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", puerto);
                out = new DataOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }

            ejecucion = true;
            // Si la conexión fue exitosa, arranca el hilo para leer mensajes
            new Thread(() -> {
                try {
                    String fromServer;
                    while (ejecucion && (fromServer = in.readLine()) != null) {
                        areaMensajes.append("Servidor: " + fromServer + "\n");
                    }
                } catch (IOException ex) {
                    if ("Socket closed".equals(ex.getMessage())) {
                        JOptionPane.showMessageDialog(this,
                                "Desconectado del servidor correctamente",
                                "Desconexión",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }).start();

            JOptionPane.showMessageDialog(this, "Conexión establecida con el servidor.", "Completado ...", JOptionPane.INFORMATION_MESSAGE);

        }
        catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "El puerto especificado no es válido (debe estar entre 0 y 65535).",
                    error,
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) {
            // No mostramos el error en consola, solo advertimos al usuario
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la conexión al servidor indicado",
                    error,
                    JOptionPane.ERROR_MESSAGE);

            // Aseguramos que el socket quede cerrado
            if (socket != null) {
                try { socket.close(); } catch (IOException ignored) {}
                socket = null;
            }
        }
    }

    public void desconectar() {
        try {
            ejecucion = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al intentar desconectarse del servidor",
                    error,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seleccionarArchivo(){
        if(escogerArchivo.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            archivo[0] = escogerArchivo.getSelectedFile();
            textoArchivo.setText("Archivo: " + archivo[0].getName());
        }
    }

    public void enviarArchivos(){

        if (socket == null || socket.isClosed() || out == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay conexión con el servidor. Conéctese primero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(archivo[0] == null){
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar primero un archivo",
                    "Cuidado ..",
                    JOptionPane.INFORMATION_MESSAGE);
        }else{

            try (FileInputStream archivoEntrada = new FileInputStream(archivo[0])) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // Enviar nombre del archivo
                String nombreArchivo = archivo[0].getName();
                byte[] bytesNombreArch = nombreArchivo.getBytes();
                dos.writeInt(bytesNombreArch.length);
                dos.write(bytesNombreArch);

                // Enviar tamaño del archivo
                long tamanio = archivo[0].length();
                dos.writeLong(tamanio);

                // Enviar contenido del archivo por bloques
                byte[] buffer = new byte[4096];
                int bytesLeidos;
                while ((bytesLeidos = archivoEntrada.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesLeidos);
                }

                dos.flush();

                areaMensajes.append("Archivo enviado: " + nombreArchivo + "\n");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al enviar el archivo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                new ClientePrincipalArch();
            }
        });
    }
}
