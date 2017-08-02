
set terminal png

set output "graphs/NAME.png"

set title "Benchmark testing - NAME"

set size 1,0.7

# y-axis grid
set grid y

# x-axis label
set xlabel "percentile"

# y-axis label
set ylabel "response time (ms)"

set datafile separator ","

plot "data/NAME.csv" with lines title "server"
exit
