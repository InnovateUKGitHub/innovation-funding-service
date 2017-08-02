
set terminal jpeg

set output "graphs/NAME.jpg"

set title "Benchmark testing - URL"

set key left top

# nicer aspect ratio for image size
set size 1,0.7

# y-axis grid
set grid y

# x-axis label
set xlabel "request"

# y-axis label
set ylabel "response time (ms)"

set datafile separator '\t'
set style fill solid border
set boxwidth 8 absolute
set yrange [0:*]
bin(x) = 10*floor(x/10.0)

plot "data/NAME.tsv" using (bin($5)):(1) every ::1 smooth frequency with boxes title 'ttime'
exit
