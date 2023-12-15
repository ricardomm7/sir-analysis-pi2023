set terminal png size 1000,600  # ou as dimensões desejadas

# Defina o arquivo de saída
set output 'export_visual_graph.png'

set xlabel 'Dias'
set ylabel 'Taxas'
set title 'Predicção SIR'

# Plote os dados com linhas mais grossas
plot 'dataS.dat' using 1:2 with linespoints linewidth 3 title 'Suscetibilidade', \
     'dataI.dat' using 1:2 with linespoints linewidth 3 title 'Infetados', \
     'dataR.dat' using 1:2 with linespoints linewidth 3 title 'Recuperados'
