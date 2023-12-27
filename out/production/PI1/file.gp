set terminal pngcairo size 1000,750 enhanced font 'Verdana,12'
set output 'multiplot_graficos.png'

set multiplot layout 2,1


set title 'Caso 1'
set xlabel 'Dias'
set ylabel 'Taxas'
set origin 0.0,0.5
set size 1.0/1,0.5
plot 'dataS1.dat' with linespoints linewidth 3 title 'Suscetibilidade',\
'dataI1.dat' with linespoints linewidth 3 title 'Infetados',\
'dataR1.dat' with linespoints linewidth 3 title 'Recuperados'

set title 'Caso 2'
set xlabel 'Dias'
set ylabel 'Taxas'
set origin 0.0,0.0
set size 1.0/1,0.5
plot 'dataS2.dat' with linespoints linewidth 3 title 'Suscetibilidade',\
'dataI2.dat' with linespoints linewidth 3 title 'Infetados',\
'dataR2.dat' with linespoints linewidth 3 title 'Recuperados'

