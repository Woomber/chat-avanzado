/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI_Funcion;

import Exceptions.JsonParserException;
import GUI.JComponent_Favorito;
import GUI.JComponent_Grupo;
import GUI.JComponent_Usuario;
import GUI.JFrame_Conversacion;
import GUI.JFrame_Principal;
import General.MessageBox;
import Json.JsonParser;
import Models.Usuario;
import ModelsSerializables.UsuarioSerializable;
import PaquetesModels.Paquete;
import Requests.LoginRequest;
import Requests.UsuariosRequest;
import Responses.LoginResponse;
import Responses.UsuariosResponse;
import Threads.Thread_Transmitter;
import java.awt.Color;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author PC
 */
public class Funcion_Principal extends JFrame_Principal {

    ArrayList<Object> usuarios;
    public JPanel PanelUsuarios, PanelFavoritos, PanelGrupos;
    
    Thread_Transmitter transmitter;

    public Funcion_Principal() {
        PanelUsuarios = super.getPanelUsuarios();
        PanelFavoritos = super.getPanelFavoritos();
        PanelGrupos = super.getPanelGrupos();
        transmitter = Thread_Transmitter.transmitter;
        /*JComponent_Usuario com = new JComponent_Usuario("Malditasea", true);
        this.PanelUsuarios.add(com);*/
        super.setOnBtnGruposClick(() -> BtnGruposClick());
        super.setOnBtnFavoritosClick(() -> BtnFavoritosClick());
        super.setOnMenuSalirClick(() -> MenuSalirClick());
        LoadUsuarios();
        LoadGrupos();
        LoadFavoritos();
        
    }

    private void LoadUsuarios() {
        transmitter.setAction(
                (Socket socket, PrintWriter pw, BufferedReader read)
                -> listaUsuarios(socket, pw, read)
        );
        transmitter.StartThread();
    }

    private void LoadGrupos() {

    }

    private void LoadFavoritos() {

    }

    private void BtnGruposClick() {
        
    }

    private void BtnFavoritosClick() {

    }

    private void MenuSalirClick() {
        Usuario.emisor = null;
        new Funcion_Ingreso().setVisible(true);
        this.setVisible(false);
    }


    
    private void listaUsuarios(Socket socket, PrintWriter pw, BufferedReader read) {
        try {
            pw.println(JsonParser.paqueteToJson(new UsuariosRequest()));
            Paquete paquete = JsonParser.jsonToPaquete(read.readLine());
            //System.out.println(paquete.getValue(UsuariosResponse.USUARIOS));
            UsuarioSerializable[] b = JsonParser.jsonToUsuarios(paquete.getValue(UsuariosResponse.USUARIOS));
            UsuarioSerializable[] a = JsonParser.jsonToUsuarios(paquete.getValue(UsuariosResponse.AMIGOS));
            for (UsuarioSerializable c : b) {
                JComponent_Usuario com = new JComponent_Usuario(c.nombre, c.connected);
                System.out.println(com.toString());
                System.out.println(c.username + " " + String.valueOf(c.connected) + "\n");
                PanelUsuarios.add(com);
            }
            for (UsuarioSerializable c : a) {
                JComponent_Favorito com = new JComponent_Favorito(c.nombre, c.connected);
                System.out.println(com.toString());
                System.out.println(c.username + " " + String.valueOf(c.connected) + "\n");
                PanelFavoritos.add(com);
            }
            PanelUsuarios.revalidate();
            PanelFavoritos.revalidate();
        } catch (IOException | JsonParserException ex) {
            System.out.println("");
            System.out.println(ex.getMessage());
            System.out.println("");
        }
    }
    
}
