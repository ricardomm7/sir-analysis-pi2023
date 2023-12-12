import java.util.Scanner;

public class SIR {
    static double LIM_MIN = 0.0;
    static double LIM_MAX = 0.5;
    static Scanner ler = new Scanner(System.in);


    public static void main(String[] args) {
        double[] valoresIniciais = lerValoresIniciais();
        double[] parametros = lerParametros();

        double[] resultadoEuler = aplicarMetodoDeEuler(valoresIniciais, parametros);
        double resultadoRunge = aplicarMetodoDeRunge_Kutta(valoresIniciais[1], valoresIniciais[2], valoresIniciais[3], valoresIniciais[4], valoresIniciais[5]);

        System.out.println("Resultado final (y_n) pelo Metodo de Euler: " + resultadoEuler);
        System.out.println("Resultado final (y_n) pelo Método de Runge-Kutta: " + resultadoRunge);

    }

    public static double[] lerValoresIniciais() {
        double[] valoresIniciais = new double[4];
        System.out.print("Informe o valor inicial de  t0: ");
        valoresIniciais[0] = ler.nextDouble();
        System.out.print("Informe o valor inicial de S(t0): ");
        valoresIniciais[1]  = ler.nextDouble();
        System.out.print("Informe o valor inicial de I(t0): ");
        valoresIniciais[2]  = ler.nextDouble();
        System.out.print("Informe o valor inicial de R(t0): ");
        valoresIniciais[3]  = ler.nextDouble();
        System.out.print("Informe o tamanho do passo h: ");
        valoresIniciais[4]  = ler.nextDouble();

        return valoresIniciais;

    }
    public static double[] lerParametros(){
        double[] parametros = new double[6];
        System.out.print("Informe o valor do parametro λ: ");
        parametros[0]  = ler.nextDouble();
        System.out.print("Informe o valor do parametro b: ");
        parametros[1]  = ler.nextDouble();
        System.out.print("Informe o valor do parametro k: ");
        parametros[2] = ler.nextDouble();
        System.out.print("Informe o valor do parametro β: ");
        parametros[3] = ler.nextDouble();
        System.out.print("Informe o valor do parametro µ: ");
        parametros[4] = ler.nextDouble();
        System.out.print("Informe o valor do parametro δ1: ");
        parametros[5]= ler.nextDouble();
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

    public static double fR( double I, double R, double k, double β, double µ, double δ2) {
        return k * I - β * I * R - (µ + δ2) * R;
    }
        public static double[] aplicarMetodoDeEuler (double [] valoresIniciais, double [] parametros){
            double n = (int) (LIM_MIN - LIM_MAX) / valoresIniciais[4];
            double S0 = valoresIniciais[1];
            double Sn = S0;
            double I0 = valoresIniciais[2];
            double In = I0;
            double R0 = valoresIniciais[3];
            double Rn = R0;
            double h = valoresIniciais[4];

            double λ = parametros [0];
            double b = parametros [1];
            double k = parametros [2];
            double β = parametros [3];
            double µ = parametros [4];
            double δ1 = parametros [5];
            double δ2 = parametros [6];

            for (int i = 0; i < n; i++) {
                Sn = Sn + h * fS(Sn, In, λ, b, µ);
                S0 = Sn;
                In = In + h * fI(Sn, In, Rn, b, k, β, µ, δ1);
                I0 = In;
                Rn = Rn + h * fR(In, Rn, k, β, µ, δ2);
                R0 = Rn;
            }

            double[] resultados = {Sn, In, Rn};
            return resultados;


        }

        public static double aplicarMetodoDeRunge_Kutta ( double t0, double S0, double I0, double R0, double h){
            double n = (int) (LIM_MIN - LIM_MAX) / h;
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
        }
    }
