package org.vinni.cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Properties;

public class ClientePrincipalFinal extends JFrame implements ActionListener {

    private final String titulo = "Cliente principal";
    private final String letra = "Arial";
    private final String error = "Error";
    private volatile boolean ejecucion = false;

    private ArrayList<Integer> listaServidores = new ArrayList<>();
    private File[] archivo = new File[1];

    private int puerto;
    private int numeroIntenReco;
    private long tiempoEspera;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private JLabel labelIconoCliente;
    private JLabel tituloCliente;
    private JLabel tituloServidores;
    private JLabel tituloEscMensajes;
    private JLabel tituloMensajes;
    private JLabel tituloFuncionesMensajes;
    private JLabel tituloArchivos;


    private JTextField textoMensaje;
    private JTextField textoArchivo;

    private JComboBox comboListaServidores;
    private JComboBox comboListaFunciones;

    private JButton botonConexSer;
    private JButton botonDesconexSer;
    private JButton botonEnviarMensaje;
    private JButton botonBuscarServidores;
    private JButton botonSelecArch;
    private JButton botonEnviarArch;

    private JFileChooser escogerArchivo;

    private JScrollPane barraAreaMensajes;
    private JTextArea areaMensajes;

    private ImageIcon imagenCliente;

    public ClientePrincipalFinal(){
        cargarConfiguracion();

        iniciarlizarComponentes();
        dimensionar();
        adicionar();
        visualizar();
        accionar();
    }

    public void iniciarlizarComponentes(){

        this.labelIconoCliente = new JLabel("", JLabel.CENTER);
        this.tituloCliente = new JLabel("Cliente TCP: DFRACK");
        this.tituloServidores = new JLabel("lista de Servidores");
        this.tituloEscMensajes = new JLabel("Escribir mensaje:");
        this.tituloMensajes = new JLabel("Mensajes");
        this.tituloFuncionesMensajes = new JLabel("Enviar Mensaje / Archivo a:");
        this.tituloArchivos = new JLabel("Seleccionar Archivos");

        this.textoMensaje = new JTextField();

        this.textoArchivo = new JTextField();
        this.textoArchivo.setEditable(false);

        this.comboListaServidores = new JComboBox<>();
        this.comboListaServidores.addItem(puerto);
        this.comboListaServidores.setEnabled(false);

        this.comboListaFunciones = new JComboBox<>();

        this.botonBuscarServidores = new JButton("Buscar ■");
        this.botonBuscarServidores.setBackground(Color.decode("#BAC6E8"));
        this.botonBuscarServidores.setFont(new Font(letra, Font.BOLD, 13));
        this.botonBuscarServidores.setEnabled(false);

        this.botonConexSer = new JButton("Conectar ▲");
        this.botonConexSer.setBackground(Color.decode("#DFEDDD"));
        this.botonConexSer.setFont(new Font(letra, Font.BOLD, 12));

        this.botonDesconexSer = new JButton("Desconectar ▼");
        this.botonDesconexSer.setBackground(Color.decode("#EDDDDD"));
        this.botonDesconexSer.setFont(new Font(letra, Font.BOLD, 12));
        this.botonDesconexSer.setEnabled(false);

        this.botonEnviarMensaje = new JButton("Enviar ►");
        this.botonEnviarMensaje.setBackground(Color.decode("#B0C2BF"));
        this.botonEnviarMensaje.setFont(new Font(letra, Font.BOLD, 12));
        this.botonEnviarMensaje.setEnabled(false);

        this.botonSelecArch = new JButton("Seleccionar ♦");
        this.botonSelecArch.setBackground(Color.decode("#B4B6CC"));
        this.botonSelecArch.setFont(new Font(letra, Font.BOLD, 12));
        this.botonSelecArch.setEnabled(false);

        this.botonEnviarArch = new JButton("Enviar ►");
        this.botonEnviarArch.setBackground(Color.decode("#B0C2BF"));
        this.botonEnviarArch.setFont(new Font(letra, Font.BOLD, 12));
        this.botonEnviarArch.setEnabled(false);

        this.areaMensajes = new JTextArea("");

        this.imagenCliente = new ImageIcon("iconos/iconoCliente.png");
        this.labelIconoCliente.setIcon(this.imagenCliente);

        this.areaMensajes = new JTextArea(20,30);
        this.areaMensajes.setEditable(false);

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
        this.comboListaServidores.setBounds(30,150,140,25);
        this.botonBuscarServidores.setBounds(200, 145, 90,30);

        this.botonConexSer.setBounds(40,190, 110,30);
        this.botonDesconexSer.setBounds(170,190, 125,30);

        this.tituloFuncionesMensajes.setBounds(30, 250, 200, 10);
        this.comboListaFunciones.setBounds(30,270, 160, 25);

        this.tituloEscMensajes.setBounds(30, 315, 150, 10);
        this.textoMensaje.setBounds(30, 335, 250, 25);

        this.botonEnviarMensaje.setBounds(30, 370, 100, 30);

        this.tituloArchivos.setBounds(30, 420, 150, 10);
        this.textoArchivo.setBounds(30, 440, 250, 25);

        this.botonSelecArch.setBounds(30,475,120,30);
        this.botonEnviarArch.setBounds(170, 475, 100,30);

        this.tituloMensajes.setBounds(30,525, 100,10);
        this.barraAreaMensajes.setBounds(30, 550, 275, 130);
    }

    public void adicionar(){

        this.add(labelIconoCliente);
        this.add(tituloCliente);
        this.add(tituloServidores);
        this.add(tituloEscMensajes);
        this.add(tituloMensajes);
        this.add(tituloFuncionesMensajes);
        this.add(tituloArchivos);

        this.add(textoMensaje);
        this.add(textoArchivo);

        this.add(comboListaServidores);
        this.add(comboListaFunciones);

        //this.add(botonBuscarServidores);
        this.add(botonConexSer);
        this.add(botonDesconexSer);
        this.add(botonEnviarMensaje);
        this.add(botonSelecArch);
        this.add(botonEnviarArch);

        this.add(barraAreaMensajes);
    }

    public void visualizar(){
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(350,730);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonConexSer.addActionListener(this);
        this.botonDesconexSer.addActionListener(this);
        this.botonEnviarMensaje.addActionListener(this);
        this.botonBuscarServidores.addActionListener(this);
        this.botonSelecArch.addActionListener(this);
        this.botonEnviarArch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if(e.getSource() == this.botonConexSer){
                int capturaPort = capturarPuerto();
                String mensaje = "Conexion establecida satisfactoriamente";
                if (capturaPort != 0) conectar(capturaPort, mensaje);
            }
            else if(e.getSource() == this.botonDesconexSer){
                desconectar();
            }
            else if(e.getSource() == this.botonBuscarServidores){
                actualizarServidoresCombo();

                if(!listaServidores.isEmpty()){
                    this.botonConexSer.setEnabled(true);
                }
            }
            else if(e.getSource() == this.botonEnviarMensaje){
                enviarMensaje();
            }
            else if(e.getSource() == this.botonSelecArch){
                seleccionarArchivo();
                this.botonEnviarArch.setEnabled(true);
            }
            else if(e.getSource() == this.botonEnviarArch){
                enviarArchivo();
            }

        }
    private int capturarPuerto(){
            return (int) comboListaServidores.getSelectedItem();
        }

    public void conectar(int puerto, String mensajeConex){
        JOptionPane.showMessageDialog(this, "Intentando conectar con servidor", "Verificando ...",  JOptionPane.INFORMATION_MESSAGE);

        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", puerto);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
            }
            JOptionPane.showMessageDialog(this,
                    mensajeConex,
                    "CONECTADO",
                    JOptionPane.INFORMATION_MESSAGE);

            ejecucion = true;
            configurarBotones(1);

            new Thread(() -> {
                try {
                    while (ejecucion) {
                        String fromServer = in.readUTF();

                        if(fromServer.startsWith("Mensaje:")){
                            String mensaje = fromServer.substring(8);

                            if (mensaje.startsWith("LIST:")) {
                                comboListaFunciones.removeAllItems();
                                String[] clientes = mensaje.substring(5).split(",");
                                for (String c : clientes) {
                                    comboListaFunciones.addItem(c);
                                }
                            }else{
                                areaMensajes.append(mensaje + "\n");
                            }
                        }
                        else {

                            String mensaje = fromServer.substring(8);

                            int nombreLength = in.readInt();
                            byte[] nombreBytes = new byte[nombreLength];
                            in.readFully(nombreBytes);
                            String nombreArchivo = new String(nombreBytes);

                            long tamanio = in.readLong();

                            // Leer contenido
                            byte[] contenido = new byte[(int) tamanio];
                            in.readFully(contenido);

                            File archivoRecibido = new File("Archivos/Clientes", nombreArchivo);
                            try (FileOutputStream fos = new FileOutputStream(archivoRecibido)) {
                                fos.write(contenido);
                            }

                            areaMensajes.append(mensaje + "\n");
                        }
                    }
                } catch (IOException ex) {
                    if ("Socket closed".equals(ex.getMessage())) {
                        JOptionPane.showMessageDialog(this,
                                "Desconectado del servidor correctamente",
                                "Desconexión",
                                JOptionPane.INFORMATION_MESSAGE);
                        desconectar();
                    }else{
                        ejecucion = false;
                        JOptionPane.showMessageDialog(this,
                                "Conexión con el servidor perdida: " + ex.getMessage(),
                                "Desconexión",
                                JOptionPane.WARNING_MESSAGE);
                        desconectar();
                    }

                    reconexionServidor();

                }
            }).start();

            JOptionPane.showMessageDialog(this, "Conexión establecida con el servidor.", "Completado ...", JOptionPane.INFORMATION_MESSAGE);

        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la conexión al servidor indicado",
                    error,
                    JOptionPane.ERROR_MESSAGE);

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
            configurarBotones(2);
            comboListaFunciones.removeAllItems();
            areaMensajes.setText("");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al intentar desconectarse del servidor",
                    error,
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public void visualizarServidores(){
        for (int puerto = 15000; puerto <= 16000; puerto++) {
            try (Socket socket = new Socket("localhost", puerto)) {
                listaServidores.add(puerto);
            } catch (IOException e) {
                // No hay servidor en este puerto
            }
        }
    }

    public void actualizarServidoresCombo(){
        eliminarListaServidores();
        visualizarServidores();
        SwingUtilities.invokeLater(() -> {
            comboListaServidores.removeAllItems();
            for (Integer servidor : listaServidores) {
                comboListaServidores.addItem(servidor);
            }
        });
    }

    public void reconexionServidor(){
        JOptionPane.showMessageDialog(this,
                "Iniciando proceso de reconexion",
                error,
                JOptionPane.ERROR_MESSAGE);
        new Thread(() -> {
            boolean conexion = false;
            int i = 0;
            while (!conexion && i < numeroIntenReco) {
                try (Socket ignored = new Socket("127.0.0.1", puerto)) {

                    conexion = true;

                    String mensaje = "Se logró la reconexión satisfactoriamente";
                    conectar(puerto, mensaje);

                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "No se logro reconexion en el " + (i+1) + " intento, probando de nuevo en 3 segundos...",
                            error,
                            JOptionPane.ERROR_MESSAGE);
                    i++;
                    try {
                        TimeUnit.SECONDS.sleep(tiempoEspera);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(!conexion) {
                JOptionPane.showMessageDialog(this,
                        "No se logro conectarse al servidor despues de " + numeroIntenReco + " intentos",
                        error,
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    public void eliminarListaServidores(){
        listaServidores = new ArrayList<>();
        comboListaServidores.removeAllItems();
    }

    public void configurarBotones(int decision){

        switch (decision){
            case 1:
                this.botonConexSer.setEnabled(false);
                this.botonDesconexSer.setEnabled(true);
                this.botonEnviarMensaje.setEnabled(true);
                this.comboListaServidores.setEnabled(false);
                this.botonSelecArch.setEnabled(true);
                break;
            case 2:
                this.botonDesconexSer.setEnabled(false);
                this.botonConexSer.setEnabled(true);
                this.botonEnviarMensaje.setEnabled(false);
                this.botonSelecArch.setEnabled(false);
                this.botonEnviarArch.setEnabled(false);
                break;
            default:
                // no acciona nada
        }
    }

    private void enviarMensaje() {
        try {
            out.writeUTF("Mensaje:" + comboListaFunciones.getSelectedItem() + ":" + textoMensaje.getText());
            out.flush();
            textoMensaje.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al enviar mensaje.",
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

    public void enviarArchivo(){
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
                // avisamos al server que es un archivo
                out.writeUTF("Archivo:" + comboListaFunciones.getSelectedItem() + ":");

                String nombreArchivo = archivo[0].getName();
                byte[] bytesNombreArch = nombreArchivo.getBytes();
                out.writeInt(bytesNombreArch.length);
                out.write(bytesNombreArch);

                long tamanio = archivo[0].length();
                out.writeLong(tamanio);

                byte[] buffer = new byte[4096];
                int bytesLeidos;
                while ((bytesLeidos = archivoEntrada.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesLeidos);
                }

                out.flush();

                this.botonEnviarArch.setEnabled(false);
                this.textoArchivo.setText("");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al enviar el archivo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void cargarConfiguracion(){
        Properties propiedades = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            propiedades.load(fis);

            numeroIntenReco = Integer.parseInt(propiedades.getProperty("numeroIntenReco"));
            puerto = Integer.parseInt(propiedades.getProperty("puerto"));
            tiempoEspera = Integer.parseInt(propiedades.getProperty("tiempoEspera"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                new ClientePrincipalFinal();
            }
        });
    }
}
