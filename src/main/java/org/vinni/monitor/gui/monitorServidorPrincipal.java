package org.vinni.monitor.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class monitorServidorPrincipal extends JFrame implements ActionListener {

    private volatile boolean ejecucion = true;
    private int puerto;
    private int numeroIntRecoMoni;
    private int tiempoEspera;
    private Process procesoServidor;
    private Thread monitor;
    private String rutaAccesoJarSev;
    private JLabel labelIconoServidor;
    private JLabel tituloMonitor;
    private JLabel tituloEstadoServ;
    private JLabel tituloNumPuerto;

    private JTextField campoTextEstServ;
    private JTextField campoTextPuerto;

    private JButton botonEncenderMoni;
    private JButton botonApagarMoni;

    
    public monitorServidorPrincipal(){

        configurarVariables();

        inicializarComponentes();
        dimensionar();
        adicionar();
        visualizar();
        accionar();
    }

    public void inicializarComponentes(){
        ImageIcon imagenMonitor;
        String letra = "Arial";

        this.labelIconoServidor = new JLabel("", SwingConstants.CENTER);

        this.tituloMonitor = new JLabel("Monitor del servidor principal");

        this.tituloMonitor.setFont(new Font(letra, Font.BOLD, 20));

        this.tituloEstadoServ = new JLabel("Estado del servidor:");
        this.tituloEstadoServ.setFont(new Font(letra, Font.BOLD, 15));

        this.tituloNumPuerto = new JLabel("Puerto servidor:");
        this.tituloNumPuerto.setFont(new Font(letra, Font.BOLD, 15));

        this.campoTextEstServ = new JTextField();
        this.campoTextEstServ.setEditable(false);

        this.campoTextPuerto = new JTextField();
        this.campoTextPuerto.setText(String.valueOf(puerto));

        this.campoTextPuerto.setEditable(false);

        this.botonEncenderMoni = new JButton("Encender ▲");
        this.botonEncenderMoni.setBackground(Color.decode("#DFEDDD"));
        this.botonEncenderMoni.setFont(new Font(letra, Font.BOLD, 15));

        this.botonApagarMoni = new JButton("Apagar ▼");
        this.botonApagarMoni.setBackground(Color.decode("#EDDDDD"));
        this.botonApagarMoni.setFont(new Font(letra, Font.BOLD, 15));
        this.botonApagarMoni.setEnabled(false);

        imagenMonitor = new ImageIcon("iconos/iconoMonitor.png");
        this.labelIconoServidor.setIcon(imagenMonitor);
    }

    public void dimensionar(){
        setLayout(null);

        this.labelIconoServidor.setBounds(160,20,64,64);

        this.tituloMonitor.setBounds(53, 100, 290, 20);

        this.botonEncenderMoni.setBounds(50, 150, 130, 30);
        this.botonApagarMoni.setBounds(212, 150, 120, 30);

        this.tituloEstadoServ.setBounds(50, 220, 160, 20);
        this.campoTextEstServ.setBounds(50, 250,150, 25);

        this.tituloNumPuerto.setBounds(220, 220, 140, 20);
        this.campoTextPuerto.setBounds(220, 250,100, 25);
    }

    public void adicionar(){
        add(labelIconoServidor);

        add(tituloMonitor);
        add(tituloEstadoServ);
        add(tituloNumPuerto);

        add(campoTextEstServ);
        add(campoTextPuerto);

        add(botonEncenderMoni);
        add(botonApagarMoni);

    }

    public void visualizar(){
        String titulo = "Aplicación monitor";
        
        this.setTitle(titulo);
        this.setVisible(true);
        this.setSize(400,350);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public void accionar(){
        this.botonEncenderMoni.addActionListener(this);
        this.botonApagarMoni.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.botonEncenderMoni){
            encender();
        }
    }

    public void encender() {
        try {
            JOptionPane.showMessageDialog(this,
                    "Iniciando servidor TCP...",
                    "Proceso de apertura",
                    JOptionPane.INFORMATION_MESSAGE);

            encenderServidor();
            iniciarMonitoreo();

            configurarBotones(1);
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,
                    "Error al intentar iniciar el servidor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public void apagarMonitor() {
        ejecucion = false;
        if (monitor != null && monitor.isAlive()) {
            monitor.interrupt();
            monitor = null;
            configurarBotones(2);
        }
    }

    public boolean encenderServidor() {
        try {

            if (procesoServidor != null && procesoServidor.isAlive()) {
                JOptionPane.showMessageDialog(this,
                        "El servidor ya está en ejecución.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", rutaAccesoJarSev);
            pb.inheritIO();
            procesoServidor = pb.start();

            actualizarEstado(1);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar el servidor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public void iniciarMonitoreo(){
        monitor = new Thread( () ->{
            int i = 0;

            while (ejecucion){
                try {

                    if (procesoServidor == null || !procesoServidor.isAlive()) {
                        if (i < numeroIntRecoMoni) {
                            i++;
                            if (i == 1){
                                JOptionPane.showMessageDialog(this,
                                        "Iniciando proceso de reconexión del servidor",
                                        "Proceso de reconexión",
                                        JOptionPane.INFORMATION_MESSAGE);
                                actualizarEstado(2);
                            }

                            JOptionPane.showMessageDialog(this,
                                    "⚠️ Servidor caído intento " + i + ", intentando nuevamente en " + tiempoEspera +" segundos...",
                                    "Proceso de Reconexion",
                                    JOptionPane.INFORMATION_MESSAGE);

                            if(encenderServidor()){
                                i = 0;
                                actualizarEstado(1);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Límite de intentos alcanzados ("+ numeroIntRecoMoni +"). No se intentará más.",
                                    "Reconexión fallida",
                                    JOptionPane.ERROR_MESSAGE);

                            apagarMonitor();
                            i = 0;
                            ejecucion = false;
                        }

                    }

                    TimeUnit.SECONDS.sleep(tiempoEspera);
                } catch (InterruptedException e) {
                    ejecucion = false;
                }
            }
        });

        monitor.start();
    }
    private void configurarVariables(){
        Properties propiedades = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            propiedades.load(fis);

            numeroIntRecoMoni = Integer.parseInt(propiedades.getProperty("numeroIntRecoMoni"));
            puerto = Integer.parseInt(propiedades.getProperty("puerto"));
            tiempoEspera = Integer.parseInt(propiedades.getProperty("tiempoEspera"));
            rutaAccesoJarSev = propiedades.getProperty("rutaAccesoJarSev");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void actualizarEstado(int estado){
        switch (estado){
            case 1:
                this.campoTextEstServ.setText("Encendido");
                break;
            case 2:
                this.campoTextEstServ.setText("Apagado");
                break;
            default:
                break;
        }
    }
    public void configurarBotones(int decision){
        switch (decision){
            case 1:
                this.botonEncenderMoni.setEnabled(false);
                this.botonApagarMoni.setEnabled(true);
                break;
            case 2:
                this.botonEncenderMoni.setEnabled(true);
                this.botonApagarMoni.setEnabled(false);
                break;
            default:
                break;
        }
    }
    public  static void main(String args[]){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new monitorServidorPrincipal().setVisible(true);
            }
        });
    }
}
