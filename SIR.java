import java.io.*;
import java.util.Scanner;

public class SIR {
    // Parametros
    static double λ = 0.04;
    static double b = 0.02;
    static double k = 0.02;
    static double β = 0.03;
    static double µ = 0.04;
    static double δ1 = 0;
    static double δ2 = 0;
    //Valores Iniciais
    static double S0 = 0.5;
    static double I0 = 0.3;
    static double R0 = 0.2;
    //Informações a pedir ao utilizador acho eu
    static int numeroDeDias = 5;
    static double h = 1.0;
    static Scanner ler = new Scanner(System.in);


    public static void main(String[] args) throws FileNotFoundException  {
        double[] S = new double[numeroDeDias];
        double[] I = new double[numeroDeDias];
        double[] R = new double[numeroDeDias];

        S[0] = S0;
        I[0] = I0;
        R[0] = R0;

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

        double[] resultadoEuler = aplicarMetodoDeEuler(valoresIniciais, parametros);
        double resultadoRunge = aplicarMetodoDeRunge_Kutta(valoresIniciais[1], valoresIniciais[2], valoresIniciais[3], valoresIniciais[4], valoresIniciais[5]);

        System.out.println("Resultado final (y_n) pelo Metodo de Euler: " + resultadoEuler);
        System.out.println("Resultado final (y_n) pelo Método de Runge-Kutta: " + resultadoRunge);*/
    }


    public static void escreverResultadosEmFicheiro(double[] S, double[] I, double[] R) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File("resultados.txt"));

        out.print("Dia       S               I               R               T          \n");

        for (int dia = 0; dia < numeroDeDias; dia++) {
            double total = S[dia] + I[dia] + R[dia];
            out.printf("%d\t%12.4f\t%12.4f\t%12.4f\t%12.4f%n", dia, S[dia], I[dia], R[dia], total);

        }
        out.close();
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
    }*/


    public static double fS(int dia, double[] S, double[] I) {
        return (λ - (b * S[dia - 1] * I[dia - 1]) - (µ * S[dia - 1]));
    }

    public static double fI(int dia, double[] S, double[] I, double[] R) {
        return (b * S[dia - 1] * I[dia - 1] - k * I[dia - 1] + β * I[dia - 1] * R[dia - 1] - (µ + δ1) * I[dia - 1]);
    }

    public static double fR(int dia, double[] I, double[] R) {
        return (k * I[dia - 1] - β * I[dia - 1] * R[dia - 1] - (µ + δ2) * R[dia - 1]);
    }


    public static void aplicarEuler(double[] S, double[] I, double[] R) {
        for (int dia = 1; dia < numeroDeDias; dia++) {
            /*double dS = h * (λ - (b * S[dia-1] * I[dia-1]) - (µ * S[dia-1]));
            double dI = h * (b * S[dia-1] * I[dia-1] - k * I[dia-1] + β * I[dia-1] * R[dia-1] - (µ + δ1) * I[dia-1]);
            double dR = h * (k * I[dia-1] - β * I[dia-1] * R[dia-1] - (µ + δ2) * R[dia-1]);*/
            double dS = h * fS(dia, S, I);
            double dI = h * fI(dia, S, I, R);
            double dR = h * fR(dia, I, R);

            S[dia] = S[dia - 1] + dS;
            I[dia] = I[dia - 1] + dI;
            R[dia] = R[dia - 1] + dR;


        }
    }

    public static void aplicarRK4(double[] S, double[] I, double[] R) {
        for (int dia = 1; dia < numeroDeDias; dia++) {

            double k1S = fS(dia, S, I); // h *(λ - b * S[dia-1] * I[dia-1] - µ * S[dia-1]);
            double k1I = fI(dia, S, I, R); // h *(b * S[dia-1] * I[dia-1] - k * I[dia-1] + β * I[dia-1] * R[dia-1] - (µ + δ1) * I[dia-1]);
            double k1R = fR(dia, I, R); // h *(k * I[dia-1] - β * I[dia-1] * R[dia-1] - (µ + δ2) * R[dia-1]);

            double k2S = h * (λ - b * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - µ * (S[dia - 1] + h / 2 * k1S));
            double k2I = h * (b * (S[dia - 1] + h / 2 * k1S) * (I[dia - 1] + h / 2 * k1I) - k * (I[dia - 1] + h / 2 * k1I) + β * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (µ + δ1) * (I[dia - 1] + h / 2 * k1I));
            double k2R = h * (k * (I[dia - 1] + h / 2 * k1I) - β * (I[dia - 1] + h / 2 * k1I) * (R[dia - 1] + h / 2 * k1R) - (µ + δ2) * (R[dia - 1] + h / 2 * k1R));

            double k3S = h * (λ - b * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - µ * (S[dia - 1] + h / 2 * k2S));
            double k3I = h * (b * (S[dia - 1] + h / 2 * k2S) * (I[dia - 1] + h / 2 * k2I) - k * (I[dia - 1] + h / 2 * k2I) + β * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (µ + δ1) * (I[dia - 1] + h / 2 * k2I));
            double k3R = h * (k * (I[dia - 1] + h / 2 * k2I) - β * (I[dia - 1] + h / 2 * k2I) * (R[dia - 1] + h / 2 * k2R) - (µ + δ2) * (R[dia - 1] + h / 2 * k2R));

            double k4S = h * (λ - b * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - µ * (S[dia - 1] + h * k3S));
            double k4I = h * (b * (S[dia - 1] + h * k3S) * (I[dia - 1] + h * k3I) - k * (I[dia - 1] + h * k3I) + β * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (µ + δ1) * (I[dia - 1] + h * k3I));
            double k4R = h * (k * (I[dia - 1] + h * k3I) - β * (I[dia - 1] + h * k3I) * (R[dia - 1] + h * k3R) - (µ + δ2) * (R[dia - 1] + h * k3R));

            double kS = (k1S + 2 * k2S + 2 * k3S + k4S) / 6;
            double kI = (k1I + 2 * k2I + 2 * k3I + k4I) / 6;
            double kR = (k1R + 2 * k2R + 2 * k3R + k4R) / 6;

            S[dia] = S[dia - 1] + kS;
            I[dia] = I[dia - 1] + kI;
            R[dia] = R[dia - 1] + kR;

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