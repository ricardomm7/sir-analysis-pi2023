import java.io.*;
import java.util.Scanner;

public class SIR {
    static final String VALORES_INICIAIS = "estado_inicial.csv";
    static final String PARAMETROS = "params_exemplo1.csv";

    //Informações a pedir ao utilizador acho eu
    static int numeroDeDias = 5;
    static double h = 1.0;
    static Scanner ler = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        double[] valoresIniciais = lerValoresIniciais();
        double[] parametros = lerParametros();

        double[] S = new double[numeroDeDias];
        double[] I = new double[numeroDeDias];
        double[] R = new double[numeroDeDias];

        System.out.print("Digite (1) caso queira aplicar o método de Euler\nDigite (2) caso queira aplicar o método de Runge-Kutta de quarta ordem\n");
        int num = ler.nextInt();
        if (num == 1) {
            aplicarEuler(S, I, R, valoresIniciais, parametros);
        } else if (num == 2) {
            aplicarRK4(S, I, R, valoresIniciais, parametros);
        }
        System.out.print("Digite qual o passo de integração que deseja: ");

        escreverResultadosEmFicheiro(S, I, R);
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


        for (int i = 0; i < parametro.length; i++) {
            parametros[i] = Double.parseDouble(parametro[i].replace(',', '.'));
        }

        ler.close();

        return parametros;
    }


    public static double fS(int dia, double[] S, double[] I, double[] parametros) {
        return (parametros[1] - (parametros[4] * S[dia - 1] * I[dia - 1]) - (parametros[1] * S[dia - 1]));
    }

    public static double fI(int dia, double[] S, double[] I, double[] R, double[] parametros) {
        return (parametros[4] * S[dia - 1] * I[dia - 1] - parametros[2] * I[dia - 1] + parametros[3] * I[dia - 1] * R[dia - 1] - (parametros[1] + parametros[5]) * I[dia - 1]);
    }

    public static double fR(int dia, double[] I, double[] R, double[] parametros) {
        return (parametros[2] * I[dia - 1] - parametros[3] * I[dia - 1] * R[dia - 1] - (parametros[1] + parametros[6]) * R[dia - 1]);
    }


    public static void aplicarEuler(double[] S, double[] I, double[] R, double[] valoresIniciais, double[] parametros) {
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

    public static void aplicarRK4(double[] S, double[] I, double[] R, double[] valoresIniciais, double[] parametros) {
        S[0] = valoresIniciais[0];
        I[0] = valoresIniciais[1];
        R[0] = valoresIniciais[2];

        for (int dia = 1; dia < numeroDeDias; dia++) {

            double k1S = fS(dia, S, I, parametros);
            double k1I = fI(dia, S, I, R, parametros);
            double k1R = fR(dia, I, R, parametros);

            double k2S = h * (parametros[0] - parametros[4] * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - parametros[1] * (S[dia - 1] + h / 2 * k1S));
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


    public static void escreverResultadosEmFicheiro(double[] S, double[] I, double[] R) throws FileNotFoundException {
        PrintWriter out = new PrintWriter("resultados.txt");

        out.print("Dia       S               I               R               T          \n");

        for (int dia = 0; dia < numeroDeDias; dia++) {
            double total = S[dia] + I[dia] + R[dia];
            out.printf("%d\t%12.4f\t%12.4f\t%12.4f\t%12.4f%n", dia, S[dia], I[dia], R[dia], total);

        }
        out.close();
    }
}