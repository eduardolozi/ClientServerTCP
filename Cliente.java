package TesteAvaliativo;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Cliente {
    public static void main(String[] args) {
        File arquivo = new File("ArquivosParaMandar\\arquivo.txt");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\mysql_init.txt");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\arquivo_recebido.txt");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\messiTCP.jpg");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\teclado.png");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\NeonLights.jpg");
        enviarArquivoAoServidor(arquivo);

        arquivo = new File("ArquivosParaMandar\\ArmazenamentoNaNuvem.pdf");
        enviarArquivoAoServidor(arquivo);

        listarArquivosDoServidor();

        baixarArquivoDoServidor("arquivo.txt");
        baixarArquivoDoServidor("arquivo_recebido.txt");
        baixarArquivoDoServidor("messiTCP.jpg");
        baixarArquivoDoServidor("mysql_init.txt");
        baixarArquivoDoServidor("NeonLights.jpg");
        baixarArquivoDoServidor("teclado.png");
        baixarArquivoDoServidor("ArmazenamentoNaNuvem.pdf");

        fecharServidor();
    }

    public static void enviarArquivoAoServidor(File arquivo) {
        byte[] arquivoEmBytes = new byte[(int) arquivo.length()];
        try {
            Socket conexao = new Socket("localhost", 12345);

            //cria um fluxo de entrada para ler o arquivo
            FileInputStream fis = new FileInputStream(arquivo);

            //cria um fluxo de entrada em buffer para melhorar a eficiencia da leitura do arquivo
            BufferedInputStream bis = new BufferedInputStream(fis);

            //le o conteudo do buffer e armazena no arquivoEmBytes
            bis.read(arquivoEmBytes, 0, arquivoEmBytes.length);
            fis.close();

            //Obtem o fluxo de saída do socket(conexao). Isso permite enivar dados para o outro lado da conexao
            OutputStream saida = conexao.getOutputStream();

            //Usa o fluxo de saída criado pelo objeto "saída". É usado para transferir tipos primitivos de maneira mais adequada
            DataOutputStream saidaDados = new DataOutputStream(saida);
            saidaDados.writeUTF("1"); //manda ao servidor a opçao 1: ENVIAR ARQUIVO
            saidaDados.writeUTF(arquivo.getName()); //manda o nome do arquivo para o servidor
            saidaDados.writeInt(arquivoEmBytes.length); //manda o tamanho em bytes para o servidor
            saidaDados.write(arquivoEmBytes, 0, arquivoEmBytes.length); //escreve o conteúdo do arquivo no fluxo de saída
            conexao.close();
            System.out.println("Arquivo " + arquivo.getName() + " enviado");

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listarArquivosDoServidor() {
        try {
            Socket conexao = new Socket("localhost", 12345);

            OutputStream saida = conexao.getOutputStream();
            DataOutputStream saidaDados = new DataOutputStream(saida);

            saidaDados.writeUTF("2");
            conexao.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void baixarArquivoDoServidor(String fileName) {
        try{
            // Informando o arquivo a ser baixado
            Socket conexao = new Socket("localhost", 12345);

            OutputStream saida = conexao.getOutputStream();
            DataOutputStream saidaDados = new DataOutputStream(saida);

            saidaDados.writeUTF("3");
            saidaDados.writeUTF(fileName);

            InputStream entrada = conexao.getInputStream();
            DataInputStream entradaDados = new DataInputStream(entrada);

            // Recebendo o arquivo baixado
            int fileSize = entradaDados.readInt();
            //Serve para escrever os dados do arquivo em um arquivo no servidor
            FileOutputStream fos = new FileOutputStream("ArquivosBaixados\\" + fileName);

            byte[] conteudoArquivoEmBytes = new byte[(int)fileSize];

            int bytesLidos;
            while((bytesLidos = entradaDados.read(conteudoArquivoEmBytes)) != -1) {
                fos.write(conteudoArquivoEmBytes, 0, bytesLidos);
            }
            fos.close();
            conexao.close();

            System.out.println("Arquivo " + fileName + " baixado com sucesso!");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fecharServidor() {
        try {
            Socket conexao = new Socket("localhost", 12345);

            OutputStream saida = conexao.getOutputStream();
            DataOutputStream saidaDados = new DataOutputStream(saida);

            saidaDados.writeUTF("4");
            conexao.close();
            System.out.println("Conexão com o servidor finalizada!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
