import java.io.*;
import java.util.Scanner;

public class Test {
    static final String VALORES_INICIAIS = "estado_inicial.csv";
    static final String PARAMETROS = "params_exemplo1.csv";
    static final int LIMITE_INF_PASSO = 0;
    static final int LIMITE_SUP_PASSO = 1;
    static final int NUM_DIA_MIN = 1;
    static final int NUM_DIA_MAX = 1000000000;
    static final int NUM_METODOS = 2;
    static final String FORMAT =".csv";
    static final String PEDIR_DIAS ="Digite o número de dias desejado :";
    static final String PEDIR_PASSO ="Digite o número do passo (h) desejado :";
    static final String PEDIR_METODO ="Digite (1) caso queira aplicar o método de Euler ou digite (2) caso queira aplicar o método de Runge-Kutta de quarta ordem";
    static Scanner ler = new Scanner(System.in);

    static final String RESULTADOS_DEFAULT = "resultados.csv";


    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
            exibirMensagemAjuda();
            System.exit(0);
        }
        String parametrosFile = obterValorArgumento(args, "-b", PARAMETROS);
        String condicoesIniciaisFile = obterValorArgumento(args, "-c", VALORES_INICIAIS);
        int metodo = Integer.parseInt(obterValorArgumento(args, "-m", "1"));
        double passo = Double.parseDouble(obterValorArgumento(args, "-p", "0.1"));
        int numeroDeDias = Integer.parseInt(obterValorArgumento(args, "-d", "5"));
        String nomeFicheiro = obterValorArgumento(args, "-f", RESULTADOS_DEFAULT);

        double[] parametros = lerParametros();
        double[] valoresIniciais = lerValoresIniciais();



        numeroDeDias = (int) pedirValorComUmPrint(NUM_DIA_MIN, NUM_DIA_MAX, PEDIR_DIAS);
        double h = pedirValorComUmPrint(LIMITE_INF_PASSO,LIMITE_SUP_PASSO, PEDIR_PASSO);
        nomeFicheiro = criarNomeParaFicheiro();

        double[] S = new double[numeroDeDias];
        double[] I = new double[numeroDeDias];
        double[] R = new double[numeroDeDias];

        int numExcMet = (int) pedirValorComUmPrint(LIMITE_INF_PASSO,NUM_METODOS,PEDIR_METODO);
        executarMetodo(numExcMet, S, I, R, h, numeroDeDias, valoresIniciais, parametros);

        escreverResultadosEmFicheiro(S, I, R, numeroDeDias,nomeFicheiro);
    }
    private static String obterValorArgumento(String[] args, String flag, String valorPadrao) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return valorPadrao;
    }

    private static void exibirMensagemAjuda() {
        System.out.println("SIR - Modelo Epidemiológico");
        System.out.println("Sintaxe: java -jar seu_programa.jar [opções]");
        System.out.println("Opções:");
        System.out.println("  -b <arquivo>   Ficheiro de parâmetros (default: params_exemplo1.csv)");
        System.out.println("  -c <arquivo>   Ficheiro de condições iniciais (default: estado_inicial.csv)");
        System.out.println("  -m <metodo>    Método a usar (1-Euler ou 2-Runge Kutta de 4ª ordem) (default: 1)");
        System.out.println("  -p <passo>     Passo de integração h (maior que zero e menor ou igual a um) (default: 0.1)");
        System.out.println("  -d <dias>      Número de dias a considerar para análise (maior que zero) (default: 5)");
        System.out.println("  -f <arquivo>   Nome do ficheiro de saída CSV (default: resultados.csv)");
        System.out.println("  -h, --help     Exibir esta mensagem de ajuda");
    }

    public static void executarMetodo(int num,double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros ) {
        if (num == 1) {
            aplicarEuler(S, I, R, h, numeroDeDias, valoresIniciais, parametros);
        } else if (num == 2) {
            aplicarRK4(S, I, R,h, numeroDeDias, valoresIniciais, parametros);
        }
    }

    public static String criarNomeParaFicheiro(){
        System.out.print("Coloque o nome para o ficheiro dos resultados [ex:results] :");
        String nome;
        ler.nextLine();
        nome = ler.nextLine();
        return nome;
    }
    public static double pedirValorComUmPrint(int min, int max, String inform){
        double num;
        System.out.print(inform);
        do {
            num = ler.nextDouble();
            if (num < min || num > max){
                System.out.print("ERRO: O valor introduzido é inválido.\nIntroduza um valor entre: ["+min+","+max+"]");
            }
        } while (num < min || num > max);

        return num;
    }

    public static double[] lerValoresIniciais() throws FileNotFoundException {
        Scanner ler = new Scanner(new File(VALORES_INICIAIS));
        ler.nextLine();
        String[] valores = ler.nextLine().split(";");
        double[] valoresIniciais = new double[valores.length];
        for (int i = 0; i < valores.length; i++) {
            valoresIniciais[i] = Double.parseDouble(valores[i].replace(',', '.'));
        }
        ler.close();
        return valoresIniciais;
    }

    public static double[] lerParametros() throws FileNotFoundException {
        double[] parametros = new double[7];
        Scanner ler = new Scanner(new File(PARAMETROS));
        ler.nextLine();
        String[] parametro = ler.nextLine().split(";");
        for (int i = 1; i < parametro.length; i++) {
            parametros[i - 1] = Double.parseDouble(parametro[i].replace(',', '.'));
        }
        ler.close();
        return parametros;
    }

    public static double fS(int dia, double[] S, double[] I, double[] parametros) {
        return (parametros[1] - (parametros[4] * S[dia - 1] * I[dia]) - (parametros[1] * S[dia]));
    }

    public static double fI(int dia, double[] S, double[] I, double[] R, double[] parametros) {
        return (parametros[4] * S[dia] * I[dia] - parametros[2] * I[dia] + parametros[3] * I[dia] * R[dia] - (parametros[1] + parametros[5]) * I[dia]);
    }

    public static double fR(int dia, double[] I, double[] R, double[] parametros) {
        return (parametros[2] * I[dia] - parametros[3] * I[dia] * R[dia] - (parametros[1] + parametros[6]) * R[dia]);
    }

    public static void aplicarEuler(double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros) {
        S[0] = valoresIniciais[0];
        I[0] = valoresIniciais[1];
        R[0] = valoresIniciais[2];
        for (int dia = 1; dia < numeroDeDias; dia++) {
            double dS = h * fS(dia, S, I, parametros);
            double dI = h * fI(dia, S, I, R, parametros);
            double dR = h * fR(dia, I, R, parametros);
            S[dia] = S[dia - 1] + dS;
            I[dia] = I[dia - 1] + dI;
            R[dia] = R[dia - 1] + dR;
        }
    }

    public static void aplicarRK4(double[] S, double[] I, double[] R, double h, int numeroDeDias, double[] valoresIniciais, double[] parametros) {
        S[0] = valoresIniciais[0];
        I[0] = valoresIniciais[1];
        R[0] = valoresIniciais[2];

        for (int dia = 1; dia < numeroDeDias; dia++) {
            double k1S = fS(dia, S, I, parametros);
            double k1I = fI(dia, S, I, R, parametros);
            double k1R = fR(dia, I, R, parametros);

            double k2S = h * fS(dia, new double[]{S[dia - 1] + 0.5 * k1S},new double[]{I[dia-1] + 0},parametros); //(parametros[0] - parametros[4] * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - parametros[1] * (S[dia - 1] + h / 2 * k1S));
            double k2I = h * (parametros[4] * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - parametros[2] * (I[dia - 1] + h / 2 * k1I) + parametros[3] * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (parametros[1] + parametros[5]) * (I[dia - 1] + h / 2 * k1I));
            double k2R = h * (parametros[2] * (I[dia - 1] + h / 2 * k1I) - parametros[3] * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (parametros[1] + parametros[6]) * (R[dia - 1] + h / 2 * k1R));

            double k3S = h * (parametros[0] - parametros[4] * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - parametros[1] * (S[dia - 1] + h / 2 * k2S));
            double k3I = h * (parametros[4] * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - parametros[2] * (I[dia - 1] + h / 2 * k2I) + parametros[3] * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (parametros[1] + parametros[5]) * (I[dia - 1] + h / 2 * k2I));
            double k3R = h * (parametros[2] * (I[dia - 1] + h / 2 * k2I) - parametros[3] * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (parametros[1] + parametros[6]) * (R[dia - 1] + h / 2 * k2R));

            double k4S = h * (parametros[0] - parametros[4] * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - parametros[1] * (S[dia - 1] + h * k3S));
            double k4I = h * (parametros[4] * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - parametros[2] * (I[dia - 1] + h * k3I) + parametros[3] * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (parametros[1] + parametros[5]) * (I[dia - 1] + h * k3I));
            double k4R = h * (parametros[2] * (I[dia - 1] + h * k3I) - parametros[3] * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (parametros[1] + parametros[6]) * (R[dia - 1] + h * k3R));

            double kS = (k1S + 2 * k2S + 2 * k3S + k4S) / 6;
            double kI = (k1I + 2 * k2I + 2 * k3I + k4I) / 6;
            double kR = (k1R + 2 * k2R + 2 * k3R + k4R) / 6;

            S[dia] = S[dia - 1] + kS;
            I[dia] = I[dia - 1] + kI;
            R[dia] = R[dia - 1] + kR;
        }
    }

    public static void escreverResultadosEmFicheiro(double[] S, double[] I, double[] R, int numeroDeDias, String nomeDoFicheiro) throws FileNotFoundException {
        PrintWriter escrever = new PrintWriter(nomeDoFicheiro+FORMAT);
        escrever.print("Dia       S               I               R               T          \n");
        for (int dia = 0; dia < numeroDeDias; dia++) {
            double total = S[dia] + I[dia] + R[dia];
            escrever.printf("%d\t%12.4f\t%12.4f\t%12.4f\t%12.4f%n", dia, S[dia], I[dia], R[dia], total);
        }
        escrever.close();
    }
}