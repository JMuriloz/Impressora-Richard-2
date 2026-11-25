import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {

        // Caminho completo para a DLL
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );

        private static String lerArquivoComoString(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = fis.readAllBytes();
            fis.close();
            return new String(data, StandardCharsets.UTF_8);
        }

  // Interface Teste
        
        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin(int pino, int ti, int tf);

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    private static boolean conexaoAberta = false;
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;

    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // ---------------- LOGIN ----------------
    public static boolean login() {
        final String LOGIN_CORRETO = "admin";
        final String SENHA_CORRETA = "1234";
        int tentativas = 3;

        System.out.println("========== LOGIN ==========");
        while (tentativas > 0) {
            String usuario = capturarEntrada("Login: ");
            String senha = capturarEntrada("Senha: ");

            if (usuario.equals(LOGIN_CORRETO) && senha.equals(SENHA_CORRETA)) {
                System.out.println("Login realizado com sucesso!\n");
                return true;
            }

            tentativas--;
            if (tentativas > 0) {
                System.out.println("Login ou senha incorretos! Tentativas restantes: " + tentativas + "\n");
            }
        }

        System.out.println("Número máximo de tentativas excedido. Encerrando...");
        return false;
    }

    // ---------------- CONEXÃO ----------------
    public static void configurarConexao() {
        System.out.println("**** Configuração da impressora ****");
        tipo = Integer.parseInt(capturarEntrada("1 - USB\n" +
                "2 - RS2322\n" +
                "3 - TCP/IP\n" +
                "4 - Bluetooth\n" +
                "5 - Impressoras acopladas(Android)\n " +
                "Escolha o tipo de conexão(informe o número da escolha): "));
        modelo = capturarEntrada("Escolha o modelo: ");
        conexao = capturarEntrada("Informe o tipo de conexão\n" +
                "USB - USB\n" +
                "RS232 - COM2\n" +
                "TCP/IP - 192.168.0.20\n" +
                "Bluetooth - AA:BB:CC:DD:EE:FF\n" +
                "Escolha: ");
        parametro = Integer.parseInt(capturarEntrada("Informe o parametro: "));
        System.out.println("Configuração salva!");
    }

    public static void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("Conexão já aberta!");
            return;
        }
        int resultado = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
        if (resultado == 0) {
            conexaoAberta = true;
            System.out.println("Conexão aberta!");
        } else {
            System.out.println("Erro ao abrir a conexão. Codigo: " + resultado);
        }
    }

    public static void fecharConexao() {
        if (!conexaoAberta) {
            System.out.println("Nenhuma conexão aberta!");
            return;
        }
        int resultado = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (resultado == 0) {
            conexaoAberta = false;
            System.out.println("Conexão fechada!");
        } else {
            System.out.println("Erro ao fechar a conexão! Erro: " + resultado);
        }
    }

    // ---------------- IMPRESSÕES ----------------
    public static void ImpressaoTexto() {
        if (conexaoAberta) {
            System.out.println("Texto a ser impresso: ");
            String dados = scanner.nextLine();
            int resultado = ImpressoraDLL.INSTANCE.ImpressaoTexto(dados, 1, 4, 3);

            if (resultado == 0) {
                System.out.println("Impressão concluida");
            } else {
                System.out.println("Erro na impressão. Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    public static void corte() {
        ImpressoraDLL.INSTANCE.Corte(5);
    }

    public static void AvancaPapel() {
        ImpressoraDLL.INSTANCE.AvancaPapel(5);
    }

    public static void ImpressaoQRCode() {
        if (conexaoAberta) {
            int resultado = ImpressoraDLL.INSTANCE.ImpressaoQRCode("Teste", 6, 4);

            if (resultado == 0) {
                System.out.println("Impressão realizada com sucesso");
            } else {
                System.out.println("Problema ao imprimir! Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    public static void ImpressaoCodigodeBarras() {
        if (conexaoAberta) {
            int resultado = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A012345678912", 100, 2, 3);

            if (resultado == 0) {
                System.out.println("Codigo de barras impresso com sucesso!");
            } else {
                System.out.println("Erro ao imprimir codigo de barras! Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    public static void impressaoXMLSAT() {
        String dados = "XMLSAT.xml";

        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(dados, 0);
            if (retorno == 0) {
                System.out.println("Retorno impressão XML SAT: " + retorno);
                ImpressoraDLL.INSTANCE.Corte(6);
            }
        } else {
            System.out.println("Erro: impressora não conectada!");
        }
    }

    public static void impressaoXMLCancelamentoSAT() {
        String dados = "CANC_SAT.xml";

        String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(dados, assQRCode, 0);
            if (retorno == 0) {
                System.out.println("Retorno impressão XML Canc SAT: " + retorno);
                ImpressoraDLL.INSTANCE.Corte(6);
            }
        } else {
            System.out.println("Erro: impressora não conectada!");
        }
    }

    // ---------------- GAVETA ----------------
    public static void AbreGavetaElgin() {
        if (conexaoAberta) {
            int resultado = ImpressoraDLL.INSTANCE.AbreGavetaElgin(1, 50, 50);

            if (resultado == 0) {
                System.out.println("Gaveta aberta com sucesso");
            } else {
                System.out.println("Erro ao abrir a gaveta! Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    public static void AbreGaveta() {
        if (conexaoAberta) {
            int resultado = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);

            if (resultado == 0) {
                System.out.println("Gaveta aberta com sucesso");
            } else {
                System.out.println("Erro ao abrir a gaveta! Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    // ---------------- SINAL SONORO ----------------
    public static void SinalSonoro() {
        if (conexaoAberta) {
            int qtd;
            try {
                qtd = Integer.parseInt(capturarEntrada("Insira a quantidade de sinais: "));
            } catch (NumberFormatException e) {
                System.out.println("Quantidade inválida! Digite apenas números.");
                return;
            }

            if (qtd <= 0) {
                System.out.println("A quantidade deve ser maior que zero!");
                return;
            }

            int resultado = ImpressoraDLL.INSTANCE.SinalSonoro(qtd, 5, 5);

            if (resultado == 0) {
                System.out.println("Sinal emitido com sucesso");
            } else {
                System.out.println("Erro ao emitir o sinal! Erro: " + resultado);
            }
        } else {
            System.out.println("Conexão não iniciada!");
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        if (!login()) {
            return;
        }

        while (true) {
            System.out.println("\n*************************************************");
            System.out.println("**************** MENU IMPRESSORA ******************");
            System.out.println("*************************************************\n");
            System.out.println("1  - Configurar Conexao");
            System.out.println("2  - Abrir Conexao");
            System.out.println("3  - Impressao Texto");
            System.out.println("4  - Impressao QRCode");
            System.out.println("5  - Impressao Cod Barras");
            System.out.println("6  - Impressao XML SAT");
            System.out.println("7  - Impressao XML Canc SAT");
            System.out.println("8  - Abrir Gaveta Elgin");
            System.out.println("9  - Abrir Gaveta");
            System.out.println("10 - Sinal Sonoro");
            System.out.println("0  - Fechar Conexao e Sair");
            System.out.println("--------------------------------------");

            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;
                case "2":
                    abrirConexao();
                    break;
                case "3":
                    ImpressaoTexto();
                    AvancaPapel();
                    corte();
                    break;
                case "4":
                    ImpressaoQRCode();
                    AvancaPapel();
                    corte();
                    break;
                case "5":
                    ImpressaoCodigodeBarras();
                    AvancaPapel();
                    corte();
                    break;
                case "6":
                    impressaoXMLSAT();
                    AvancaPapel();
                    corte();
                    break;
                case "7":
                    impressaoXMLCancelamentoSAT();
                    AvancaPapel();
                    corte();
                    break;
                case "8":
                    AbreGavetaElgin();
                    break;
                case "9":
                    AbreGaveta();
                    break;
                case "10":
                    SinalSonoro();
                    break;
                case "0":
                    fecharConexao();
                    System.out.println("Encerrando...");
                    return;
                default:
                    System.out.println("Escolha invalida, tente novamente!");
            }
        }
    }
}
