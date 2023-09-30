package TesteAvaliativo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Servidor {

    private static List<String> fileListServer = new ArrayList<>();

    public static void main(String[] args) {
        try {
            //Responsável por esperar a conexão do cliente na porta 12345
            ServerSocket srvSocket = new ServerSocket(12345);
            System.out.println("Aguardando conexão do cliente...\n");
            System.out.println("----------------------------------------------------------------------");

            boolean bool = true;
            while (bool) {
                //Vai criar a conexão quando o cliente se conectar ao servidor
                Socket conexao = srvSocket.accept();

                //Cria um fluxo de entrada na conexão. InputStream é usado para receber dados no servidor
                InputStream entrada = conexao.getInputStream();

                //Usa o fluxo de entrada do socket, é usado para ler tipos primitivos de forma mais adequada
                DataInputStream entradaDados = new DataInputStream(entrada);

                //Recebe uma string do fluxo de entrada de dados que foi enviada pelo usuário para indicar que ação ele quer escolher
                String option = entradaDados.readUTF();

                if (option.equals("1")) {
                    recebeArquivoDoCliente(entradaDados);
                } else if (option.equals("2")) {
                    listarArquivosNoServidor();
                } else if (option.equals("3")) {
                    String fileName = entradaDados.readUTF();
                    baixarArquivo(fileName, conexao);
                } else if(option.equals("4")) {
                    bool = false;
                    conexao.close();
                    System.out.println("Conexão com o cliente finalizada!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recebeArquivoDoCliente(DataInputStream entradaDados) {
        try {
            //o objeto vai receber os dados do fluxo, que vão ser o nome do arquivo enviado ao servidor
            String fileName = entradaDados.readUTF();

            //vai receber o tamanho do arquivo que foi mandando ao servidor
            int fileSize = entradaDados.readInt();

            //Serve para escrever os dados do arquivo em um arquivo no servidor
            FileOutputStream fos = new FileOutputStream("ArquivosServidor\\" + fileName);

            byte[] conteudoArquivoEmBytes = new byte[(int)fileSize];

            int bytesLidos;

            //entradaDados.read(conteudoArquivoEmBytes) serve para ler os bytes do fluxo de entrada para o conteudoArquivoEmBytes, e ele retorna o número de bytes lidos
            while((bytesLidos = entradaDados.read(conteudoArquivoEmBytes)) != -1) {
                fos.write(conteudoArquivoEmBytes, 0, bytesLidos); //o conteúdo do arquivoEmBytes é gravado para o FileOutputStream
            }

            fos.close();
            fileListServer.add(fileName);
            System.out.println("Arquivo " + fileName + " gravado com sucesso!");
            System.out.println("----------------------------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listarArquivosNoServidor() {
        System.out.println("LISTA DE ARQUIVOS NO SERVIDOR");
        for(String fileName: fileListServer) {
            System.out.println(fileName);
        }
        System.out.println("----------------------------------------------------------------------");
    }

    public static void baixarArquivo(String fileName, Socket conexao) {
        try {
            OutputStream saida = conexao.getOutputStream();
            DataOutputStream saidaDados = new DataOutputStream(saida);

            if(fileListServer.contains(fileName)) {
                File arquivo = new File("ArquivosServidor\\" + fileName);
                byte[] arquivoEmBytes = new byte[(int)arquivo.length()];
                FileInputStream fis = new FileInputStream(arquivo);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(arquivoEmBytes, 0, arquivoEmBytes.length);

                saidaDados.writeInt(arquivoEmBytes.length);
                saidaDados.write(arquivoEmBytes, 0, arquivoEmBytes.length);

                saidaDados.close();
                System.out.println("Arquivo " + fileName + " enviado para o cliente.");
            } else {
                System.out.println("Arquivo não encontrado no servidor!");
            }
            System.out.println("----------------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
