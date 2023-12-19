import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class SIR {
    static final int NUM_PARAM_COMAND = 12;
    static final int MIN_VALORES_ESPERADOS = 3;
    static final int MIN_PARAMETROS_ESPERADOS = 7;
    static final int LIMITE_INF_PASSO = 0;
    static final int LIMITE_SUP_PASSO = 1;
    static final int NUM_DIA_MIN = 0;
    static final int NUM_METODOS = 2;
    static final String FORMAT = ".csv";
    static final String FICH_S_GNU = "dataS.dat";
    static final String FICH_I_GNU = "dataI.dat";
    static final String FICH_R_GNU = "dataR.dat";
    static final String FICH_GP = "file.gp";
    static final String NOME_FICHEIRO_PNG = "export_visual_graph.png";
    static final String PEDIR_DIAS = "Digite o número de dias desejado: ";
    static final String PEDIR_PASSO = "Digite o número do passo (h) desejado: ";

    static final double VALOR_MIN = 0.0;
    static final double VALOR_MAX = 1.0;
    static Scanner ler = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0) {
            executarPorComando(args);
        } else {
            exibirMenuPrincipal();
        }
    }

    public static void exibirMenuPrincipal() throws FileNotFoundException {
        int escolha, metodo = 0;
        double[] valoresParametros = new double[0], valoresInicias = new double[0], argumentos = new double[0];
        String[] columnNamesEstado = null, columnNamesParametros = null;
        String out = null;

        do {
            System.out.println();
            System.out.println("=== Menu Principal ===");
            System.out.println("|| 1. Colocar nome ficheiro dos Valores Iniciais");
            System.out.println("|| 2. Colocar nome ficheiro dos Parametros");
            System.out.println("|| 3. Colocar argumentos (Passo e NumDias)");
            System.out.println("|| 4. Escolher método");
            System.out.println("|| 5. Escrever nome do ficheiro de saída");
            System.out.println("|| 6. Realizar cálculos");
            System.out.println("|| 0. Fechar o programa");
            System.out.println();
            System.out.println("Digite a opção desejada: ");

            escolha = ler.nextInt();
            ler.nextLine(); // limpar buffer

            switch (escolha) {
                case 1:
                    System.out.print("Digite o nome do ficheiro que contém os valores Iniciais : ");
                    String ficheiroValorIni = ler.nextLine() + FORMAT;
                    valoresInicias = lerValoresIniciais(ficheiroValorIni);
                    columnNamesEstado = getColumnNames(ficheiroValorIni);
                    break;
                case 2:
                    System.out.print("Digite o nome do ficheiro que contém os valores dos Parametros : ");
                    String ficheiroParamentros = ler.nextLine() + FORMAT;
                    valoresParametros = lerParametros(ficheiroParamentros);
                    columnNamesParametros = getColumnNames(ficheiroParamentros);
                    break;
                case 3:
                    argumentos = colocarArgumentos();
                    break;
                case 4:
                    metodo = escolherMetodo();
                    break;
                case 5:
                    System.out.print("Digite o nome desejado para o ficheiro que contém os resultados : ");

                    out = ler.nextLine();
                    break;
                case 6:
                    double hCalculo = argumentos[1];
                    int numeroDeDiasCalculo = (int) argumentos[0];
                    verificarPlausibilidade(valoresInicias, valoresParametros);

                    double[] SCalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];
                    double[] ICalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];
                    double[] RCalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];

                    executarMetodo(metodo, SCalculo, ICalculo, RCalculo, hCalculo,
                            numeroDeDiasCalculo, valoresInicias, valoresParametros,
                            columnNamesEstado, columnNamesParametros);

                    escreverResultadosEmFicheiro(SCalculo, ICalculo, RCalculo, numeroDeDiasCalculo, out, hCalculo);
                    escreverPontosGnu(SCalculo, numeroDeDiasCalculo, FICH_S_GNU, hCalculo);
                    escreverPontosGnu(ICalculo, numeroDeDiasCalculo, FICH_I_GNU, hCalculo);
                    escreverPontosGnu(RCalculo, numeroDeDiasCalculo, FICH_R_GNU, hCalculo);
                    executarGP(FICH_GP);
                    break;
                case 0:
                    System.out.println("Programa encerrado.");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (escolha != 0);

        ler.close();
    }

    public static double[] colocarArgumentos() {
        double[] argumentos = new double[2];
        argumentos[0] = pedirNumeroDias(PEDIR_DIAS);
        argumentos[1] = pedirValorComUmPrint(LIMITE_INF_PASSO, LIMITE_SUP_PASSO, PEDIR_PASSO);
        return argumentos;
    }

    public static int escolherMetodo() {
        System.out.print("Digite (1) para o método de Euler ou (2) para o método de Runge-Kutta: ");
        int numMetodo = ler.nextInt();
        verificarComandoMetodo(numMetodo, LIMITE_INF_PASSO, NUM_METODOS);
        return numMetodo;
    }
    public static int pedirNumeroDias(String mensagem) {
        int numero;
        System.out.print(mensagem);
        do {
            while (!ler.hasNextInt()) {
                System.out.print("ERRO: Por favor, insira um número inteiro válido: ");
                ler.next();
            }
            numero = ler.nextInt();
            if (numero <= NUM_DIA_MIN) {
                System.out.print("ERRO: O valor introduzido é inválido.\nIntroduza um número maior que zero: ");
            }
        } while (numero <= NUM_DIA_MIN);
        return numero;
    }

    public static void verificarPlausibilidade(double[] valoresIniciais, double[] parametros) {
        verificarNumeroElementos(valoresIniciais, MIN_VALORES_ESPERADOS, "valores iniciais");
        verificarNumeroElementos(parametros, MIN_PARAMETROS_ESPERADOS, "parâmetros");
        if (saoValoresPlausiveis(valoresIniciais)) {
            System.out.println("ERRO: Valores iniciais não plausíveis.");
            System.exit(1);
        }
        if (saoValoresPlausiveis(parametros)) {
            System.out.println("ERRO: Parâmetros não plausíveis.");
            System.exit(1);
        }
    }

    public static void verificarNumeroElementos(double[] array, int numeroEsperado, String nome) {
        if (array.length < numeroEsperado) {
            System.out.println("ERRO: O número de " + nome + " deve ser " + numeroEsperado + ".");
            System.exit(1);
        }
    }

    public static boolean saoValoresPlausiveis(double[] valores) {
        for (int i = 0; i < valores.length; i++) {
            double valor = valores[i];
            if (valor < VALOR_MIN || valor > VALOR_MAX) {
                return true;
            }
        }
        return false;
    }

    public static void executarPorComando(String[] args) throws FileNotFoundException {
        if ((args[0].equals("-h") || args[0].equals("--help"))) {
            exibirMensagemAjuda();
            System.exit(0);
        } else if (args.length != NUM_PARAM_COMAND) {
            System.out.println("Erro: Número inválido de argumentos. Cada opção deve ter um valor correspondente.");
            exibirMensagemAjuda();
            System.exit(1);
        } else {
            String parametrosFile = obterValorArgumento(args, "-b");
            String condicoesIniciaisFile = obterValorArgumento(args, "-c");
            int metodo = Integer.parseInt(Objects.requireNonNull(obterValorArgumento(args, "-m")));
            double h = Double.parseDouble(Objects.requireNonNull(obterValorArgumento(args, "-p")));
            int numeroDeDias = Integer.parseInt(Objects.requireNonNull(obterValorArgumento(args, "-d")));
            String nomeFicheiro = obterValorArgumento(args, "-f");


            verificarComandoMetodo(metodo, LIMITE_INF_PASSO, NUM_METODOS);
            verificarComandoDias(numeroDeDias, NUM_DIA_MIN);
            verificarComandoPasso(h, LIMITE_INF_PASSO, LIMITE_SUP_PASSO);

            double[] valoresIniciais = lerValoresIniciais(condicoesIniciaisFile);
            String[] columnNamesEstado = getColumnNames(condicoesIniciaisFile);
            double[] parametros = lerParametros(parametrosFile);
            String[] columnNamesParametro = getColumnNames(parametrosFile);

            double[] S = new double[(int) (numeroDeDias / h)];
            double[] I = new double[(int) (numeroDeDias / h)];
            double[] R = new double[(int) (numeroDeDias / h)];


            executarMetodo(metodo, S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesEstado, columnNamesParametro);
            escreverResultadosEmFicheiro(S, I, R, numeroDeDias, nomeFicheiro, h);
            escreverPontosGnu(S, numeroDeDias, FICH_S_GNU, h);
            escreverPontosGnu(I, numeroDeDias, FICH_I_GNU, h);
            escreverPontosGnu(R, numeroDeDias, FICH_R_GNU, h);
            executarGP(FICH_GP);
        }
    }

    public static void verificarComandoMetodo(int num, int min, int max) {
        if (num < min || num > max) {
            System.out.println("Erro: Valor inválido para o método. Deve ser 1 (Euler) ou 2 (Runge-Kutta).");
            exibirMensagemAjuda();
            System.exit(1);
        }
    }

    public static void verificarComandoDias(int num, int min) {
        if (num < min) {
            System.out.println("Erro: Número inválido de dias. Deve ser maior que zero.");
            exibirMensagemAjuda();
            System.exit(1);
        }
    }

    public static void verificarComandoPasso(double num, int min, int max) {
        if (num < min || num >= max || verificarSomasSucessivasPasso(num)) {
            System.out.println("Erro: Valor inválido para o passo. Deve ser divisivel por 1 maior que zero e menor ou igual a um.");
            exibirMensagemAjuda();
            System.exit(1);
        }
    }
    public static void executarMetodo(int num, double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros, String[] columnNamesEstado, String[] columnNamesParametro) {
        if (num == 1) {
            aplicarEuler(S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesEstado, columnNamesParametro);
        } else if (num == 2) {
            aplicarRK4(S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesParametro);
        }
    }

    public static String obterValorArgumento(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }

    public static void exibirMensagemAjuda() {
        System.out.println("\nSIR - Modelo Epidemiológico\n");

        System.out.println("Opções:\n");
        System.out.println("  -b <arquivo>   Ficheiro de parâmetros (default: params_exemplo1.csv)");
        System.out.println("  -c <arquivo>   Ficheiro de condições iniciais (default: estado_inicial.csv)");
        System.out.println("  -m <metodo>    Método a usar (1-Euler ou 2-Runge Kutta de 4ª ordem) (default: 1)");
        System.out.println("  -p <passo>     Passo de integração h (maior que zero e menor ou igual a um) (default: 0.1)");
        System.out.println("  -d <dias>      Número de dias a considerar para análise (maior que zero) (default: 5)");
        System.out.println("  -f <arquivo>   Nome do ficheiro de saída CSV (default: resultados.csv)");
        System.out.println("  -h, --help     Exibir esta mensagem de ajuda\n");
    }



    public static double pedirValorComUmPrint(int min, int max, String inform) {
        double num;
        System.out.print(inform);
        do {
            num = ler.nextDouble();
            if (num <= min || num > max || verificarSomasSucessivasPasso(num)) {
                System.out.print("Erro: Valor inválido. Introduza um valor divisivel por 1 entre: [" + min + "," + max + "]: ");
                System.out.println();
            }
        } while (num <= min || num > max || verificarSomasSucessivasPasso(num));
        return num;
    }

    public static boolean verificarSomasSucessivasPasso(double passo) {
        boolean bool = false;
        int i = 0;
        while (passo * i <= 1 && !bool) {
            if (passo * i == 1) bool = true;
            i++;
        }
        return !bool;
    }

    public static int findColumnByName(String[] columns, String label) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(label)) {
                return i;
            }
        }
        return -1;
    }

    public static String[] getColumnNames(String file) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(file));
        return ler.next().split(";");
    }

    public static double[] lerValoresIniciais(String file) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(file));
        String[] valores = ler.next().split(";");

        ler.nextLine();
        double[] valoresIniciais = new double[valores.length];
        String[] splitStr = ler.next().split(";");

        for (int i = 0; i < splitStr.length; i++) {
            valoresIniciais[i] = Double.parseDouble(splitStr[i].replace(',', '.'));
        }
        ler.close();
        return valoresIniciais;
    }

    public static double[] lerParametros(String file) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(file));
        String[] valores = ler.next().split(";");

        ler.nextLine();
        double[] parametros = new double[valores.length];
        String[] splitStr = ler.next().split(";");

        for (int i = 0; i < splitStr.length; i++) {
            parametros[i] = Double.parseDouble(splitStr[i].replace(',', '.'));

        }
        ler.close();
        return parametros;
    }

    public static double fS(double S, double I, double[] parametros, String[] columnNamesParametro) {
        int indexLambda = findColumnByName(columnNamesParametro, "lambda");
        int indexMU = findColumnByName(columnNamesParametro, "mu");
        int index_b = findColumnByName(columnNamesParametro, "b");

        return (parametros[indexLambda] - (parametros[index_b] * S * I) - (parametros[indexMU] * S));
    }

    public static double fI(double S, double I, double R, double[] parametros, String[] columnNamesParametro) {
        int indexMU = findColumnByName(columnNamesParametro, "mu");
        int indexKapa = findColumnByName(columnNamesParametro, "kapa");
        int indexBeta = findColumnByName(columnNamesParametro, "beta");
        int index_b = findColumnByName(columnNamesParametro, "b");
        int indexDelta1 = findColumnByName(columnNamesParametro, "delta1");

        return (parametros[index_b] * S * I - parametros[indexKapa] * I + parametros[indexBeta] * I * R - (parametros[indexMU] + parametros[indexDelta1]) * I);
    }

    public static double fR(double I, double R, double[] parametros, String[] columnNamesParametro) {
        int indexMU = findColumnByName(columnNamesParametro, "mu");
        int indexKapa = findColumnByName(columnNamesParametro, "kapa");
        int indexBeta = findColumnByName(columnNamesParametro, "beta");
        int indexDelta2 = findColumnByName(columnNamesParametro, "delta2");

        return (parametros[indexKapa] * I - parametros[indexBeta] * I * R - (parametros[indexMU] + parametros[indexDelta2]) * R);
    }

    public static void aplicarEuler(double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros, String[] columnNamesEstado, String[] columnNamesParametro) {
        int indexS0 = findColumnByName(columnNamesEstado, "S0");
        int indexI0 = findColumnByName(columnNamesEstado, "I0");
        int indexR0 = findColumnByName(columnNamesEstado, "R0");

        S[0] = valoresIniciais[indexS0];
        I[0] = valoresIniciais[indexI0];
        R[0] = valoresIniciais[indexR0];
        for (int dia = 1; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            double dS = S[dia] + h * (fS(S[dia - 1], I[dia - 1], parametros, columnNamesParametro));
            double dI = I[dia] + h * (fI(S[dia - 1], I[dia - 1], R[dia - 1], parametros, columnNamesParametro));
            double dR = R[dia] + h * (fR(I[dia - 1], R[dia - 1], parametros, columnNamesParametro));
            S[dia] = S[dia - 1] + dS;
            I[dia] = I[dia - 1] + dI;
            R[dia] = R[dia - 1] + dR;
        }
    }

    public static void aplicarRK4(double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros, String[] columnNamesParametro) {
        S[0] = valoresIniciais[0];
        I[0] = valoresIniciais[1];
        R[0] = valoresIniciais[2];
        for (int dia = 1; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            double k1S = h * (fS(S[dia - 1], I[dia - 1], parametros, columnNamesParametro));
            double k1I = h * (fI(S[dia - 1], I[dia - 1], R[dia - 1], parametros, columnNamesParametro));
            double k1R = h * (fR(I[dia - 1], R[dia - 1], parametros, columnNamesParametro));

            double k2S = h * (fS(S[dia - 1] + h * k1S, I[dia - 1] + h * k1I, parametros, columnNamesParametro));
            double k2I = h * (fI(S[dia - 1] + h * k1S, I[dia - 1] + h * k1I, R[dia - 1] + h * k1R, parametros, columnNamesParametro));
            double k2R = h * (fR(I[dia - 1] + h * k1I, R[dia - 1] + h * k1R, parametros, columnNamesParametro));

            double k3S = h * (fS(S[dia - 1] + h * k2S, I[dia - 1] + h * k2I, parametros, columnNamesParametro));
            double k3I = h * (fI(S[dia - 1] + h * k2S, I[dia - 1] + h * k2I, R[dia - 1] + h * k2R, parametros, columnNamesParametro));
            double k3R = h * (fR(I[dia - 1] + h * k2I, R[dia - 1] + h * k2R, parametros, columnNamesParametro));

            double k4S = h * (fS(S[dia - 1] + h * k3S, I[dia - 1] + h * k3I, parametros, columnNamesParametro));
            double k4I = h * (fI(S[dia - 1] + h * k3S, I[dia - 1] + h * k3I, R[dia - 1] + h * k3R, parametros, columnNamesParametro));
            double k4R = h * (fR(I[dia - 1] + h * k3I, R[dia - 1] + h * k3R, parametros, columnNamesParametro));

            double kS = (k1S + 2 * k2S + 2 * k3S + k4S) / 6;
            double kI = (k1I + 2 * k2I + 2 * k3I + k4I) / 6;
            double kR = (k1R + 2 * k2R + 2 * k3R + k4R) / 6;

            S[dia] = S[dia - 1] + kS;
            I[dia] = I[dia - 1] + kI;
            R[dia] = R[dia - 1] + kR;
        }
    }

    public static void escreverResultadosEmFicheiro(double[] S, double[] I, double[] R, int numeroDeDias, String nomeDoFicheiro, double h) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter(nomeDoFicheiro + FORMAT);
        escrever.print("Dia;S;I;R;T\n");
        double varAux = 0;
        for (int dia = 0; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            if (eInteiro(varAux)) {
                int diaUnitario = (int) varAux;
                double total = S[dia] + I[dia] + R[dia];
                escrever.printf("%d;%.6f;%.6f;%.6f;%.6f%n", diaUnitario, S[dia], I[dia], R[dia], total);
            }
            varAux += h;
        }
        escrever.close();
    }

    public static boolean eInteiro(double numero) {
        return numero % 1 == 0;
    }

    public static void escreverPontosGnu(double[] parametro, int numeroDeDias, String ficheiroGNU, double h) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter(ficheiroGNU);
        double varAux = 0;
        for (int dia = 0; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            String valorFormatado = String.format("%.1f %.6f", varAux, parametro[dia]).replace(',', '.');
            escrever.println(valorFormatado);
            varAux += h;
        }
        escrever.close();
    }

    public static void executarGP(String nomeArquivo) {
        try {
            String diretorioAtual = System.getProperty("user.dir");
            String caminhoScriptGP = diretorioAtual + File.separator + nomeArquivo;
            String comando = "gnuplot " + caminhoScriptGP;
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", comando);
            pb.start();
            System.out.println("A imagem foi gerada com sucesso! O nome do arquivo é " + NOME_FICHEIRO_PNG + ".");
        } catch (IOException e) {
            System.out.println("Erro ao executar o script GnuPlot: " + e.getMessage());
        }
    }
}