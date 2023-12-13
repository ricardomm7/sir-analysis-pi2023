import java.io.*;
import java.util.Scanner;

public class SIR {
    static final String VALORES_INICIAIS = "estado_inicial.csv";
    static final String PARAMETROS = "params_exemplo1.csv";
    // Parametros
    static double lambda = 0.04;
    static double b = 0.02;
    static double k = 0.02;
    static double beta = 0.03;
    static double u = 0.04;
    static double delta1 = 0;
    static double delta2 = 0;
    //Valores Iniciais
    static double S0 = 0.5;
    static double I0 = 0.3;
    static double R0 = 0.2;

    //Informações a pedir ao utilizador acho eu
    static int numeroDeDias = 5;
    static double h = 1.0;
    static Scanner ler = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        double[] S = new double[numeroDeDias];
        double[] I = new double[numeroDeDias];
        double[] R = new double[numeroDeDias];


        System.out.print("Digite (1) caso queira aplicar o método de Euler\nDigite (2) caso queira aplicar o método de Runge-Kutta de quarta ordem\n");
        int num = ler.nextInt();
        if (num == 1) {
            aplicarEuler(S, I, R);
        } else if (num == 2) {
            aplicarRK4(S, I, R);
        }

        escreverResultadosEmFicheiro(S, I, R);


        /*double[] valoresIniciais = lerValoresIniciais();
        double[] parametros = lerParametros();
       */
    }


    public static double[] lerValoresIniciais() throws FileNotFoundException {
        Scanner ler = new Scanner(new File(VALORES_INICIAIS));
        ler.nextLine();
                
        double[] valoresIniciais = new double[3];
        valoresIniciais[0] = ler.nextDouble();
        valoresIniciais[1] = ler.nextDouble();
        valoresIniciais[2] = ler.nextDouble();
        
        ler.close();

        return valoresIniciais;
    }

    public static double[] lerParametros() throws FileNotFoundException {
        double[] parametros = new double[6];
        Scanner ler = new Scanner(new File(PARAMETROS));
        ler.nextLine();
        ler.nextDouble();
        
        parametros[0] = ler.nextDouble();
        parametros[1] = ler.nextDouble();
        parametros[2] = ler.nextDouble();
        parametros[3] = ler.nextDouble();
        parametros[4] = ler.nextDouble();
        parametros[5] = ler.nextDouble();

        ler.close();

        return parametros;
    }


    public static double fS(int dia, double[] S, double[] I) {
        return (lambda - (b * S[dia - 1] * I[dia - 1]) - (u * S[dia - 1]));
    }

    public static double fI(int dia, double[] S, double[] I, double[] R) {
        return (b * S[dia - 1] * I[dia - 1] - k * I[dia - 1] + beta * I[dia - 1] * R[dia - 1] - (u + delta1) * I[dia - 1]);
    }

    public static double fR(int dia, double[] I, double[] R) {
        return (k * I[dia - 1] - beta * I[dia - 1] * R[dia - 1] - (u + delta2) * R[dia - 1]);
    }


    public static void aplicarEuler(double[] S, double[] I, double[] R) {
        S[0] = S0;
        I[0] = I0;
        R[0] = R0;
        for (int dia = 1; dia < numeroDeDias; dia++) {
            double dS = h * fS(dia, S, I);
            double dI = h * fI(dia, S, I, R);
            double dR = h * fR(dia, I, R);

            S[dia] = S[dia - 1] + dS;
            I[dia] = I[dia - 1] + dI;
            R[dia] = R[dia - 1] + dR;
        }
    }

    public static void aplicarRK4(double[] S, double[] I, double[] R) {
        S[0] = S0;
        I[0] = I0;
        R[0] = R0;

        for (int dia = 1; dia < numeroDeDias; dia++) {

            double k1S = fS(dia, S, I);
            double k1I = fI(dia, S, I, R);
            double k1R = fR(dia, I, R);

            double k2S = h * (lambda - b * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - u * (S[dia - 1] + h / 2 * k1S));
            double k2I = h * (b * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - k * (I[dia - 1] + h / 2 * k1I) + beta * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (u + delta1) * (I[dia - 1] + h / 2 * k1I));
            double k2R = h * (k * (I[dia - 1] + h / 2 * k1I) - beta * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (u + delta2) * (R[dia - 1] + h / 2 * k1R));

            double k3S = h * (lambda - b * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - u * (S[dia - 1] + h / 2 * k2S));
            double k3I = h * (b * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - k * (I[dia - 1] + h / 2 * k2I) + beta * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (u + delta1) * (I[dia - 1] + h / 2 * k2I));
            double k3R = h * (k * (I[dia - 1] + h / 2 * k2I) - beta * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (u + delta2) * (R[dia - 1] + h / 2 * k2R));

            double k4S = h * (lambda - b * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - u * (S[dia - 1] + h * k3S));
            double k4I = h * (b * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - k * (I[dia - 1] + h * k3I) + beta * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (u + delta1) * (I[dia - 1] + h * k3I));
            double k4R = h * (k * (I[dia - 1] + h * k3I) - beta * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (u + delta2) * (R[dia - 1] + h * k3R));

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