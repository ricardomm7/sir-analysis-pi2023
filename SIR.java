import java.util.Scanner;

public class SIR {
    static double LIM_MIN = 0.0; static double LIM_MAX = 0.5;
    static Scanner ler = new Scanner(System.in);


    public static void main(String[] args) {

        System.out.print("Informe o valor inicial de x (x0): ");
        double x0 = ler.nextDouble();
        System.out.print("Informe o valor inicial de y (y0): ");
        double y0 = ler.nextDouble();
        System.out.print("Informe o tamanho do passo (h): ");
        double h = ler.nextDouble();

        double resultadoEuler = aplicarMetodoDeEuler(x0, y0,h);
        double resultadoRunge = aplicarMetodoDeRunge_Kutta(x0, y0, h);

        System.out.println("Resultado final (y_n) pelo Metodo de Euler: " + resultadoEuler);
        System.out.println("Resultado final (y_n) pelo Método de Runge-Kutta: " + resultadoRunge);

    }
    public static double f(double x, double y) {
        return -2 * x - y;
    }
    public static double aplicarMetodoDeEuler(double x0, double y0, double h){
        double n = (int)(LIM_MIN - LIM_MAX) / h;
        double xn = x0;
        double yn = y0;

        for (int i = 0; i < n; i++) {
            yn = yn + h * f(xn, yn);
            xn = xn + h;

        }
        return yn;

    }
    public static double aplicarMetodoDeRunge_Kutta(double x0, double y0, double h){
        double n = (int)(LIM_MIN - LIM_MAX) / h;
        double xn = x0;
        double yn = y0;

        for (int i = 0; i < n; i++) {
            double k1 = h * f(xn, yn);
            double k2 = h * f(xn + h/2, yn + k1/2);
            double k3 = h * f(xn + h/2, yn + k2/2);
            double k4 = h * f(xn + h, yn + k3);

            double k = (k1 + 2*k2 + 2*k3 + k4) / 6;

            yn = yn + k;
            xn = xn + h;
        }
        return yn;
    }
}
