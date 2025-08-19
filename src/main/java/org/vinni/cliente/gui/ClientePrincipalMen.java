package org.vinni.cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientePrincipalMen extends JFrame implements ActionListener {
    private final String titulo = "Cliente principal";
    private final String letra = "Arial";
    private final String error = "Error";
    private volatile boolean ejecucion = false;

    private ArrayList<Integer> listaServidores = new ArrayList<>();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JLabel labelIconoCliente;
    private JLabel tituloCliente;
    private JLabel tituloServidores;
    private JLabel tituloArchivos;
    private JLabel tituloMensajes;
    private JLabel tituloFuncionesMensajes;

    private JTextField textoMensaje;

    private JComboBox comboListaServidores;
    private JComboBox comboListaFunciones;

    private JButton botonConexSer;
    private JButton botonDesconexSer;
    private JButton botonEnviarMensaje;
    private JButton botonBuscarServidores;

    private JScrollPane barraAreaMensajes;
    private JTextArea areaMensajes;
    private ImageIcon imagenCliente;

    public ClientePrincipalMen(){
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
        this.tituloArchivos = new JLabel("Escribir mensaje:");
        this.tituloMensajes = new JLabel("Mensajes");
        this.tituloFuncionesMensajes = new JLabel("Enviar Mensaje a:");

        this.textoMensaje = new JTextField();

        this.comboListaServidores = new JComboBox<>();
        this.comboListaFunciones = new JComboBox<>();

        this.botonBuscarServidores = new JButton("Buscar ■");
        this.botonBuscarServidores.setBackground(Color.decode("#BAC6E8"));
        this.botonBuscarServidores.setFont(new Font(letra, Font.BOLD, 13));

        this.botonConexSer = new JButton("Conectar ▲");
        this.botonConexSer.setBackground(Color.decode("#DFEDDD"));
        this.botonConexSer.setFont(new Font(letra, Font.BOLD, 12));
        this.botonConexSer.setEnabled(false);

        this.botonDesconexSer = new JButton("Desconectar ▼");
        this.botonDesconexSer.setBackground(Color.decode("#EDDDDD"));
        this.botonDesconexSer.setFont(new Font(letra, Font.BOLD, 12));
        this.botonDesconexSer.setEnabled(false);

        this.botonEnviarMensaje = new JButton("Enviar ►");
        this.botonEnviarMensaje.setBackground(Color.decode("#B0C2BF"));
        this.botonEnviarMensaje.setFont(new Font(letra, Font.BOLD, 12));
        this.botonEnviarMensaje.setEnabled(false);

        this.areaMensajes = new JTextArea("");

        this.imagenCliente = new ImageIcon("iconos/iconoCliente.png");
        this.labelIconoCliente.setIcon(this.imagenCliente);

        this.areaMensajes = new JTextArea(20,30);
        this.areaMensajes.setEditable(false);

        this.barraAreaMensajes = new JScrollPane();
        this.barraAreaMensajes.setViewportView(this.areaMensajes);

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

        this.tituloArchivos.setBounds(30, 260, 140, 10);
        this.textoMensaje.setBounds(30,280, 250, 25);

        this.tituloFuncionesMensajes.setBounds(30, 315, 150, 10);
        this.comboListaFunciones.setBounds(30, 335, 150, 25);

        this.botonEnviarMensaje.setBounds(30, 375, 100, 30);

        this.tituloMensajes.setBounds(30,425, 100,10);
        this.barraAreaMensajes.setBounds(30, 450, 275, 130);
    }

    public void adicionar(){

        this.add(labelIconoCliente);
        this.add(tituloCliente);
        this.add(tituloServidores);
        this.add(tituloArchivos);
        this.add(tituloMensajes);
        this.add(tituloFuncionesMensajes);

        this.add(textoMensaje);

        this.add(comboListaServidores);
        this.add(comboListaFunciones);

        this.add(botonBuscarServidores);
        this.add(botonConexSer);
        this.add(botonDesconexSer);
        this.add(botonEnviarMensaje);

        this.add(barraAreaMensajes);
    }

    public void visualizar(){
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(350,650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonConexSer.addActionListener(this);
        this.botonDesconexSer.addActionListener(this);
        this.botonEnviarMensaje.addActionListener(this);
        this.botonBuscarServidores.addActionListener(this);
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
        else if(e.getSource() == this.botonBuscarServidores){
            actualizarServidoresCombo();

            if(!listaServidores.isEmpty()){
                this.botonConexSer.setEnabled(true);
            }
        }
        else if(e.getSource() == this.botonEnviarMensaje){
            enviarMensaje();
        }

    }

    private int capturarPuerto(){
        return (int) comboListaServidores.getSelectedItem();
    }

    public void conectar(int puerto){
        JOptionPane.showMessageDialog(this, "Conectando con servidor", "Verificando ...",  JOptionPane.INFORMATION_MESSAGE);

        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", puerto); // Asume que el servidor está en localhost y escucha en el puerto 5555
                out = new PrintWriter(socket.getOutputStream(), true);
            }

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ejecucion = true;

            configurarBotones(1);

            new Thread(() -> {
                try {
                    String fromServer;
                    while (ejecucion && (fromServer = in.readLine()) != null) {
                        if (fromServer.startsWith("LIST:")) {
                            // Actualizar comboBox
                            comboListaFunciones.removeAllItems();
                            String[] clientes = fromServer.substring(5).split(",");

                            for (String c : clientes) {
                                comboListaFunciones.addItem(c);
                            }
                        } else {
                            areaMensajes.append(fromServer + "\n");
                        }
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
                this.botonBuscarServidores.setEnabled(false);
                this.comboListaServidores.setEnabled(false);
                break;
            case 2:
                this.botonDesconexSer.setEnabled(false);
                this.botonConexSer.setEnabled(true);
                this.botonBuscarServidores.setEnabled(true);
                this.botonEnviarMensaje.setEnabled(false);
                this.comboListaServidores.setEnabled(true);
                break;
            default:
                // no acciona nada
        }
    }

    private void enviarMensaje() {
        out.println(comboListaFunciones.getSelectedItem() + ":" + textoMensaje.getText());
        textoMensaje.setText("");
    }

    public static void main(String[] args){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                new ClientePrincipalMen();
            }
        });
    }
}
