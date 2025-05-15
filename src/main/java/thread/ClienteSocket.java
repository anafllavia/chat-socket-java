/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;


public class ClienteSocket implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private String nome;
    private final List<ClienteSocket> clientes;

    public ClienteSocket(Socket socket, List<ClienteSocket> clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            nome = entrada.readLine(); // Primeiro dado é o nome
            System.out.println("Novo cliente conectado: " + nome);
            enviarParaTodos("🟢 " + nome + " entrou no chat");

            String mensagem;
            while ((mensagem = entrada.readLine()) != null) {
                String mensagemFormatada = nome + ": " + mensagem;
                System.out.println("Mensagem recebida: " + mensagemFormatada);
                enviarParaTodos(mensagemFormatada);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + nome);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
            clientes.remove(this);
            enviarParaTodos("🔴 " + nome + " saiu do chat");
        }
    }

    private void enviarParaTodos(String mensagem) {
        synchronized (clientes) {
            for (ClienteSocket cliente : clientes) {
                cliente.enviarMensagem(mensagem);
            }
        }
    }

    public void enviarMensagem(String mensagem) {
        if (saida != null) {
            saida.println(mensagem);
        }
    }
}
