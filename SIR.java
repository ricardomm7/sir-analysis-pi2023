import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class SIR {
    static final int NUM_PARAM_COMAND = 11;
    static final int MIN_VALORES_ESPERADOS = 3;
    static final int MIN_PARAMETROS_ESPERADOS = 7;
    static final int LIMITE_INF_PASSO = 0;
    static final int LIMITE_SUP_PASSO = 1;
    static final int NUM_DIA_MIN = 0;
    static final int NUM_METODOS = 2;
    static final String FORMAT = ".csv";
    static final String FICH_GP = "file.gp";
    static final String NOME_FICHEIRO_PNG = "multiplot_graficos.png";
    static final String PEDIR_DIAS = "Digite o número de dias desejado: ";
    static final String PEDIR_PASSO = "Digite o número do passo (h) desejado: ";
    static final String MENSAGEM_EXIT = "Digite 'exit' para regressar ao menu.";
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

    // EXECUTAR PROGRAMA NO TERMINAL
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
            String passo = (obterValorArgumento(args, "-p"));
            String passoI = Objects.requireNonNull(passo).replace(",", ".");// Caso o Utilizador digite ","
            double h = Double.parseDouble(passoI);

            int numeroDeDias = Integer.parseInt(Objects.requireNonNull(obterValorArgumento(args, "-d")));
            String nomeFicheiro = args[args.length - 1];


            verificarComandoMetodo(metodo, LIMITE_INF_PASSO, NUM_METODOS);
            verificarComandoDias(numeroDeDias, NUM_DIA_MIN);
            verificarComandoPasso(h, LIMITE_INF_PASSO, LIMITE_SUP_PASSO);

            String[] columnNamesEstado = getColumnNames(condicoesIniciaisFile);
            String[] columnNamesParametro = getColumnNames(parametrosFile);
            int quantCasos = obterQuantCasos(columnNamesParametro, parametrosFile);

            for (int i = 1; i <= quantCasos; i++) {

                double[] parametros = lerParametros(parametrosFile, i);
                double[] valoresIniciais = lerValoresIniciais(condicoesIniciaisFile, i);

                double[] S = new double[(int) (numeroDeDias / h) + 1];
                double[] I = new double[(int) (numeroDeDias / h) + 1];
                double[] R = new double[(int) (numeroDeDias / h) + 1];


                executarMetodo(metodo, S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesEstado,
                        columnNamesParametro);
                escreverResultadosEmFicheiro(S, I, R, numeroDeDias, nomeFicheiro, h, i);

                escreverPontosGnu(S, numeroDeDias, "dataS" + i + ".dat", h);
                escreverPontosGnu(I, numeroDeDias, "dataI" + i + ".dat", h);
                escreverPontosGnu(R, numeroDeDias, "dataR" + i + ".dat", h);
            }
            escreverScript(quantCasos);
            executarGP(FICH_GP);

        }
    }

    // VERIFICAÇÕES DAS ENTRADAS NA LINHA DE COMANDO
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

    public static String obterValorArgumento(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }

    // MENSAGEM DE AJUDA
    public static void exibirMensagemAjuda() {
        System.out.println("\nSIR - Modelo Epidemiológico\n");

        System.out.println("Opções:\n");
        System.out.println("  -b <arquivo>   Ficheiro de parâmetros.");
        System.out.println("  -c <arquivo>   Ficheiro de condições iniciais.");
        System.out.println("  -m <metodo>    Método a usar (1-Euler ou 2-Runge Kutta de 4ª ordem).");
        System.out.println("  -p <passo>     Passo de integração h (maior que zero e menor ou igual a um).");
        System.out.println("  -d <dias>      Número de dias a considerar para análise (maior que zero e divisivel por 1).");
        System.out.println("  <arquivo>   Nome do ficheiro de saída CSV.");
        System.out.println("  -h, --help     Exibir esta mensagem de ajuda.\n");
    }

    // EXECUTAR PROGRAMA MODO INTERATIVO
    public static void exibirMenuPrincipal() throws FileNotFoundException {
        int escolha, metodo = 0, quantCasos = 0;
        double[] valoresParametros, valoresInicias, argumentos = new double[0];
        String[] columnNamesEstado = null, columnNamesParametros = null;
        String out = null, ficheiroParamentros = null, ficheiroValorIni = null;

        do {
            System.out.println();
            System.out.println("=== Menu Principal ===");
            System.out.println("|| 1. Colocar nome ficheiro dos valores iniciais.");
            System.out.println("|| 2. Colocar nome ficheiro dos parâmetros.");
            System.out.println("|| 3. Colocar argumentos (passo e número de dias).");
            System.out.println("|| 4. Escolher método.");
            System.out.println("|| 5. Escrever nome do ficheiro de saída.");
            System.out.println("|| 6. Realizar cálculos.");
            System.out.println("|| 0. Fechar o programa.");
            System.out.println();
            System.out.println("Digite a opção desejada: ");

            escolha = ler.nextInt();
            ler.nextLine(); // limpar buffer

            switch (escolha) {
                case 1:
                    System.out.print(MENSAGEM_EXIT + "\nDigite o nome do ficheiro que contém os Valores Iniciais: ");

                    String input1 = ler.nextLine();

                    if (voltarMenu(input1)) {
                        continue;
                    }

                    ficheiroValorIni = input1 + FORMAT;
                    columnNamesEstado = getColumnNames(ficheiroValorIni);
                    break;
                case 2:
                    System.out.print(MENSAGEM_EXIT + "\nDigite o nome do ficheiro que contém os Valores dos Parâmetros: ");
                    String input2 = ler.nextLine();

                    if (voltarMenu(input2)) {
                        continue;
                    }

                    ficheiroParamentros = input2 + FORMAT;
                    columnNamesParametros = getColumnNames(ficheiroParamentros);
                    quantCasos = obterQuantCasos(columnNamesParametros, ficheiroParamentros);
                    break;
                case 3:
                    argumentos = colocarArgumentos();
                    break;
                case 4:
                    metodo = escolherMetodo();
                    break;
                case 5:
                    System.out.print(MENSAGEM_EXIT + "\nDigite o prefixo desejado para o ficheiro CSV que contém os resultados: ");
                    String input5 = ler.nextLine();

                    if (voltarMenu(input5)) {
                        continue;
                    }

                    out = input5;
                    break;
                case 6:
                    for (int i = 1; i <= quantCasos; i++) {
                        valoresInicias = lerValoresIniciais(ficheiroValorIni, i);
                        valoresParametros = lerParametros(ficheiroParamentros, i);
                        double hCalculo = argumentos[1];
                        int numeroDeDiasCalculo = (int) argumentos[0];

                        double[] SCalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];
                        double[] ICalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];
                        double[] RCalculo = new double[((int) (numeroDeDiasCalculo / hCalculo)) + 1];


                        executarMetodo(metodo, SCalculo, ICalculo, RCalculo, hCalculo,
                                numeroDeDiasCalculo, valoresInicias, valoresParametros,
                                columnNamesEstado, columnNamesParametros);

                        escreverResultadosEmFicheiro(SCalculo, ICalculo, RCalculo, argumentos[0],
                                out, argumentos[1], i);

                        escreverPontosGnu(SCalculo, numeroDeDiasCalculo, "dataS" + i + ".dat", hCalculo);
                        escreverPontosGnu(ICalculo, numeroDeDiasCalculo, "dataI" + i + ".dat", hCalculo);
                        escreverPontosGnu(RCalculo, numeroDeDiasCalculo, "dataR" + i + ".dat", hCalculo);
                    }
                    escreverScript(quantCasos);
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

    public static boolean voltarMenu(String input) {
        return input.equalsIgnoreCase("exit");
    }

    // ENTRADA DE VALORES
    public static double[] colocarArgumentos() throws FileNotFoundException {
        double[] argumentos = new double[2];
        argumentos[0] = pedirNumeroDias(PEDIR_DIAS);
        argumentos[1] = pedirValorComUmPrint(LIMITE_INF_PASSO, LIMITE_SUP_PASSO, PEDIR_PASSO);
        return argumentos;
    }

    public static int escolherMetodo() throws FileNotFoundException {
        System.out.print(MENSAGEM_EXIT + "\nDigite (1) para o método de Euler ou (2) para o método de Runge-Kutta: ");
        String input3 = ler.nextLine();

        if (voltarMenu(input3)) {
            exibirMenuPrincipal();
        }

        int numMetodo = Integer.parseInt(input3);
        verificarComandoMetodo(numMetodo, LIMITE_INF_PASSO, NUM_METODOS);
        return numMetodo;
    }

    public static int pedirNumeroDias(String mensagem) throws FileNotFoundException {
        int numero;
        System.out.print(MENSAGEM_EXIT + "\n" + mensagem);

        do {
            String input4 = ler.nextLine();
            if (voltarMenu(input4)) {
                exibirMenuPrincipal();
            }

            numero = Integer.parseInt(input4);
            if (numero <= NUM_DIA_MIN) {
                System.out.print("ERRO: O valor introduzido é inválido.\nIntroduza um número maior que zero: ");
            }

        } while (numero <= NUM_DIA_MIN);

        return numero;
    }

    public static double pedirValorComUmPrint(int min, int max, String inform) throws FileNotFoundException {
        double num;
        System.out.print("\n" +
                MENSAGEM_EXIT + "\n" + inform);
        do {
            String input4 = ler.nextLine();
            if (voltarMenu(input4)) {
                exibirMenuPrincipal();
            }
            input4 = input4.replace(",", ".");
            num = Double.parseDouble(input4);
            if (num <= min || num > max || verificarSomasSucessivasPasso(num)) {
                System.out.print("ERRO: O valor introduzido é inválido.\nIntroduza um valor entre: [" + min + "," + max + "]: ");
                System.out.println();
            }
        } while (num <= min || num > max || verificarSomasSucessivasPasso(num));
        return num;
    }

    // LEITURA DOS VALORES DO FICHEIRO COM ATENÇÃO AO INDICE
    public static int obterQuantCasos(String[] columnNamesParametros, String file) throws FileNotFoundException {
        int indexCaso = findColumnByName(columnNamesParametros, "caso");
        int contador = 0;

        Scanner ler = new Scanner(new File(file));

        if (ler.hasNextLine()) {
            ler.nextLine();
        }

        while (ler.hasNextLine()) {
            String linha = ler.nextLine();
            String[] colunas = linha.split(";");

            if (colunas.length > indexCaso + 1) {
                contador++;
            }
        }
        return contador;
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

    public static double[] lerValoresIniciais(String file, int casoN) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(file));

        ler.nextLine();

        double[] valoresIniciais;

        for (int i = 1; i < casoN; i++) {
            ler.nextLine();
        }

        String linha = ler.nextLine();

        String[] partes = linha.split(";");
        valoresIniciais = new double[partes.length];

        for (int i = 0; i < partes.length; i++) {
            String valor = partes[i].replace(",", ".");
            valoresIniciais[i] = Double.parseDouble(valor);
        }

        ler.close();
        verificarNumeroElementos(valoresIniciais, MIN_VALORES_ESPERADOS, "valores iniciais");
        verificarPlausibilidadeValoresIniciais(valoresIniciais);
        return valoresIniciais;
    }

    public static double[] lerParametros(String file, int casoN) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(file));

        ler.nextLine();
        double[] parametros;

        for (int i = 1; i < casoN; i++) {
            ler.nextLine();
        }

        String[] splitStr = ler.nextLine().split(";");
        parametros = new double[splitStr.length];

        for (int i = 0; i < splitStr.length; i++) {
            String valor = splitStr[i].replace(",", ".");
            parametros[i] = Double.parseDouble(valor);
        }

        ler.close();
        verificarNumeroElementos(parametros, MIN_PARAMETROS_ESPERADOS, "parâmetros");
        verificarPlausibilidadeParametros(parametros);
        return parametros;
    }

    // VERIFICAÇÕES
    public static void verificarNumeroElementos(double[] array, int numeroEsperado, String nome) {
        if (array.length < numeroEsperado) {
            System.out.println("ERRO: O número de " + nome + " deve ser " + numeroEsperado + ".");
            System.exit(1);
        }
    }

    public static void verificarPlausibilidadeValoresIniciais(double[] valores) {
        for (int i = 0; i < valores.length; i++) {
            double valor = valores[i];
            if (valor < VALOR_MIN || valor > VALOR_MAX) {
                System.out.println("ERRO: Valores iniciais inválidos.");
                System.exit(1);
            }
        }
    }

    public static void verificarPlausibilidadeParametros(double[] valores) {
        for (int i = 1; i < valores.length; i++) {
            double valor = valores[i];
            if (valor < VALOR_MIN || valor > VALOR_MAX) {
                System.out.println("ERRO: Valores dos parâmetros inválidos.");
                System.exit(1);
            }
        }
    }

    public static boolean eInteiro(double numero) {
        double parteDecimal = numero % 1;

        if (parteDecimal >= 0.9) {
            return true;
        }

        return parteDecimal == 0;
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

    // DEFENIR FUNÇÕES
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

        return (parametros[index_b] * S * I - parametros[indexKapa] * I + parametros[indexBeta] * I * R -
                (parametros[indexMU] + parametros[indexDelta1]) * I);
    }

    public static double fR(double I, double R, double[] parametros, String[] columnNamesParametro) {
        int indexMU = findColumnByName(columnNamesParametro, "mu");
        int indexKapa = findColumnByName(columnNamesParametro, "kapa");
        int indexBeta = findColumnByName(columnNamesParametro, "beta");
        int indexDelta2 = findColumnByName(columnNamesParametro, "delta2");

        return (parametros[indexKapa] * I - parametros[indexBeta] * I * R - (parametros[indexMU] + parametros[indexDelta2]) * R);
    }
    // REALIZAR CÁLCULOS

    public static void executarMetodo(int num, double[] S, double[] I, double[] R, double h, int numeroDeDias,
                                      double[] valoresIniciais, double[] parametros, String[] columnNamesEstado,
                                      String[] columnNamesParametro) {
        if (num == 1) {
            aplicarEuler(S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesEstado, columnNamesParametro);
        } else if (num == 2) {
            aplicarRK4(S, I, R, h, numeroDeDias, valoresIniciais, parametros, columnNamesParametro);
        }
    }

    // APLICAR MÉTODO EULER
    public static void aplicarEuler(double[] S, double[] I, double[] R, double h, int numeroDeDias,
                                    double[] valoresIniciais, double[] parametros, String[] columnNamesEstado,
                                    String[] columnNamesParametro) {
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

    // APLICAR MÉTODO RUNGE-KUTTA
    public static void aplicarRK4(double[] S, double[] I, double[] R, double h, int numeroDeDias,
                                  double[] valoresIniciais, double[] parametros, String[] columnNamesParametro) {
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

    // ESCREVER OS RESULTADOS EM FICHEIRO
    public static void escreverResultadosEmFicheiro(double[] S, double[] I, double[] R, double numeroDeDias,
                                                    String nomeDoFicheiro, double h, int caso) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter(nomeDoFicheiro + "_Caso_" + caso + FORMAT);
        escrever.print("Dia;S;I;R;T\n");
        double varAux = 0;
        for (int dia = 0; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            if (eInteiro(varAux)) {
                int diaUnitario = (int) trocarParaUmaCasaDecimal(varAux);
                double total = S[dia] + I[dia] + R[dia];
                escrever.printf("%d;%.6f;%.6f;%.6f;%.6f%n", diaUnitario, S[dia], I[dia], R[dia], total);
            }
            varAux += h;
        }
        escrever.close();
    }

    public static double trocarParaUmaCasaDecimal(double numero) {
        double parteInteira = Math.floor(numero);
        double parteDecimal = numero - parteInteira;

        if (parteDecimal >= 0.9) {
            parteInteira += 1.0;
        }

        return parteInteira;
    }

    //  REALIZAR GRÁFICO NO GNUPLOT
    public static void escreverPontosGnu(double[] parametro, int numeroDeDias, String ficheiroGNU,
                                         double h) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter(ficheiroGNU);
        double varAux = 0;
        for (int dia = 0; dia < ((int) (numeroDeDias / h)) + 1; dia++) {
            String valorFormatado = String.format("%.1f %.6f", varAux, parametro[dia]).replace(',', '.');
            escrever.println(valorFormatado);
            varAux += h;
        }
        escrever.close();
    }

    public static void escreverScript(int casos) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter("file.gp");

        // Calcular layout
        int layY = 2;  // Número de linhas fixo
        int layX = (casos + layY - 1) / layY;  // Número de colunas

        String scr = "set terminal pngcairo size 1000,750 enhanced font 'Verdana,12'\n" +
                "set output 'multiplot_graficos.png'\n" +
                "\n" +
                "set multiplot layout " + layY + "," + layX + "\n" +
                "\n";
        escrever.println(scr);

        for (int i = 1; i <= casos; i++) {
            // Definir posição do gráfico no layout
            int row = (i - 1) / layX + 1;  // Calcular linha
            int col = (i - 1) % layX + 1;  // Calcular coluna

            String aEscrever = "set title 'Caso " + i + "'\n" +
                    "set xlabel 'Dias'\n" +
                    "set ylabel 'Taxas'\n" +
                    "set origin " + (col - 1) / (double) layX + "," + (layY - row) / (double) layY + "\n" +
                    "set size 1.0/" + layX + "," + 1.0 / layY + "\n" +
                    "plot 'dataS" + i + ".dat' with linespoints linewidth 3 title 'Suscetibilidade',\\\n" +
                    "'dataI" + i + ".dat' with linespoints linewidth 3 title 'Infetados',\\\n" +
                    "'dataR" + i + ".dat' with linespoints linewidth 3 title 'Recuperados'\n";

            escrever.println(aEscrever);
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