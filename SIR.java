import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SIR {
    static double λ = 0.04;
    static double b = 0.02;
    static double k = 0.02;
    static double β = 0.03;
    static double µ = 0.04;
    static double δ1 = 0;
    static double δ2 = 0;
    static double S0 = 0.5;
    static double I0 = 0.3;
    static double R0 = 0.2;
    static int numeroDeDias = 5;
    static double h = 1.0;
    static Scanner ler = new Scanner(System.in);


    public static void main(String[] args) throws IOException {
        double[] S = new double[numeroDeDias];
        double[] I = new double[numeroDeDias];
        double[] R = new double[numeroDeDias];
        S[0] = S0;
        I[0] = I0;
        R[0] = R0;
        aplicarEuler(S, I, R);
        aplicarRK4(S, I, R);

        escreverResoltadosEmFicheiro(S, I, R);


        /*double[] valoresIniciais = lerValoresIniciais();
        double[] parametros = lerParametros();

        double[] resultadoEuler = aplicarMetodoDeEuler(valoresIniciais, parametros);
        double resultadoRunge = aplicarMetodoDeRunge_Kutta(valoresIniciais[1], valoresIniciais[2], valoresIniciais[3], valoresIniciais[4], valoresIniciais[5]);

        System.out.println("Resultado final (y_n) pelo Metodo de Euler: " + resultadoEuler);
        System.out.println("Resultado final (y_n) pelo Método de Runge-Kutta: " + resultadoRunge);*/
    }

    public static void escreverResoltadosEmFicheiro(double[] S, double[] I, double[] R) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados.txt"))) {
            writer.printf("Dia\tS\tI\tR\tT\n");

            for (int dia = 0; dia < numeroDeDias; dia++) {

                double total = S[dia] + I[dia] + R[dia];
                writer.printf("%d\t%.6f\t%.6f\t%.6f\t%.6f%n", dia, S[dia], I[dia], R[dia], total);

            }
        }
    }

    /*
    public static double[] lerValoresIniciais() {
        double[] valoresIniciais = new double[6];
        System.out.print("Informe o valor inicial de  t0: ");
        valoresIniciais[0] = ler.nextDouble();
        System.out.print("Informe o valor inicial de S(t0): ");
        valoresIniciais[1] = ler.nextDouble();
        System.out.print("Informe o valor inicial de I(t0): ");
        valoresIniciais[2] = ler.nextDouble();
        System.out.print("Informe o valor inicial de R(t0): ");
        valoresIniciais[3] = ler.nextDouble();
        System.out.print("Informe o tamanho do passo h: ");
        valoresIniciais[4] = ler.nextDouble();
        System.out.print("Informe o número de dias: ");
        valoresIniciais[5] = ler.nextDouble();

        return valoresIniciais;
    }

    public static double[] lerParametros() {
        double[] parametros = new double[7];
        System.out.print("Informe o valor do parametro λ: ");
        parametros[0] = ler.nextDouble();
        System.out.print("Informe o valor do parametro b: ");
        parametros[1] = ler.nextDouble();
        System.out.print("Informe o valor do parametro k: ");
        parametros[2] = ler.nextDouble();
        System.out.print("Informe o valor do parametro β: ");
        parametros[3] = ler.nextDouble();
        System.out.print("Informe o valor do parametro µ: ");
        parametros[4] = ler.nextDouble();
        System.out.print("Informe o valor do parametro δ1: ");
        parametros[5] = ler.nextDouble();
        System.out.print("Informe o valor do parametro δ2: ");
        parametros[6] = ler.nextDouble();

        return parametros;
    }


    public static double fS(double S, double I, double λ, double b, double µ) {
        return λ - (b * S * I) - (µ * S);
    }

    public static double fI(double S, double I, double R, double b, double k, double β, double µ, double δ1) {
        return b * S * I - k * I + β * I * R - (µ + δ1) * I;
    }

    public static double fR(double I, double R, double k, double β, double µ, double δ2) {
        return k * I - β * I * R - (µ + δ2) * R;
    }
    /*
     */

    public static void aplicarEuler(double []S, double[]I, double[]R){
        for (int dia = 0; dia < numeroDeDias; dia++) {
            double dS = λ - (b * S[dia] * I[dia]) - (µ * S[dia]);
            double dI = b * S[dia] * I[dia] - k * I[dia] + β * I[dia] * R[dia] - (µ + δ1) * I[dia];
            double dR = k * I[dia] - β * I[dia] * R[dia] - (µ + δ2) * R[dia];
            
            S[dia] = S[dia] + h * dS;
            I[dia] = I[dia] + h * dI;
            R[dia] = R[dia] + h * dR;

        }
    }
    
    public static void aplicarRK4(double[]S, double[]I, double[]R){
        for (int dia = 0; dia < numeroDeDias; dia++) {

            double k1S = h *(λ - b * S[dia] * I[dia] - µ * S[dia]);
            double k1I = h *(b * S[dia] * I[dia] - k * I[dia] + β * I[dia] * R[dia] - (µ + δ1) * I[dia]);
            double k1R = h *(k * I[dia] - β * I[dia] * R[dia] - (µ + δ2) * R[dia]);

            double k2S = h *(λ - b * (S[dia] + h / 2 * k1S) * (I[dia] + h / 2 * k1I) - µ * (S[dia] + h / 2 * k1S));
            double k2I = h *(b * (S[dia] + h / 2 * k1S) * (I[dia] + h / 2 * k1I) - k * (I[dia] + h / 2 * k1I) + β * (I[dia] + h / 2 * k1I) * (R[dia] + h / 2 * k1R) - (µ + δ1) * (I[dia] + h / 2 * k1I));
            double k2R = h *(k * (I[dia] + h / 2 * k1I) - β * (I[dia] + h / 2 * k1I) * (R[dia] + h / 2 * k1R) - (µ + δ2) * (R[dia] + h / 2 * k1R));

            double k3S = h *(λ - b * (S[dia] + h / 2 * k2S) * (I[dia] + h / 2 * k2I) - µ * (S[dia] + h / 2 * k2S));
            double k3I = h *(b * (S[dia] + h / 2 * k2S) * (I[dia] + h / 2 * k2I) - k * (I[dia] + h / 2 * k2I) + β * (I[dia] + h / 2 * k2I) * (R[dia] + h / 2 * k2R) - (µ + δ1) * (I[dia] + h / 2 * k2I));
            double k3R = h *(k * (I[dia] + h / 2 * k2I) - β * (I[dia] + h / 2 * k2I) * (R[dia] + h / 2 * k2R) - (µ + δ2) * (R[dia] + h / 2 * k2R));

            double k4S = h *(λ - b * (S[dia] + h * k3S) * (I[dia] + h * k3I) - µ * (S[dia] + h * k3S));
            double k4I = h *(b * (S[dia] + h * k3S) * (I[dia] + h * k3I) - k * (I[dia] + h * k3I) + β * (I[dia] + h * k3I) * (R[dia] + h * k3R) - (µ + δ1) * (I[dia] + h * k3I));
            double k4R = h *(k * (I[dia] + h * k3I) - β * (I[dia] + h * k3I) * (R[dia] + h * k3R) - (µ + δ2) * (R[dia] + h * k3R));

            double kS = (k1S + 2 * k2S + 2 * k3S + k4S) / 6;
            double kI = (k1I + 2 * k2I + 2 * k3I + k4I) / 6;
            double kR = (k1R + 2 * k2R + 2 * k3R + k4R) / 6;

            S[dia] = S[dia] + kS;
            I[dia] = I[dia] + kI;
            R[dia] = R[dia] + kR;
            
        }
    }/*
    public static double[] aplicarMetodoDeEuler(double[] valoresIniciais, double[] parametros) {
        double n = (numeroDeDias) / h;
        double Sn = S0;
        double In = I0;
        double Rn = R0;


        for (int i = 0; i < n; i++) {
            Sn = S0 + h * fS(Sn, In, λ, b, µ);
            S0 = Sn;
            In = I0 + h * fI(Sn, In, Rn, b, k, β, µ, δ1);
            I0 = In;
            Rn = R0 + h * fR(In, Rn, k, β, µ, δ2);
            R0 = Rn;
        }

        double[] resultados = {Sn, In, Rn};
        return resultados;
    }*

    public static double aplicarMetodoDeRunge_Kutta(double t0, double S0, double I0, double R0, double h) {
        double n = (numeroDeDias) / h;
        double yn = -1;

        for (int i = 0; i < n; i++) {
            double k1 = h * f(x0, y0);
            double k2 = h * f(x0 + h / 2, y0 + k1 / 2);
            double k3 = h * f(x0 + h / 2, y0 + k2 / 2);
            double k4 = h * f(x0 + h, y0 + k3);

            double k = (k1 + 2 * k2 + 2 * k3 + k4) / 6;

            yn = y0 + k;
            x0 = x0 + h;
            y0 = yn;
        }
        return yn;
    }*/
}